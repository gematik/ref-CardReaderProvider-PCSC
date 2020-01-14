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

import de.gematik.ti.cardreader.provider.api.ICardReaderController;
import de.gematik.ti.cardreader.provider.api.entities.IProviderDescriptor;
import de.gematik.ti.cardreader.provider.api.entities.ProviderDescriptor;
import de.gematik.ti.cardreader.provider.spi.ICardReaderControllerProvider;

/**
 *
 * include::{userguide}/PCSCCRP_Overview.adoc[tag=PCSCCardReaderProvider]
 *
 */
public class CardReaderProvider implements ICardReaderControllerProvider {

    private final ProviderDescriptor providerDescriptor;

    /**
     * Contructor to create new pcsc card reader provider
     */
    public CardReaderProvider() {
        providerDescriptor = new ProviderDescriptor("Gematik PCSC-CardReaderProvider");
        providerDescriptor.setClassName(this.getClass().toString());
        providerDescriptor.setDescription("Dieser Provider stellt die PCSC Kartenleser bereit.");
        providerDescriptor.setLicense("Gematik interner Gebrauch");
    }

    /**
     * Returns an instance of PCSCCardReaderController
     *
     * @return PcScCardReaderController.getInstance()
     */
    @Override
    public ICardReaderController getCardReaderController() {
        return PcScCardReaderController.getInstance();
    }

    /**
     * Returns the Providerdesciptor with information about the PCSC CardReaderProvider
     *
     * @return Providerdesciptor
     */
    @Override
    public IProviderDescriptor getDescriptor() {
        return providerDescriptor;
    }

}
