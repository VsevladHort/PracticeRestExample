package packets.implementations;

import packets.Constants;
import packets.abstractions.MessageWrapper;
import packets.exceptions.DiscardException;
import packets.utils.abstractions.CRCCalculator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageWrapperImpl implements MessageWrapper {
    private final CRCCalculator calculator;
    private static final Logger LOGGER = Logger.getLogger(MessageWrapperImpl.class.getName());

    public MessageWrapperImpl(CRCCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public byte[] wrap(byte[] message, byte bSrc, long bPktId, int cType, int bUserId, Cipher cipher) throws DiscardException {
        byte[] bytes = new byte[message.length + Constants.MIN_LENGTH];
        int currentIndex;
        bytes[Constants.OFFSET_MAGIC] = Constants.MAGIC;
        bytes[Constants.OFFSET_SRC] = bSrc;
        currentIndex = Constants.OFFSET_PKT_ID;
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            bytes[currentIndex] = (byte) ((bPktId >>> i) & 0xff);
            currentIndex++;
        }
        for (int i = Integer.BYTES - 1; i >= 0; i--) {
            bytes[currentIndex] = (byte) ((message.length >>> i) & 0xff);
            currentIndex++;
        }
        short crc16n1 = calculator.calculate(bytes, 0, currentIndex);
        LOGGER.log(Level.INFO, "crc16n1 in wrapper: " + crc16n1);
        for (int i = Short.BYTES - 1; i >= 0; i--) {
            bytes[currentIndex] = (byte) ((crc16n1 >>> i) & 0xff);
            currentIndex++;
        }
        byte[] msgToCipher = new byte[8 + message.length];
        int currentMessageToCipherIndex = 0;
        for (int i = Integer.BYTES - 1; i >= 0; i--) {
            msgToCipher[currentMessageToCipherIndex] = (byte) ((cType >>> i) & 0xff);
            currentMessageToCipherIndex++;
        }
        for (int i = Integer.BYTES - 1; i >= 0; i--) {
            msgToCipher[currentMessageToCipherIndex] = (byte) ((bUserId >>> i) & 0xff);
            currentMessageToCipherIndex++;
        }
        for (byte b : message) {
            msgToCipher[currentMessageToCipherIndex] = b;
            currentMessageToCipherIndex++;
        }
        byte[] cipheredMessage;
        try {
            cipheredMessage = cipher.doFinal(msgToCipher);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new DiscardException(e.getMessage());
        }
        if (cipheredMessage != null) {
            for (byte b : message) {
                bytes[currentIndex] = b;
                currentIndex++;
            }
            short crc16n2 = calculator.calculate(cipheredMessage, 0, cipheredMessage.length);
            LOGGER.log(Level.INFO, "crc16n2 in wrapper: " + crc16n2);
            for (int i = Short.BYTES - 1; i >= 0; i--) {
                bytes[currentIndex] = (byte) ((crc16n2 >>> i) & 0xff);
                currentIndex++;
            }
        }
        return bytes;
    }
}
