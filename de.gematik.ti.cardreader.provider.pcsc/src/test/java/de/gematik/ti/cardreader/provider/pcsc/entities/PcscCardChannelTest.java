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
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.card.ICard;
import de.gematik.ti.cardreader.provider.api.command.CommandApdu;
import de.gematik.ti.cardreader.provider.api.command.ICommandApdu;
import de.gematik.ti.cardreader.provider.api.command.IResponseApdu;
import de.gematik.ti.cardreader.provider.pcsc.PcscCardTerminal;
import de.gematik.ti.utils.codec.Hex;

/**
 */
public class PcscCardChannelTest {

    private static final Logger LOG = LoggerFactory.getLogger(PcscCardChannelTest.class);
    private static PcscCardChannel pcscCardChannel;
    private static boolean deviceForTestAvailable = true;
    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            CardTerminal cardTerminal = PcscCardTerminal.getCardReader().getCardTerminal();
            Card javaxCard = cardTerminal.connect("*");
            PcscCard pcscCard = new PcscCard(javaxCard);
            pcscCardChannel = new PcscCardChannel(javaxCard.getBasicChannel(), pcscCard);

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
        testClose();
    }

    @Test
    public void testGetCard() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start " + testName.getMethodName());
        ICard iCard = pcscCardChannel.getCard();
        LOG.info("iCard is {}", iCard);
        Assert.assertThat(iCard, IsNull.notNullValue());

    }

    @Test
    public void testGetChannelNumber() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start " + testName.getMethodName());
        int channelNumber = pcscCardChannel.getChannelNumber();
        Assert.assertThat(channelNumber, Is.is(0));
    }

    @Test
    public void shouldSucceedExtendedLengthSupported() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start " + testName.getMethodName());
        CardChannel channelMock = Mockito.mock(CardChannel.class);
        PcscCard cardMock = Mockito.mock(PcscCard.class);
        PcscCardChannel pcscCardChannelLocal = new PcscCardChannel(channelMock, cardMock);
        boolean extendedLengthSupported = pcscCardChannelLocal.isExtendedLengthSupported();
        Assert.assertThat(extendedLengthSupported, Is.is(true));
    }

    @Test
    public void testGetMaxMessageLength() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start " + testName.getMethodName());
        CardChannel channelMock = Mockito.mock(CardChannel.class);
        PcscCard cardMock = Mockito.mock(PcscCard.class);
        PcscCardChannel pcscCardChannelLocal = new PcscCardChannel(channelMock, cardMock);
        int maxMessageLength = pcscCardChannelLocal.getMaxMessageLength();
        Assert.assertThat(maxMessageLength, Is.is(1033));
    }

    @Test
    public void testGetMaxResponseLength() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start " + testName.getMethodName());
        CardChannel channelMock = Mockito.mock(CardChannel.class);
        PcscCard cardMock = Mockito.mock(PcscCard.class);
        PcscCardChannel pcscCardChannelLocal = new PcscCardChannel(channelMock, cardMock);
        int maxResponseLength = pcscCardChannelLocal.getMaxResponseLength();
        Assert.assertThat(maxResponseLength, Is.is(8192));
    }

    @Test
    public void testTransmit() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start " + testName.getMethodName());
        ICommandApdu command = new CommandApdu(0x00, 0xB0, 0x82, 0x00, 0x00);
        try {
            IResponseApdu resp = pcscCardChannel.transmit(command);
            LOG.info("resp: sw1:" + resp.getSW1() + " sw2:" + resp.getSW2() + " bytes:" + Hex.encodeHexString(resp.getBytes()));
            Assert.assertThat(Integer.toHexString(resp.getSW()), Is.is("9000"));
            // 5A0A802768831100000172229000
            LOG.info("received resp {}, command is 00B08200000000", resp);
        } catch (de.gematik.ti.cardreader.provider.api.card.CardException e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * run at last. because other tests require connection
     */
    public static void testClose() {
        Assume.assumeTrue(deviceForTestAvailable);
        LOG.info("------ Start close");
        try {
            pcscCardChannel.close();
            ExpectedException.none();
            LOG.info("pcscChannel {} closed", pcscCardChannel);
        } catch (de.gematik.ti.cardreader.provider.api.card.CardException e) {
            Assert.fail(e.toString());
        }
    }

}
