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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.card.CardException;
import de.gematik.ti.cardreader.provider.api.events.card.CardAbsentEvent;
import de.gematik.ti.cardreader.provider.api.events.card.CardPresentEvent;
import de.gematik.ti.cardreader.provider.pcsc.entities.CardReader;

/**
 * Unit-Tests für die Klasse {@link PcScCardReaderController}
 *
 */
public class CardConnectionTest {
    private static final Logger LOG = LoggerFactory.getLogger(CardConnectionTest.class);
    private final MyCardTerminal scCardTerminal1 = new MyCardTerminal("scCardTerminal1");
    private final MyCardTerminal scCardTerminal2 = new MyCardTerminal("scCardTerminal2");

    private CardReader pcscCardReader1;
    private CardReader pcscCardReader2;
    private CardReaderCardChecker cardReaderCardChecker1;
    private CardReaderCardChecker cardReaderCardChecker2;
    private ConnectionListener listener1;

    @Before
    public void init() throws InterruptedException {
        scCardTerminal2.setCardPresent(true);
        listener1 = new ConnectionListener();
        EventBus.getDefault().register(listener1);

        pcscCardReader1 = new CardReader(scCardTerminal1);
        pcscCardReader2 = new CardReader(scCardTerminal2);

        cardReaderCardChecker1 = CardReaderCardChecker.startNewInstance(pcscCardReader1);
        cardReaderCardChecker2 = CardReaderCardChecker.startNewInstance(pcscCardReader2);

        LOG.debug(" Create CardReader " + pcscCardReader1);
        LOG.debug(" Create CardReader " + pcscCardReader2);

        await().until(() -> cardReaderCardChecker1.isActive());
        await().until(() -> cardReaderCardChecker2.isActive());
    }

    @After
    public void close() {
        EventBus.getDefault().unregister(listener1);
        cardReaderCardChecker1.stopChecker();
        cardReaderCardChecker1.stopChecker();
    }

    @Test
    public void testCards() throws InterruptedException, CardException {
        Assert.assertEquals(1, listener1.getCards());
        scCardTerminal1.setCardPresent(true);
        await().atMost(10, SECONDS).until(() -> listener1.getCards() == 2);

        Assert.assertEquals(2, listener1.getCards());
        scCardTerminal1.setCardPresent(false);
        scCardTerminal2.setCardPresent(false);

        EventBus.getDefault().post(scCardTerminal1);
        EventBus.getDefault().post(scCardTerminal2);
        await().atMost(10, SECONDS).until(() -> listener1.getCards() == 0);
        Assert.assertEquals(0, listener1.getCards());
    }

    public static class ConnectionListener {

        private int cards = 0;

        @Subscribe
        public void cardPresent(final CardPresentEvent cardPresentEvent) {
            LOG.debug("cardPresent " + cardPresentEvent.getCardReader());
            cards += 1;
        }

        @Subscribe
        public void cardAbsent(final CardAbsentEvent cardAbsentEvent) {
            LOG.debug("cardAbsent " + cardAbsentEvent.getCardReader());
            cards -= 1;
        }

        public int getCards() {
            return cards;
        }
    }

    public static class MyCardTerminal extends javax.smartcardio.CardTerminal {

        private final String name;
        private boolean cardPresent = false;

        public MyCardTerminal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public javax.smartcardio.Card connect(String s) {
            return null;
        }

        @Override
        public boolean isCardPresent() {
            return cardPresent;
        }

        @Override
        public boolean waitForCardPresent(long l) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOG.error(e.toString(), e);
            }
            return true;
        }

        @Override
        public boolean waitForCardAbsent(long l) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOG.error(e.toString(), e);
            }
            return true;
        }

        public void setCardPresent(boolean cardPresent) {
            this.cardPresent = cardPresent;
        }
    }
}
