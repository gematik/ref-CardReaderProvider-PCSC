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

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.hamcrest.core.Is;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gematik.ti.cardreader.provider.pcsc.PcscCardTerminal;
import de.gematik.ti.cardreader.provider.pcsc.entities.PcscCrpRuntimeException;

/**
 */
public class CardReaderCardCheckerTest {
    private static CardReaderCardChecker cardReaderCardChecker;
    private static final Logger LOG = LoggerFactory.getLogger(CardReaderCardCheckerTest.class);
    private static boolean deviceForTestAvailable = true;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    PcscCardTerminal.getPcscCardTerminal().connect("*");
                }
            };
            runner.run();
        } catch (PcscCrpRuntimeException e) {
            LOG.warn(e.getMessage());
            deviceForTestAvailable = false;
            return;
        }
        cardReaderCardChecker = new CardReaderCardChecker(PcscCardTerminal.getCardReader());
        CardReaderCardChecker.startNewInstance(PcscCardTerminal.getCardReader());
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Assume.assumeTrue(deviceForTestAvailable);
        cardReaderCardChecker.stopChecker();

    }

    @Test
    public void testStartNewInstance() {
        Assume.assumeTrue(deviceForTestAvailable);
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> PcscCardTerminal.getPcscCardTerminal().isCardPresent());
        Assert.assertThat(PcscCardTerminal.getPcscCardTerminal().waitForCardPresent(10), Is.is(true));
    }

    @Test
    public void testStopChecker() {
        Assume.assumeTrue(deviceForTestAvailable);
        cardReaderCardChecker.stopChecker();
        LOG.info("Simulate remove a card from CardReader");
        Assert.assertThat(cardReaderCardChecker.isActive(), Is.is(false));
    }

    @Test
    public void testRun() {
        Assume.assumeTrue(deviceForTestAvailable);
        cardReaderCardChecker.start();
    }
}
