import dao.Dao;
import dao.DaoImplInMemory;
import dao.exceptions.DaoWrapperException;
import entities.SomethingLikeInMemoryDatabase;
import homework_processing.implementations.ReceiverImpl;
import homework_processing.implementations.ReceiverImplInMemory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import packets.Constants;
import packets.abstractions.MessageWrapper;
import packets.exceptions.DiscardException;
import packets.implementations.MessageWrapperImpl;
import packets.utils.implementations.CRCCalculatorImplementation;
import packets.utils.implementations.CiphererSimpleImpl;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class MultiThreadingTest {
    @org.junit.jupiter.api.Test
    void testAddGroups() throws InterruptedException {
        SomethingLikeInMemoryDatabase.clear();
        String[] messages = {"1;2", "1;2", "2;3", "3;4", "4;5"};
        List<ReceiverImplInMemory> listLaunched = new ArrayList<>();
        MessageWrapper messageWrapper = new MessageWrapperImpl(CRCCalculatorImplementation.provide());
        Arrays.stream(messages).forEach(it -> {
            try {
                listLaunched.add(new ReceiverImplInMemory(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
                        (byte) 0, 0, Constants.TYPE_REQUEST_ADD_GROUP, 0, new CiphererSimpleImpl())));
            } catch (DiscardException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        Collections.shuffle(listLaunched);
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
        Assertions.assertEquals("1", SomethingLikeInMemoryDatabase.getInstance().groups.get("1").getName());
        Assertions.assertEquals("2", SomethingLikeInMemoryDatabase.getInstance().groups.get("1").getDescription());
        Assertions.assertEquals("4", SomethingLikeInMemoryDatabase.getInstance().groups.get("3").getDescription());
        Assertions.assertEquals("5", SomethingLikeInMemoryDatabase.getInstance().groups.get("4").getDescription());
        Assertions.assertEquals("4", SomethingLikeInMemoryDatabase.getInstance().groups.get("4").getName());
    }

    @Test
    void testAddGood() throws InterruptedException, DaoWrapperException {
        SomethingLikeInMemoryDatabase.clear();
        String[] messages1 = {"1;Good1", "1;Good2", "1;Good3"};
        String[] messages2 = {"1;2", "1;2", "2;3", "3;4", "4;5"};
        List<ReceiverImplInMemory> listLaunched = new ArrayList<>();
        MessageWrapper messageWrapper = new MessageWrapperImpl(CRCCalculatorImplementation.provide());
        Arrays.stream(messages2).forEach(it -> {
            try {
                listLaunched.add(new ReceiverImplInMemory(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
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
                listLaunched.add(new ReceiverImplInMemory(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
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
        DaoImplInMemory dao = new DaoImplInMemory();
        Assertions.assertEquals("Good1", dao.getGood("1", "Good1").getName());
        Assertions.assertEquals("Good2", dao.getGood("1", "Good2").getName());
        Assertions.assertEquals("Good3", dao.getGood("1", "Good3").getName());
    }

    @Test
    void testAddGoodAmount() throws InterruptedException, DaoWrapperException {
        SomethingLikeInMemoryDatabase.clear();
        String[] messages1 = {"1;Good1", "1;Good2", "1;Good3"};
        String[] messages3 = {"1;Good1;10", "1;Good1;30", "1;Good1;60"};
        String[] messages2 = {"1;2", "1;2", "2;3", "3;4", "4;5"};
        List<ReceiverImplInMemory> listLaunched = new ArrayList<>();
        MessageWrapper messageWrapper = new MessageWrapperImpl(CRCCalculatorImplementation.provide());
        Arrays.stream(messages2).forEach(it -> {
            try {
                listLaunched.add(new ReceiverImplInMemory(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
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
                listLaunched.add(new ReceiverImplInMemory(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
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
        DaoImplInMemory dao = new DaoImplInMemory();
        listLaunched.clear();
        Arrays.stream(messages3).forEach(it -> {
            try {
                listLaunched.add(new ReceiverImplInMemory(messageWrapper.wrap(it.getBytes(StandardCharsets.UTF_8),
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
