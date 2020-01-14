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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.AbstractCardReaderController;
import de.gematik.ti.cardreader.provider.api.card.CardException;
import de.gematik.ti.cardreader.provider.pcsc.entities.CardReader;

/**
 * Thread to check card reader for connected or disconnected cards and inform over EventBus
 *
 */
public class CardReaderCardChecker extends Thread {
    private static final int WAIT_FOR_CARD_EVENT = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(CardReaderCardChecker.class);
    private volatile boolean physicalReaderAvailable = true;
    private final CardReader cardReader;
    private boolean active = false;
    private final AbstractCardReaderController cardReaderController;

    /**
     * Create a new instance
     *
     * @param cardReader
     *            - card reader to check
     */
    public CardReaderCardChecker(final CardReader cardReader) {
        this.cardReader = cardReader;
        this.cardReaderController = PcScCardReaderController.getInstance();
    }

    /**
     * Create a new Instance and start the Thread
     *
     * @param cardReader
     *            - card reader to check
     * @return new instance of CardReaderCardChecker
     */
    public static CardReaderCardChecker startNewInstance(final CardReader cardReader) {
        CardReaderCardChecker newInstance = new CardReaderCardChecker(cardReader);
        newInstance.start();
        return newInstance;
    }

    @Override
    public void run() {
        super.run();
        CardReaderCardChecker.this.doCheckCards();
    }

    private void doCheckCards() {
        active = true;
        LOGGER.info("Start CardReaderCardChecker");
        boolean state = false;
        try {
            state = cardReader.isCardPresent();
            LOGGER.debug("state: " + state);
        } catch (CardException e) {
            LOGGER.debug("Error by request current card status at card reader " + cardReader, e);
        }
        if (state) {
            cardReaderController.createCardEventTransmitter(cardReader).informAboutCardPresent();
        }
        LOGGER.debug("initial State: " + state);
        while (physicalReaderAvailable) {
            try {
                if (state) {
                    boolean cardAbsent = cardReader.waitForCardAbsent(WAIT_FOR_CARD_EVENT);
                    LOGGER.trace("cardAbsent: " + cardAbsent);
                } else {
                    boolean cardPresent = cardReader.waitForCardPresent(WAIT_FOR_CARD_EVENT);
                    LOGGER.trace("cardPresent: " + cardPresent);
                }
                if (!physicalReaderAvailable) {
                    LOGGER.debug("physicalReaderAvailable: " + physicalReaderAvailable);
                    return;
                }

                if (cardReader.isCardPresent() != state) {
                    state = cardReader.isCardPresent();
                    sendCardMessageForState(state);
                    LOGGER.trace("sendCardMessageForState: " + state);
                }
            } catch (CardException e) {
                LOGGER.error("Error by request current card status at card reader " + cardReader, e);
            }
        }
        active = false;
    }

    protected void sendCardMessageForState(final boolean state) {
        if (state) {
            cardReaderController.createCardEventTransmitter(cardReader).informAboutCardPresent();
        } else {
            cardReaderController.createCardEventTransmitter(cardReader).informAboutCardAbsent();
        }
    }

    /**
     * Stop the checking Thread
     */
    public void stopChecker() {
        physicalReaderAvailable = false;
    }

    /**
     * return the current running state of checker thread
     *
     * @return current thread state
     */
    public boolean isActive() {
        return active;
    }
}
