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

import de.gematik.ti.cardreader.provider.api.card.*;

/**
 */
public class TestCard implements ICard {

    @Override
    public Atr getATR() {
        return null;
    }

    @Override
    public ICardChannel openLogicalChannel() throws CardException {
        return null;
    }

    @Override
    public void disconnect(boolean reset) throws CardException {

    }

    @Override
    public CardProtocol getProtocol() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ICardChannel openBasicChannel() throws CardException {
        // TODO Auto-generated method stub
        return null;
    }

}
