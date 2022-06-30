package homework_processing.implementations;

import homework_processing.abstractions.Encryptor;
import packets.abstractions.Message;
import packets.abstractions.MessageWrapper;
import packets.exceptions.DiscardException;
import packets.utils.implementations.CiphererSimpleImpl;

public class EncryptorImpl implements Encryptor {
    private final MessageWrapper messageWrapper;
    private long pktId = 0;

    public EncryptorImpl(MessageWrapper messageWrapper) {
        this.messageWrapper = messageWrapper;
    }

    @Override
    public byte[] encrypt(Message message) {
        try {
            return messageWrapper.wrap(message.getMessage(), (byte) 0, pktId++, message.getCType(), message.getBUserId(), new CiphererSimpleImpl());
        } catch (DiscardException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
