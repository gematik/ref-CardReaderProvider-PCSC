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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.card.*;

/**
 * Encapsulate a javax.Card in gematik.ti.Card
 *
 */
public class PcscCard implements ICard {

    private static final Logger LOG = LoggerFactory.getLogger(PcscCard.class);
    private final Card javaxCard;

    public PcscCard(Card javaxCard) {
        this.javaxCard = javaxCard;
        if (javaxCard == null) {
            throw new PcscCrpRuntimeException("javaxCard is not available.");
        }
    }

    @Override
    public ICardChannel openLogicalChannel() throws CardException {
        try {
            CardChannel channel = javaxCard.openLogicalChannel();
            LOG.debug("a channel {}({}) successfully opened", channel, channel.getChannelNumber());
            return new PcscCardChannel(channel, this);
        } catch (javax.smartcardio.CardException e) {
            throw new CardException("Error " + e + " on openBasicChannel occurred");
        }
    }

    @Override
    public ICardChannel openBasicChannel() throws CardException {
        try {
            if (javaxCard.getBasicChannel() != null) {
                LOG.info("basicChannel opened already: " + javaxCard.getBasicChannel());
                return new PcscCardChannel(javaxCard.getBasicChannel(), this);
            }
            CardChannel channel = javaxCard.openLogicalChannel();
            PcscCard pcscCard = new PcscCard(javaxCard);
            PcscCardChannel pcscCardChannel = new PcscCardChannel(channel, pcscCard);
            return pcscCardChannel;
        } catch (javax.smartcardio.CardException e) {
            throw new CardException("Error " + e + " on openBasicChannel occurred");
        }
    }

    @Override
    public CardProtocol getProtocol() {
        try {
            String protocolResult = javaxCard.getProtocol();
            switch (protocolResult) {
                case "T=0":
                    return CardProtocol.T0;
                case "T=1":
                    return CardProtocol.T1;
                default:
                    return CardProtocol.T15;
            }
        } catch (Exception e) {
            throw new PcscCrpRuntimeException("Error " + e + " on get Protocol occurred");
        }
    }

    @Override
    public Atr getATR() {
        try {
            byte[] atrBytes = javaxCard.getATR().getBytes();
            return new Atr(atrBytes);
        } catch (Exception e) {
            throw new PcscCrpRuntimeException("Error " + e + " on get ATR occurred");
        }

    }

    @Override
    public void disconnect(boolean reset) throws CardException {
        try {
            javaxCard.disconnect(reset);
        } catch (javax.smartcardio.CardException e) {
            throw new PcscCrpRuntimeException("Error " + e + " on disconnect occurred");
        }
    }

}
