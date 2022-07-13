package homework_processing.implementations;

import dao.DBService;
import dao.Dao;
import dao.DaoImplInMemory;
import dao.exceptions.DaoWrapperException;
import entities.Good;
import entities.GoodGroup;
import homework_processing.abstractions.Encryptor;
import homework_processing.abstractions.Processor;
import packets.abstractions.Message;
import packets.exceptions.DiscardException;
import packets.implementations.MessageImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static packets.Constants.*;

public class ProcessorImplInMemory implements Processor {
    private static final String ERROR_MESSAGE = "Malformed message";
    private final Encryptor encryptor;
    private int bUserId;
    private InetAddress inetAddress;
    private static final Logger LOGGER = Logger.getLogger(DBService.class.getCanonicalName());

    static {
        try {
            LOGGER.addHandler(new FileHandler("logs/processorLogs.txt"));
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Could not add File handler for processor logging");
        }
    }

    public ProcessorImplInMemory(Encryptor encryptor) {
        this.encryptor = encryptor;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public ProcessorImplInMemory(Encryptor encryptor, InetAddress inetAddress) {
        this.encryptor = encryptor;
        this.inetAddress = inetAddress;
    }

    @Override
    public void process(Message message) throws DiscardException {
        DaoImplInMemory dao = new DaoImplInMemory();
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

        }
    }

    private void addGroup(DaoImplInMemory dao, String[] contentSplit) throws DiscardException, DaoWrapperException {
        if (contentSplit.length != 2)
            throw new IllegalStateException(ERROR_MESSAGE);
        dao.createGroup(new GoodGroup(contentSplit[0], contentSplit[1]));
        new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("".getBytes(StandardCharsets.UTF_8),
                TYPE_RESPONSE_OK, bUserId)), inetAddress);
    }

    private void addGood(DaoImplInMemory dao, String[] contentSplit) throws DiscardException, DaoWrapperException {
        if (contentSplit.length != 2)
            throw new IllegalStateException(ERROR_MESSAGE);
        dao.createGood(contentSplit[0], new Good(contentSplit[1]));
        new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("".getBytes(StandardCharsets.UTF_8),
                TYPE_RESPONSE_OK, bUserId)), inetAddress);
    }

    private void addGoodAmount(DaoImplInMemory dao, String[] contentSplit) throws DiscardException, DaoWrapperException {
        if (contentSplit.length != 3)
            throw new IllegalStateException(ERROR_MESSAGE);
        Good good = dao.getGood(contentSplit[0], contentSplit[1]);
        good.setAmount(good.getAmount() + Integer.parseInt(contentSplit[2]));
        new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("".getBytes(StandardCharsets.UTF_8),
                TYPE_RESPONSE_OK, bUserId)), inetAddress);
    }

    private void lowerGoodAmount(DaoImplInMemory dao, String[] contentSplit) throws DiscardException, DaoWrapperException {
        if (contentSplit.length != 3)
            throw new IllegalStateException(ERROR_MESSAGE);
        Good good = dao.getGood(contentSplit[0], contentSplit[1]);
        good.setAmount(good.getAmount() - Integer.parseInt(contentSplit[2]));
        new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("".getBytes(StandardCharsets.UTF_8),
                TYPE_RESPONSE_OK, bUserId)), inetAddress);
    }

    private void findGoodAmount(DaoImplInMemory dao, String[] contentSplit) throws DiscardException, DaoWrapperException {
        if (contentSplit.length != 2)
            throw new IllegalStateException(ERROR_MESSAGE);
        Good good = dao.getGood(contentSplit[0], contentSplit[1]);
        new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl(String.valueOf(good.getAmount())
                .getBytes(StandardCharsets.UTF_8),
                TYPE_RESPONSE_OK, bUserId)), inetAddress);
    }

    private void setGoodPrice(DaoImplInMemory dao, String[] contentSplit) throws DiscardException, DaoWrapperException {
        if (contentSplit.length != 3)
            throw new IllegalStateException(ERROR_MESSAGE);
        Good good = dao.getGood(contentSplit[0], contentSplit[1]);
        good.setPrice(Double.parseDouble(contentSplit[2]));
        new SenderImpl().sendMessage(encryptor.encrypt(new MessageImpl("".getBytes(StandardCharsets.UTF_8),
                TYPE_RESPONSE_OK, bUserId)), inetAddress);
    }
}
