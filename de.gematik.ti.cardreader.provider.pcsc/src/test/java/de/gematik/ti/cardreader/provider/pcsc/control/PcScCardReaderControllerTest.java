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

/**
 * Unit-Tests für die Klasse {@link PcScCardReaderController}
 *
 */

// @RunWith(PowerMockRunner.class)
// @PrepareForTest({ TerminalFactory.class })
// @PowerMockIgnore({ "javax.management.*", "javax.crypto.*", "javax.script.*", "javax.xml", "org.xml.*", "org.w3c.*" })
public class PcScCardReaderControllerTest {

    // private static final Logger LOG = LoggerFactory.getLogger(PcScCardReaderControllerTest.class);
    // private Collection<ICardReader> pcscCardReaders;
    // private PcScCardReaderController pcscCardReaderController;
    //
    // private Collection<ICardReader> cardReaders;
    // private final CardTerminals smartCardTerminalsMock = PowerMockito.mock(CardTerminals.class);
    // private final CardTerminal smartCardTerminalMock1 = PowerMockito.mock(CardTerminal.class);
    // private final CardTerminal smartCardTerminalMock2 = PowerMockito.mock(CardTerminal.class);
    // private final CardTerminal smartCardTerminalMock3 = PowerMockito.mock(CardTerminal.class);
    // private final CardTerminal smartCardTerminalMock4 = PowerMockito.mock(CardTerminal.class);
    // private final CardTerminal smartCardTerminalMock5 = PowerMockito.mock(CardTerminal.class);
    //
    // private final TerminalFactory factory = PowerMockito.mock(TerminalFactory.class);
    // private CardReader pcscCardReader1;
    // private CardReader pcscCardReader2;
    // private CardReader pcscCardReader3;
    // private CardReader pcscCardReader4;
    // private CardReader pcscCardReader5;
    //
    // private List<CardTerminal> cardTerminal2;
    //
    // public class CardReaders extends CardTerminals {
    //
    // @Override
    // public List<CardTerminal> list(State state) {
    // return cardTerminal2;
    // }
    //
    // @Override
    // public boolean waitForChange(long l) {
    // return false;
    // }
    // }
    //
    // @Before
    // public void init() throws CardException, InterruptedException {
    //
    // cardTerminal2 = new ArrayList<>();
    //
    // pcscCardReader1 = new CardReader(smartCardTerminalMock1);
    // pcscCardReader2 = new CardReader(smartCardTerminalMock2);
    // pcscCardReader3 = new CardReader(smartCardTerminalMock3);
    // pcscCardReader4 = new CardReader(smartCardTerminalMock4);
    // pcscCardReader5 = new CardReader(smartCardTerminalMock5);
    //
    // CardReaders cardReaders3 = new CardReaders();
    //
    // PowerMockito.mockStatic(TerminalFactory.class);
    //
    // PowerMockito.when(TerminalFactory.getDefault()).thenReturn(factory);
    // PowerMockito.when(factory.terminals()).thenReturn(cardReaders3);
    // try {
    // PowerMockito.when(smartCardTerminalsMock.list()).thenReturn(cardTerminal2);
    // } catch (Exception e) {
    // throw new PcscCrpRuntimeException("Error " + e.toString() + " on smartCardTerminalsMock.list() occurred");
    // }
    // PowerMockito.when(smartCardTerminalMock1.getName()).thenReturn("TestReader1");
    // PowerMockito.when(smartCardTerminalMock2.getName()).thenReturn("TestReader2");
    // PowerMockito.when(smartCardTerminalMock3.getName()).thenReturn("TestReader3");
    // PowerMockito.when(smartCardTerminalMock4.getName()).thenReturn("TestReader4");
    // PowerMockito.when(smartCardTerminalMock5.getName()).thenReturn("TestReader5");
    //
    // try {
    // PowerMockito.when(smartCardTerminalsMock.list()).thenReturn(cardReaders3.list());
    // } catch (javax.smartcardio.CardException e) {
    // throw new PcscCrpRuntimeException("Error " + e.toString() + " on smartCardTerminalsMock.list() occurred");
    // }
    // pcscCardReaderController = PcScCardReaderController.getInstance();
    //
    // Thread.sleep(3000);
    // }
    //
    // @After
    // public void close() {
    // pcscCardReaderController = null;
    //
    // }
    //
    // @Test
    // public void testGetCardReaders() throws CardException {
    // List<CardTerminal> terminals = new ArrayList<>();
    // try {
    // terminals = TerminalFactory.getDefault().terminals().list();
    // } catch (Exception e) {
    // // LOG.warn("TerminalFactory.getDefault().terminals().list(): " + e);
    // }
    //
    // cardReaders = PcScCardReaderController.getInstance().getCardReaders();
    // Assert.assertNotNull(cardReaders);
    // Assert.assertEquals(terminals.size(), cardReaders.size());
    // pcscCardReaders = pcscCardReaderController.getCardReaders();
    // Assert.assertNotNull(pcscCardReaders);
    // Assert.assertEquals(0, pcscCardReaders.size());
    // }
    //
    // @Test
    // public void testAddReader() throws InterruptedException, CardException {
    //
    // ConnectionListener listener1 = new ConnectionListener();
    // EventBus.getDefault().register(listener1);
    //
    // cardTerminal2.add(pcscCardReader1.getCardTerminal());
    // await().atMost(10, SECONDS).until(() -> listener1.getConnectedReaders() == 1);
    //
    // Assert.assertEquals(1, listener1.getConnectedReaders());
    // Assert.assertEquals(1, listener1.getInitializedReaders());
    // EventBus.getDefault().unregister(listener1);
    // }
    //
    // @Test
    // public void testAddReaderWithTwoListeners() throws InterruptedException {
    //
    // ConnectionListener listener1 = new ConnectionListener();
    // ConnectionListener listener2 = new ConnectionListener();
    //
    // EventBus.getDefault().register(listener1);
    // EventBus.getDefault().register(listener2);
    // cardTerminal2.add(pcscCardReader1.getCardTerminal());
    //
    // await().atMost(10, SECONDS).until(() -> listener1.getConnectedReaders() == 1);
    //
    // Assert.assertEquals(1, listener1.getConnectedReaders());
    // Assert.assertEquals(1, listener1.getInitializedReaders());
    //
    // await().atMost(10, SECONDS).until(() -> listener2.getConnectedReaders() == 1);
    // Assert.assertEquals(1, listener2.getConnectedReaders());
    // Assert.assertEquals(1, listener2.getInitializedReaders());
    //
    // EventBus.getDefault().unregister(listener1);
    // EventBus.getDefault().unregister(listener2);
    // }
    //
    // @Test
    // public void testAddFiveDifferentReaders() throws InterruptedException {
    // ConnectionListener listener1 = new ConnectionListener();
    // EventBus.getDefault().register(listener1);
    //
    // cardTerminal2.add(pcscCardReader1.getCardTerminal());
    // cardTerminal2.add(pcscCardReader2.getCardTerminal());
    // cardTerminal2.add(pcscCardReader3.getCardTerminal());
    // cardTerminal2.add(pcscCardReader4.getCardTerminal());
    // cardTerminal2.add(pcscCardReader5.getCardTerminal());
    //
    // await().atMost(10, SECONDS).until(() -> listener1.getConnectedReaders() == 5);
    //
    // Assert.assertEquals(5, listener1.getConnectedReaders());
    // Assert.assertEquals(5, listener1.getInitializedReaders());
    //
    // EventBus.getDefault().unregister(listener1);
    // }
    //
    // @Test
    // public void testAddAndRemoveReaders() throws InterruptedException {
    // ConnectionListener listener1 = new ConnectionListener();
    // EventBus.getDefault().register(listener1);
    //
    // cardTerminal2.add(pcscCardReader1.getCardTerminal());
    // cardTerminal2.add(pcscCardReader2.getCardTerminal());
    //
    // await().atMost(10, SECONDS).until(() -> listener1.getConnectedReaders() == 2);
    // Assert.assertEquals(2, listener1.getConnectedReaders());
    //
    // cardTerminal2.remove(pcscCardReader1.getCardTerminal());
    // cardTerminal2.remove(pcscCardReader2.getCardTerminal());
    //
    // await().atMost(20, SECONDS).until(() -> listener1.getDisConnectedReaders() == 2);
    //
    // Assert.assertEquals(2, listener1.getDisConnectedReaders());
    //
    // await().atMost(10, SECONDS).until(() -> listener1.getInitializedReaders() == 2);
    // Assert.assertEquals(2, listener1.getInitializedReaders());
    // EventBus.getDefault().unregister(listener1);
    // }
    //
    // static class ConnectionListener {
    //
    // private int connectedReaders = 0;
    // private int disconnectedReaders = 0;
    // private int initializedReaders = 0;
    //
    // @Subscribe
    // public void cardReaderConnected(final CardReaderConnectedEvent connectedEvent) {
    // connectedReaders += 1;
    // if (connectedEvent.getInitStatus() == InitializationStatus.INIT_SUCCESS) {
    // ++initializedReaders;
    // }
    // }
    //
    // @Subscribe
    // public void cardReaderDisconnected(final CardReaderDisconnectedEvent disconnectedEvent) {
    // disconnectedReaders += 1;
    // }
    //
    // public int getConnectedReaders() {
    // return connectedReaders;
    // }
    //
    // public int getDisConnectedReaders() {
    // return disconnectedReaders;
    // }
    //
    // public int getInitializedReaders() {
    // return initializedReaders;
    // }
    // }

}
