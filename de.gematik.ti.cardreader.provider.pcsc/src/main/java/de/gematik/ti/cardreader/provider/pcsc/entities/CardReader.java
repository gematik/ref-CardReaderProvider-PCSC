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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.api.ICardReader;
import de.gematik.ti.cardreader.provider.api.card.CardException;
import de.gematik.ti.cardreader.provider.api.card.ICard;

/**
 * include::{userguide}/PCSCCRP_Overview.adoc[tag=PCSCCardReader]
 *
 */
public class CardReader implements ICardReader {

    private static final Logger LOG = LoggerFactory.getLogger(CardReader.class);
    private final CardTerminal cardTerminal;

    public CardReader(final CardTerminal cardTerminal) {
        this.cardTerminal = cardTerminal;
        if (cardTerminal == null) {
            throw new PcscCrpRuntimeException("CardTerminal is not available");
        }
    }

    /**
     * initialize the card reader
     */
    @Override
    public void initialize() {
        // Nothing to do
    }

    /**
     * Return the current initialisation status
     *
     * @return true: if card terminal is initialized false: card terminal not operational
     */
    @Override
    public boolean isInitialized() {
        return true;
    }

    /**
     * Establishes a connection to the card. If a connection has previously established using the specified protocol, this method returns the same Card object
     * as the previous call.
     *
     * @param protocol
     *            - the protocol to use ("T=0", "T=1", or "T=CL"), or "*" to connect using any available protocol.
     * @return Card object
     * @throws NullPointerException
     *             if protocol is null IllegalArgumentException if protocol is an invalid protocol specification CardNotPresentException if no card is present
     *             in this terminal CardException if a connection could not be established using the specified protocol or if a connection has previously been
     *             established using a different protocol SecurityException if a SecurityManager exists and the caller does not have the required permission
     */
    public ICard connect(final String protocol) throws CardException {
        try {
            return new PcscCard(cardTerminal.connect(protocol));
        } catch (Exception e) {
            throw new CardException("Error " + e + " on cardTerminal.connect occurred");
        }
    }

    /**
     * Establishes a connection to the card with the protocol `*`. If a connection has previously established using the specified protocol, this method returns
     * the same Card object as the previous call.
     *
     * @return Card object
     * @throws NullPointerException
     *             if protocol is null IllegalArgumentException if protocol is an invalid protocol specification CardNotPresentException if no card is present
     *             in this terminal CardException if a connection could not be established using the specified protocol or if a connection has previously been
     *             established using a different protocol SecurityException if a SecurityManager exists and the caller does not have the required permission
     */
    @Override
    public ICard connect() throws CardException {
        return connect("*");
    }

    /**
     * Returns the unique name of this terminal.
     *
     * @return the unique name of this terminal.
     */
    @Override
    public String getName() {
        return cardTerminal.getName();
    }

    /**
     * Returns whether a card is present in this terminal.
     *
     * @return whether a card is present in this terminal.
     * @throws CardException
     *             if the status could not be determined
     */
    @Override
    public boolean isCardPresent() throws CardException {
        try {
            return cardTerminal.isCardPresent();
        } catch (Exception e) {
            throw new CardException("Error " + e + " on cardTerminal.isCardPresent() occurred");
        }
    }

    /**
     * Waits until a card is absent in this terminal or the timeout expires. If the method returns due to an expired timeout, it returns false. Otherwise it
     * return true. If no card is present in this terminal when this method is called, it returns immediately.
     *
     * @param timeout
     *            - if positive, block for up to timeout milliseconds; if zero, block indefinitely; must not be negative
     * @return false if the method returns due to an expired timeout, true otherwise.
     * @throws IllegalArgumentException
     *             if timeout is negative CardException if the operation failed
     */
    public boolean waitForCardAbsent(final long timeout) throws CardException {
        try {
            return cardTerminal.waitForCardAbsent(timeout);
        } catch (javax.smartcardio.CardException e) {
            throw new CardException("Error " + e + " on cardTerminal.waitForCardAbsent() occurred");
        }
    }

    /**
     * Waits until a card is present in this terminal or the timeout expires. If the method returns due to an expired timeout, it returns false. Otherwise it
     * return true. If a card is present in this terminal when this method is called, it returns immediately.
     *
     * @param timeout
     *            - if positive, block for up to timeout milliseconds; if zero, block indefinitely; must not be negative
     * @return false if the method returns due to an expired timeout, true otherwise.
     * @throws IllegalArgumentException
     *             if timeout is negative CardException if the operation failed
     */
    public boolean waitForCardPresent(final long timeout) throws CardException {
        try {
            return cardTerminal.waitForCardPresent(timeout);
        } catch (javax.smartcardio.CardException e) {
            throw new CardException("Error " + e + " on cardTerminal.waitForCardAbsent(long) occurred");
        }
    }

    @Override
    public String toString() {
        return this.cardTerminal.getName();
    }

    /**
     * @return cardTerminal
     */
    public CardTerminal getCardTerminal() {
        return cardTerminal;
    }
}
