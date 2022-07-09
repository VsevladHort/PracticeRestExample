package homework_processing.implementations;

import dao.Dao;
import dao.DaoImplInMemory;
import dao.exceptions.DaoWrapperException;
import entities.Good;
import entities.GoodGroup;
import homework_processing.abstractions.Encryptor;
import homework_processing.abstractions.Processor;
import packets.abstractions.Message;
import packets.implementations.MessageImpl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static packets.Constants.*;

public class ProcessorImpl implements Processor {
    private static final String ERROR_MESSAGE = "Malformed message";
    private final Encryptor encryptor;
    private int bUserId;

    public ProcessorImpl(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public void process(Message message) {
        Dao dao = new DaoImplInMemory();
        String info = new String(message.getMessage());
        bUserId = message.getBUserId();
        String[] contentSplit = info.split(";");
        try {
            switch (message.getCType()) {
                case TYPE_REQUEST_ADD_GROUP -> addGroup(dao, contentSplit);
                case TYPE_REQUEST_ADD_GOOD -> addGood(dao, contentSplit);
                case TYPE_REQUEST_ADD_GOOD_AMOUNT -> addGoodAmount(dao, contentSplit);
                case TYPE_REQUEST_LOWER_GOOD_AMOUNT -> lowerGoodAmount(dao, contentSplit);
                case TYPE_REQUEST_FIND_GOOD_AMOUNT -> findGoodAmount(dao, contentSplit);
                case TYPE_REQUEST_SET_PRICE_FOR_GOOD -> setGoodPrice(dao, contentSplit);
                default -> throw new IllegalStateException("Unexpected message code: " + message.getCType());
            }
        } catch (DaoWrapperException e) {
            try {
                new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("SOMETHING WENT BAD".getBytes(StandardCharsets.UTF_8),
                        TYPE_RESPONSE_OK, bUserId)), InetAddress.getLocalHost());
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void addGroup(Dao dao, String[] contentSplit) throws DaoWrapperException {
        if (contentSplit.length != 2)
            throw new IllegalStateException(ERROR_MESSAGE);
        dao.createGroup(new GoodGroup(contentSplit[0], contentSplit[1]));
        try {
            new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("".getBytes(StandardCharsets.UTF_8),
                    TYPE_RESPONSE_OK, bUserId)), InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void addGood(Dao dao, String[] contentSplit) throws DaoWrapperException {
        System.out.println("Here i am");
        if (contentSplit.length != 2)
            throw new IllegalStateException(ERROR_MESSAGE);
        dao.createGood(contentSplit[0], new Good(contentSplit[1]));
        try {
            new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("".getBytes(StandardCharsets.UTF_8),
                    TYPE_RESPONSE_OK, bUserId)), InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void addGoodAmount(Dao dao, String[] contentSplit) throws DaoWrapperException {
        if (contentSplit.length != 3)
            throw new IllegalStateException(ERROR_MESSAGE);
        Good good = dao.getGood(contentSplit[0], contentSplit[1]);
        good.setAmount(good.getAmount() + Integer.parseInt(contentSplit[2]));
        try {
            new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("".getBytes(StandardCharsets.UTF_8),
                    TYPE_RESPONSE_OK, bUserId)), InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void lowerGoodAmount(Dao dao, String[] contentSplit) throws DaoWrapperException {
        if (contentSplit.length != 3)
            throw new IllegalStateException(ERROR_MESSAGE);
        Good good = dao.getGood(contentSplit[0], contentSplit[1]);
        good.setAmount(good.getAmount() - Integer.parseInt(contentSplit[2]));
        try {
            new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("".getBytes(StandardCharsets.UTF_8),
                    TYPE_RESPONSE_OK, bUserId)), InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void findGoodAmount(Dao dao, String[] contentSplit) throws DaoWrapperException {
        if (contentSplit.length != 2)
            throw new IllegalStateException(ERROR_MESSAGE);
        Good good = dao.getGood(contentSplit[0], contentSplit[1]);
        try {
            new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl(String.valueOf(good.getAmount())
                    .getBytes(StandardCharsets.UTF_8),
                    TYPE_RESPONSE_OK, bUserId)), InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void setGoodPrice(Dao dao, String[] contentSplit) throws DaoWrapperException {
        if (contentSplit.length != 3)
            throw new IllegalStateException(ERROR_MESSAGE);
        Good good = dao.getGood(contentSplit[0], contentSplit[1]);
        good.setPrice(Double.parseDouble(contentSplit[2]));
        try {
            new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("".getBytes(StandardCharsets.UTF_8),
                    TYPE_RESPONSE_OK, bUserId)), InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
