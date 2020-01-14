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

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.smartcardio.CardChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.card.CardException;
import de.gematik.ti.cardreader.provider.api.card.ICard;
import de.gematik.ti.cardreader.provider.api.card.ICardChannel;
import de.gematik.ti.cardreader.provider.api.command.ICommandApdu;
import de.gematik.ti.cardreader.provider.api.command.IResponseApdu;
import de.gematik.ti.cardreader.provider.api.command.ResponseApdu;

/**
 * Encapsulate a javax.CardChannel in gematik.ti.CardChannel
 *
 */
public class PcscCardChannel implements ICardChannel {

    private static final int MINIMAL_ALLOCATE = 258;
    private static final Logger LOG = LoggerFactory.getLogger(PcscCardChannel.class);
    private static final String CARDREADER_BUFFER = "cardReader_Buffer";
    private static final String CARD_MAXAPDUBUFFERSIZE = "card_maxApduBufferSize";
    private static final String CARD_MAXRESPAPDUBUFFERSIZE = "card_maxRespApduBufferSize";
    // SecureMessaging-Values not required for the time being, but should stay here for future
    // private static final String CARD_MAXAPDUBUFFERSIZESM = "card_maxApduBufferSizeSM";
    // private static final String CARD_MAXRESPAPDUBUFFERSIZESM = "card_maxRespApduBufferSizeSM";
    private final CardChannel javaxCardChannel;
    private int allocateLength;
    private final PcscCard pcscCard;

    private static final Map<String, Integer> bufferSizeConfig = new LinkedHashMap() {
        {
            put(CARDREADER_BUFFER, 8192);
            put(CARD_MAXAPDUBUFFERSIZE, 1033);
            put(CARD_MAXRESPAPDUBUFFERSIZE, 65535);
            // SecureMessaging-Values not required for the time being, but should stay here for future
            // put(CARD_MAXAPDUBUFFERSIZESM, 1033);
            // put(CARD_MAXRESPAPDUBUFFERSIZESM, 1033);
        }
    };

    public PcscCardChannel(CardChannel channel, PcscCard pcscCard) {
        this.javaxCardChannel = channel;
        this.pcscCard = pcscCard;
    }

    @Override
    public ICard getCard() {
        return pcscCard;
    }

    @Override
    public int getChannelNumber() {
        return javaxCardChannel.getChannelNumber();
    }

    /**
     * @return
     */
    @Override
    public boolean isExtendedLengthSupported() {
        return getMaxMessageLength() > 255 && getMaxResponseLength() > 255;
    }

    /**
     * TODO: secureMessaging: Do only if it it required, channel must be know before if secureMessaging used.
     *
     * @return
     */
    @Override
    public int getMaxMessageLength() {
        int maxMessageLengthCardReader = bufferSizeConfig.get(CARDREADER_BUFFER);
        int maxMessageLengthCard = bufferSizeConfig.get(CARD_MAXAPDUBUFFERSIZE);
        return Math.min(maxMessageLengthCard, maxMessageLengthCardReader);
    }

    /**
     * TODO: secureMessaging: Do only if it it required, channel must be know before if secureMessaging used.
     *
     * @return
     */
    @Override
    public int getMaxResponseLength() {
        int maxResponseLengthCardReader = bufferSizeConfig.get(CARDREADER_BUFFER);
        int maxResponseLengthCard = bufferSizeConfig.get(CARD_MAXRESPAPDUBUFFERSIZE);
        return Math.min(maxResponseLengthCard, maxResponseLengthCardReader);
    }

    @Override
    public IResponseApdu transmit(ICommandApdu command) throws CardException {
        try {
            if (allocateLength == 0) {
                allocateLength = MINIMAL_ALLOCATE;
            }
            ByteBuffer byteBufferResp = ByteBuffer.allocate(MINIMAL_ALLOCATE);
            int validLength = javaxCardChannel.transmit(ByteBuffer.wrap(command.getBytes()), byteBufferResp);
            LOG.debug("validLength in Response is {}", validLength);
            byte[] validBytes = new byte[validLength];
            System.arraycopy(byteBufferResp.array(), 0, validBytes, 0, validLength);
            return new ResponseApdu(validBytes);
        } catch (javax.smartcardio.CardException e) {
            throw new CardException("Error " + e + " on close occurred");
        }
    }

    @Override
    public void close() throws CardException {
        try {
            if (javaxCardChannel.getChannelNumber() == pcscCard.openBasicChannel().getChannelNumber()) {
                LOG.debug("BasicChannel not closable");
            } else {
                javaxCardChannel.close();
            }
        } catch (Exception e) {
            throw new CardException("Error " + e + " on close occurred");
        }
    }

    public void setAllocateLength(int length) {
        if (length < MINIMAL_ALLOCATE) {
            throw new PcscCrpRuntimeException("Length must not be less than " + length);
        }
        allocateLength = length;
    }
}
