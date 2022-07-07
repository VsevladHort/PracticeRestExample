import dao.Dao;
import dao.DaoImplInMemory;
import entities.SomethingLikeInMemoryDatabase;
import homework_processing.implementations.ReceiverImpl;
import networking.tcp.StoreServerTCP;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import packets.Constants;
import packets.abstractions.MessageWrapper;
import packets.exceptions.DiscardException;
import packets.implementations.MessageWrapperImpl;
import packets.utils.implementations.CRCCalculatorImplementation;
import packets.utils.implementations.CiphererSimpleImpl;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class TCPServerTest {
    private static StoreServerTCP storeServerTCP;
    private static final int PORT = 1337;

    @BeforeAll
    static void before() throws IOException {
        storeServerTCP = new StoreServerTCP(PORT);
        new Thread(storeServerTCP).start();
    }

    @AfterAll
    static void after() throws IOException {
        storeServerTCP.stop();
    }

    @org.junit.jupiter.api.Test
    void testAddGroups() throws InterruptedException {
        SomethingLikeInMemoryDatabase.clear();
        String[] messages = {"1;2", "1;2", "2;3", "3;4", "4;5"};
        List<ReceiverImpl> listLaunched = new ArrayList<>();
        MessageWrapper messageWrapper = new MessageWrapperImpl(CRCCalculatorImplementation.provide());
        Arrays.stream(messages).forEach(it -> {
            try {
                listLaunched.add(new ReceiverImpl(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
                        (byte) 0, 0, Constants.TYPE_REQUEST_ADD_GROUP, 0, new CiphererSimpleImpl())));
            } catch (DiscardException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        Collections.shuffle(listLaunched);
        listLaunched.forEach(it -> {
            try {
                it.receiveMessage();
            } catch (DiscardException | UnknownHostException e) {
                e.printStackTrace();
            }
        });
        for (Thread thread : storeServerTCP.getRunningThreads()) {
            if (thread != null)
                thread.join();
        }
        Assertions.assertEquals("1", SomethingLikeInMemoryDatabase.getInstance().groups.get("1").getName());
        Assertions.assertEquals("2", SomethingLikeInMemoryDatabase.getInstance().groups.get("1").getDescription());
        Assertions.assertEquals("4", SomethingLikeInMemoryDatabase.getInstance().groups.get("3").getDescription());
        Assertions.assertEquals("5", SomethingLikeInMemoryDatabase.getInstance().groups.get("4").getDescription());
        Assertions.assertEquals("4", SomethingLikeInMemoryDatabase.getInstance().groups.get("4").getName());
    }

    @Test
    void testAddGood() throws InterruptedException {
        SomethingLikeInMemoryDatabase.clear();
        String[] messages1 = {"1;Good1", "1;Good2", "1;Good3"};
        String[] messages2 = {"1;2", "1;2", "2;3", "3;4", "4;5"};
        List<ReceiverImpl> listLaunched = new ArrayList<>();
        MessageWrapper messageWrapper = new MessageWrapperImpl(CRCCalculatorImplementation.provide());
        Arrays.stream(messages2).forEach(it -> {
            try {
                listLaunched.add(new ReceiverImpl(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
                        (byte) 0, 0, Constants.TYPE_REQUEST_ADD_GROUP, 0, new CiphererSimpleImpl())));
            } catch (DiscardException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        List<Thread> listOfThreadsLaunched = new ArrayList<>();
        listLaunched.forEach(it -> {
            try {
                it.receiveMessage();
            } catch (DiscardException | UnknownHostException e) {
                e.printStackTrace();
            }
            listOfThreadsLaunched.add(it.getThreadLaunched());
        });
        for (Thread thread : listOfThreadsLaunched) {
            if (thread != null)
                thread.join();
        }
        listLaunched.clear();
        Arrays.stream(messages1).forEach(it -> {
            try {
                listLaunched.add(new ReceiverImpl(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
                        (byte) 0, 0, Constants.TYPE_REQUEST_ADD_GOOD, 0, new CiphererSimpleImpl())));
            } catch (DiscardException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        Collections.shuffle(listLaunched);
        listLaunched.forEach(it -> {
            try {
                it.receiveMessage();
            } catch (DiscardException | UnknownHostException e) {
                e.printStackTrace();
            }
            listOfThreadsLaunched.add(it.getThreadLaunched());
        });
        for (Thread thread : listOfThreadsLaunched) {
            if (thread != null)
                thread.join();
        }
        Dao dao = new DaoImplInMemory();
        Assertions.assertEquals("Good1", dao.getGood("1", "Good1").getName());
        Assertions.assertEquals("Good2", dao.getGood("1", "Good2").getName());
        Assertions.assertEquals("Good3", dao.getGood("1", "Good3").getName());
    }

    @Test
    void testAddGoodAmount() throws InterruptedException {
        SomethingLikeInMemoryDatabase.clear();
        String[] messages1 = {"1;Good1", "1;Good2", "1;Good3"};
        String[] messages3 = {"1;Good1;10", "1;Good1;30", "1;Good1;60"};
        String[] messages2 = {"1;2", "1;2", "2;3", "3;4", "4;5"};
        List<ReceiverImpl> listLaunched = new ArrayList<>();
        MessageWrapper messageWrapper = new MessageWrapperImpl(CRCCalculatorImplementation.provide());
        Arrays.stream(messages2).forEach(it -> {
            try {
                listLaunched.add(new ReceiverImpl(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
                        (byte) 0, 0, Constants.TYPE_REQUEST_ADD_GROUP, 0, new CiphererSimpleImpl())));
            } catch (DiscardException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        List<Thread> listOfThreadsLaunched = new ArrayList<>();
        listLaunched.forEach(it -> {
            try {
                it.receiveMessage();
            } catch (DiscardException | UnknownHostException e) {
                e.printStackTrace();
            }
            listOfThreadsLaunched.add(it.getThreadLaunched());
        });
        for (Thread thread : listOfThreadsLaunched) {
            if (thread != null)
                thread.join();
        }
        listLaunched.clear();
        Arrays.stream(messages1).forEach(it -> {
            try {
                listLaunched.add(new ReceiverImpl(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
                        (byte) 0, 0, Constants.TYPE_REQUEST_ADD_GOOD, 0, new CiphererSimpleImpl())));
            } catch (DiscardException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        Collections.shuffle(listLaunched);
        listLaunched.forEach(it -> {
            try {
                it.receiveMessage();
            } catch (DiscardException | UnknownHostException e) {
                e.printStackTrace();
            }
            listOfThreadsLaunched.add(it.getThreadLaunched());
        });
        for (Thread thread : listOfThreadsLaunched) {
            if (thread != null)
                thread.join();
        }
        Dao dao = new DaoImplInMemory();
        listLaunched.clear();
        Arrays.stream(messages3).forEach(it -> {
            try {
                listLaunched.add(new ReceiverImpl(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
                        (byte) 0, 0, Constants.TYPE_REQUEST_ADD_GOOD_AMOUNT, 0, new CiphererSimpleImpl())));
            } catch (DiscardException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        Collections.shuffle(listLaunched);
        listLaunched.forEach(it -> {
            try {
                it.receiveMessage();
            } catch (DiscardException | UnknownHostException e) {
                e.printStackTrace();
            }
            listOfThreadsLaunched.add(it.getThreadLaunched());
        });
        for (Thread thread : listOfThreadsLaunched) {
            if (thread != null)
                thread.join();
        }
        Assertions.assertEquals(100, dao.getGood("1", "Good1").getAmount());
    }
}
