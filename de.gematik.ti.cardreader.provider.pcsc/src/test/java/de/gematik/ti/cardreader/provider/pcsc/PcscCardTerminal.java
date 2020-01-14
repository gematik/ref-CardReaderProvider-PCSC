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

package de.gematik.ti.cardreader.provider.pcsc;

import java.util.List;

import javax.smartcardio.CardTerminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.card.ICard;
import de.gematik.ti.cardreader.provider.pcsc.entities.CardReader;
import de.gematik.ti.cardreader.provider.pcsc.entities.PcscCrpRuntimeException;
import de.gematik.ti.cardreader.provider.pcsc.entities.TerminalService;

public class PcscCardTerminal {

    private static final Logger LOG = LoggerFactory.getLogger(PcscCardTerminal.class);
    private boolean cardPresent;
    private TestCard card;
    private volatile static CardReader cardReader;
    private volatile static PcscCardTerminal pcscCardTerminal;

    private PcscCardTerminal() {
        getCardReader();
    }

    public static CardReader getCardReader() {
        if (cardReader == null) {
            cardReader = new CardReader(initCardTerminal());
        }
        return cardReader;
    }

    public static PcscCardTerminal getPcscCardTerminal() {

        if (pcscCardTerminal == null) {
            pcscCardTerminal = new PcscCardTerminal();
        }
        return pcscCardTerminal;
    }

    public boolean isCardPresent() {
        return cardPresent;
    }

    public void setCardPresent(boolean statePresent) {
        cardPresent = statePresent;
    }

    public String getName() {
        return "PcscCardTerminal for test";
    }

    public ICard connect(String s) {
        card = new TestCard();
        setCardPresent(true);
        return card;
    }

    public boolean waitForCardPresent(long l) {
        cardPresent = true;
        return cardPresent;
    }

    public boolean waitForCardAbsent(long l) {
        cardPresent = false;
        return cardPresent;
    }

    private static CardTerminal initCardTerminal() {
        try {
            List<CardTerminal> list = TerminalService.getTerminalSerice().getListTerminals();
            if (list != null) {
                return list.get(0);
            } else {
                LOG.info("No cardTerminal found");
                return null;
            }
        } catch (Exception e) {
            throw new PcscCrpRuntimeException("Error " + e + " on initCardTerminals occurred");
        }
    }

}
