/*
 * Copyright (c) 2020 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.ti.cardreader.provider.pcsc.control;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.AbstractCardReaderController;
import de.gematik.ti.cardreader.provider.api.ICardReader;
import de.gematik.ti.cardreader.provider.api.listener.InitializationStatus;
import de.gematik.ti.cardreader.provider.pcsc.entities.CardReader;

/**
 * include::{userguide}/PCSCCRP_Overview.adoc[tag=PCSCCardReaderController]
 *
 */
final class PcScCardReaderController extends AbstractCardReaderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PcScCardReaderController.class);
    private static volatile PcScCardReaderController instance;
    private final Collection<ICardReader> readers;

    private PcScCardReaderController() {
        readers = new ArrayList<>();
        Collection<ICardReader> cardReaders = findAvailableCardReaders();
        readers.addAll(cardReaders);
        new CheckReaders().start();
    }

    /**
     * Returns an instance of PcScCardReaderController
     *
     * @return PcScCardReaderController singleton instance
     */
    public static PcScCardReaderController getInstance() {
        if (instance == null) {
            instance = new PcScCardReaderController();
        }
        return instance;
    }

    @Override
    public Collection<ICardReader> getCardReaders() {
        return readers;
    }

    private Collection<ICardReader> findAvailableCardReaders() {
        TerminalFactory factory = TerminalFactory.getDefault();
        return listCardReaders(factory);
    }

    /**
     * Returns a collection of connected cardReaders
     *
     * @return cardReaders
     */
    private Collection<ICardReader> listCardReaders(final TerminalFactory factory) {
        List<CardTerminal> list;
        try {
            list = factory.terminals().list();
        } catch (Exception e) {
            list = new ArrayList<>();
            LOGGER.info(String.valueOf(e)); // exception is logged
        }
        return list.stream().map(ct -> new CardReader(ct)).collect(Collectors.toList());
    }

    /**
     * Thread which checks every 1000ms for new or disconnected pcsc card readers and inform the listeners about connection or disconnection
     */
    class CheckReaders extends Thread {

        private static final int SLEEP_BETWEEN_READERS_CHECK = 1000;
        private final Map<CardReader, CardReaderCardChecker> cardCheckerMap = new HashMap<>();

        CheckReaders() {
            setDaemon(true);
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Collection<ICardReader> cardReaders = findAvailableCardReaders();
                    findNewAndInform(cardReaders);
                    findRemovedAndInform(cardReaders);
                    PcScCardReaderController.this.readers.clear();
                    PcScCardReaderController.this.readers.addAll(cardReaders);
                    Thread.sleep(SLEEP_BETWEEN_READERS_CHECK);
                } catch (InterruptedException e) {
                    LOGGER.warn("InterruptedException by read PCSC-CardReader", e);
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void findNewAndInform(final Collection<ICardReader> cardReaders) {
            Collection<ICardReader> cardReadersNewRead = new ArrayList<>(cardReaders);
            Predicate<ICardReader> ctPredicate = ct -> PcScCardReaderController.this.readers.stream().anyMatch(ct2 -> ct2.getName().equals(ct.getName()));
            cardReadersNewRead.removeIf(ctPredicate);
            cardReadersNewRead.forEach(ct -> informAboutReaderConnection(ct, InitializationStatus.INIT_SUCCESS));
            cardReadersNewRead.forEach(ct -> createNewChecker((CardReader) ct));
        }

        private void findRemovedAndInform(final Collection<ICardReader> cardReaders) {
            Collection<ICardReader> cardReadersToRemove = new ArrayList<>(PcScCardReaderController.this.readers);
            Predicate<ICardReader> ctPredicate = ct -> cardReaders.stream().anyMatch(ct2 -> ct2.getName().equals(ct.getName()));

            cardReadersToRemove.removeIf(ctPredicate);
            cardReadersToRemove.forEach(ct -> stopChecker((CardReader) ct));
            cardReadersToRemove.forEach(ct -> informAboutReaderDisconnection(ct));
        }

        private void createNewChecker(final CardReader cardReader) {
            cardCheckerMap.put(cardReader, CardReaderCardChecker.startNewInstance(cardReader));
        }

        private void stopChecker(final CardReader cardReader) {
            if (cardCheckerMap.containsKey(cardReader)) {
                cardCheckerMap.remove(cardReader).stopChecker();
            }
        }
    }
}
