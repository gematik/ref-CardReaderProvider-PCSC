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

package de.gematik.ti.cardreader.provider.pcsc.entities;

import javax.smartcardio.CardTerminal;

import org.hamcrest.core.IsNull;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.card.CardException;
import de.gematik.ti.cardreader.provider.api.card.ICard;
import de.gematik.ti.cardreader.provider.pcsc.PcscCardTerminal;

public class CardReaderTest {
    private static final Logger LOG = LoggerFactory.getLogger(CardReaderTest.class);
    private static CardReader cardReader;
    private static boolean deviceForTestAvailable = true;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            CardTerminal cardTerminal = PcscCardTerminal.getCardReader().getCardTerminal();
            cardReader = new CardReader(cardTerminal);
            LOG.info("Configure a cardReader '{}' on cardTerminal '{}'", cardReader, cardTerminal);
        } catch (PcscCrpRuntimeException e) {
            LOG.info(e.getMessage());
            deviceForTestAvailable = false;
            return;
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testGetName() {
        Assume.assumeTrue(deviceForTestAvailable);
        Assert.assertThat(cardReader.getName(), IsNull.notNullValue());
    }

    @Test
    public void testIsCardPresent() {
        Assume.assumeTrue(deviceForTestAvailable);
        try {
            cardReader.isCardPresent();
            ExpectedException.none();
        } catch (CardException e) {
            Assert.fail(e.toString());
        }

    }

    @Test
    public void testWaitForCardPresent() {
        Assume.assumeTrue(deviceForTestAvailable);
        try {
            cardReader.waitForCardPresent(10);
            ExpectedException.none();
        } catch (CardException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testWaitForCardAbsent() {
        Assume.assumeTrue(deviceForTestAvailable);
        try {
            cardReader.waitForCardAbsent(10);
            ExpectedException.none();
        } catch (CardException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testCardReader() {
        Assume.assumeTrue(deviceForTestAvailable);
        Assert.assertThat(cardReader, IsNull.notNullValue());
    }

    @Test
    public void testConnectString() {
        Assume.assumeTrue(deviceForTestAvailable);
        ICard card;
        try {
            if (cardReader.isCardPresent()) {
                card = cardReader.connect("*");
                Assert.assertThat(card, IsNull.notNullValue());
            }
        } catch (CardException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testConnect() {
        Assume.assumeTrue(deviceForTestAvailable);
        ICard card;
        try {
            if (cardReader.isCardPresent()) {
                card = cardReader.connect();
                Assert.assertThat(card, IsNull.notNullValue());
            }
        } catch (CardException e) {
            Assert.fail(e.toString());
        }
    }

}
