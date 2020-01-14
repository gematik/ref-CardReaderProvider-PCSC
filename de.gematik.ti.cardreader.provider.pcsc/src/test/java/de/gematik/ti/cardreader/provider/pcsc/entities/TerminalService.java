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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.smartcardio.CardTerminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search Terminals for instantiation a Cardreader
 * A direct way to get PCSCTerminal without using javax.smartcardio.TerminalFactory and javax.smartcardio.TerminalFactorySpi <br/>
 */
public class TerminalService {
    private static final Logger LOG = LoggerFactory.getLogger(TerminalService.class);
    private static volatile TerminalService instance;
    private final List<CardTerminal> listTerminals;

    private TerminalService() {
        listTerminals = initCardTerminals();
    }

    /**
     * Singleton instance
     *
     * @return
     */
    public static TerminalService getTerminalSerice() {
        if (instance == null) {
            instance = new TerminalService();
        }
        return instance;
    }

    /**
     * Supply the result to user <br/>
     *
     * @Nullable
     * @return
     */
    public List<CardTerminal> getListTerminals() {
        return listTerminals;
    }

    public List<CardTerminal> initCardTerminals() {
        try {
            Class<?> c = Class.forName("sun.security.smartcardio.PCSCTerminals");
            Constructor<?> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object o = constructor.newInstance();
            //
            Method declaredMethodInit = o.getClass().getDeclaredMethod("initContext");
            LOG.debug("Methode: " + declaredMethodInit);
            declaredMethodInit.setAccessible(true);
            Object[] parameter = null;
            Object resultInit = declaredMethodInit.invoke(o, parameter);
            LOG.info("ContextId: " + resultInit);
            //
            Field declaredField = c.getDeclaredField("contextId");
            declaredField.setAccessible(true);
            Object object = declaredField.get(o);
            LOG.info("contextId=" + object);

            //
            Method declaredMethod = o.getClass().getDeclaredMethod("list", javax.smartcardio.CardTerminals.State.class);
            LOG.debug("Methode: " + declaredMethod);
            declaredMethod.setAccessible(true);
            Object result = declaredMethod.invoke(o, javax.smartcardio.CardTerminals.State.ALL);
            LOG.info("all Terminals: " + result);
            //

            java.util.List<CardTerminal> list = (java.util.List<CardTerminal>) result;
            return list;
        } catch (java.lang.reflect.InvocationTargetException e) {
            LOG.info("No Terminal found");
            return null;
        } catch (Exception e) {
            throw new PcscCrpRuntimeException("Error " + e + " on initCardTerminals occurred");
        }
    }
}
