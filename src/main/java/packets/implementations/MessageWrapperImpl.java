package packets.implementations;

import packets.Constants;
import packets.abstractions.MessageWrapper;
import packets.exceptions.CipherException;
import packets.exceptions.DiscardException;
import packets.utils.abstractions.CRCCalculator;
import packets.utils.abstractions.Cipherer;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageWrapperImpl implements MessageWrapper {
    private final CRCCalculator calculator;
    private static final Logger LOGGER = Logger.getLogger(MessageWrapperImpl.class.getName());

    public MessageWrapperImpl(CRCCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public byte[] wrap(byte[] message, byte bSrc, long bPktId, int cType, int bUserId, Cipherer cipher) throws DiscardException {
        ArrayList<Byte> bytes = new ArrayList<>();
        bytes.add(Constants.MAGIC);
        bytes.add(bSrc);
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            bytes.add((byte) ((bPktId >>> i * 8) & 0xff));
        }
        byte[] msgToCipher = new byte[8 + message.length];
        int currentMessageToCipherIndex = 0;
        for (int i = Integer.BYTES - 1; i >= 0; i--) {
            msgToCipher[currentMessageToCipherIndex] = (byte) ((cType >>> i * 8) & 0xff);
            currentMessageToCipherIndex++;
        }
        for (int i = Integer.BYTES - 1; i >= 0; i--) {
            msgToCipher[currentMessageToCipherIndex] = (byte) ((bUserId >>> i * 8) & 0xff);
            currentMessageToCipherIndex++;
        }
        for (byte b : message) {
            msgToCipher[currentMessageToCipherIndex] = b;
            currentMessageToCipherIndex++;
        }
        byte[] cipheredMessage;
        try {
            cipheredMessage = cipher.cipher(msgToCipher);
        } catch (CipherException e) {
            throw new DiscardException(e.getMessage());
        }
        if (cipheredMessage != null) {
            for (int i = Integer.BYTES - 1; i >= 0; i--) {
                bytes.add((byte) ((cipheredMessage.length >>> i * 8) & 0xff));
            }
            short crc16n1 = calculator.calculate(bytes, 0, bytes.size());
            for (int i = Short.BYTES - 1; i >= 0; i--) {
                bytes.add((byte) ((crc16n1 >>> i * 8) & 0xff));
            }
            for (byte b : cipheredMessage) {
                bytes.add(b);
            }
            short crc16n2 = calculator.calculate(cipheredMessage, 0, cipheredMessage.length);
            for (int i = Short.BYTES - 1; i >= 0; i--) {
                bytes.add((byte) ((crc16n2 >>> i * 8) & 0xff));
            }
        }
        int currentIndex = 0;
        byte[] result = new byte[bytes.size()];
        for (byte b : bytes)
            result[currentIndex++] = b;
        return result;
    }
}
