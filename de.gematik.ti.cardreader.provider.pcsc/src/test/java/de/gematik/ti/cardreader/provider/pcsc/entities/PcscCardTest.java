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

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.card.Atr;
import de.gematik.ti.cardreader.provider.api.card.CardProtocol;
import de.gematik.ti.cardreader.provider.api.card.ICardChannel;
import de.gematik.ti.cardreader.provider.pcsc.PcscCardTerminal;

/**
 * Required connetion to device (i.e. CardTerminal and inserted Card). *no failure if no device, just interrupt
 *
 */
public class PcscCardTest {

    private static final Logger LOG = LoggerFactory.getLogger(PcscCardTest.class);
    private static PcscCard pcscCard;
    private static boolean deviceForTestAvailable = true;
    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void setUpBeforeClass() {
        CardTerminal cardTerminal = null;
        try {
            cardTerminal = PcscCardTerminal.getCardReader().getCardTerminal();
            Card javaxCard;
            javaxCard = cardTerminal.connect("*");
            pcscCard = new PcscCard(javaxCard);
        } catch (PcscCrpRuntimeException e) {
            LOG.info(e.getMessage());
            deviceForTestAvailable = false;
            return;
        } catch (CardException e) {
            throw new PcscCrpRuntimeException("Error " + e + " on PcscCardTest.setUpBeforeClass occurred");
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        testDisconnect();
    }

    @Test
    public void testOpenLogicalChannel() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start " + testName.getMethodName());
        try {
            ICardChannel newChannel = pcscCard.openLogicalChannel();
            Assert.assertThat(newChannel, IsNull.notNullValue());
            Assert.assertThat(newChannel.getMaxMessageLength(), Is.is(0));
        } catch (de.gematik.ti.cardreader.provider.api.card.CardException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testOpenBasicChannel() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start " + testName.getMethodName());
        try {
            ICardChannel basicChannel = pcscCard.openBasicChannel();
            Assert.assertThat(basicChannel, IsNull.notNullValue());
            Assert.assertThat(basicChannel.getChannelNumber(), Is.is(0));
        } catch (de.gematik.ti.cardreader.provider.api.card.CardException e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void testGetProtocol() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start " + testName.getMethodName());
        CardProtocol protocol = pcscCard.getProtocol();
        Assert.assertThat(protocol, Is.is(CardProtocol.T1));
    }

    @Test
    public void testGetATR() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start " + testName.getMethodName());
        Atr atr = pcscCard.getATR();
        Assert.assertThat(atr, IsNull.notNullValue());
        Assert.assertThat(atr.getBytes(), IsNull.notNullValue());
        LOG.info("atr.length is {}", atr.getBytes().length);
        Assert.assertThat(atr.getBytes().length > 10, Is.is(true));
    }

    /**
     * run at last. because other tests require connection
     */
    public static void testDisconnect() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start testDisconnect");
        try {
            pcscCard.disconnect(true);
            ExpectedException.none();
            LOG.info("pcscCard {} successfully closed", pcscCard);
        } catch (de.gematik.ti.cardreader.provider.api.card.CardException e) {
            Assert.fail(e.toString());
        }
    }

}
