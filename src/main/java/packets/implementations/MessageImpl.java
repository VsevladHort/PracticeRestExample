package packets.implementations;

import packets.Constants;
import packets.abstractions.Message;
import packets.exceptions.CipherException;
import packets.exceptions.DiscardException;
import packets.utils.abstractions.Cipherer;

import java.nio.ByteBuffer;

public class MessageImpl implements Message {
    private final int cType;
    private final int bUserId;
    private final byte[] message;

    public MessageImpl(ByteBuffer bytes, int wLen, int startIndex, Cipherer cipher) throws DiscardException {
        byte[] cipheredMessage = new byte[wLen];
        byte[] decipheredMessage;
        bytes.get(startIndex + Constants.OFFSET_MSG, cipheredMessage);
        try {
            decipheredMessage = cipher.decipher(cipheredMessage);
        } catch (CipherException e) {
            throw new DiscardException(e.getMessage());
        }
        bytes = ByteBuffer.wrap(decipheredMessage);
        cType = bytes.getInt(Constants.MSG_OFFSET_C_TYPE);
        bUserId = bytes.getInt(Constants.MSG_OFFSET_B_USER_ID);
        message = new byte[decipheredMessage.length - Constants.MSG_OFFSET_MESSAGE];
        bytes.get(Constants.MSG_OFFSET_MESSAGE, message);
    }

    public MessageImpl(byte[] message, int cType, int bUserId) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = new byte[message.length];
        System.arraycopy(message, 0, this.message, 0, message.length);
    }

    @Override
    public int getCType() {
        return cType;
    }

    @Override
    public int getBUserId() {
        return bUserId;
    }

    @Override
    public byte[] getMessage() {
        return message;
    }
}
