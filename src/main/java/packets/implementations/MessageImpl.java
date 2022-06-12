package packets.implementations;

import packets.Constants;
import packets.abstractions.Message;
import packets.exceptions.DiscardException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;
import java.util.Base64;

public class MessageImpl implements Message {
    private final int cType;
    private final int bUserId;
    private final byte[] message;

    public MessageImpl(ByteBuffer bytes, int wLen, int startIndex, Cipher cipher) throws DiscardException {
        byte[] cipheredMessage = new byte[wLen];
        byte[] decipheredMessage;
        bytes.get(startIndex + Constants.OFFSET_MSG, cipheredMessage);
        try {
            decipheredMessage = cipher.doFinal(cipheredMessage);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new DiscardException(e.getMessage());
        }
        bytes = ByteBuffer.wrap(decipheredMessage);
        cType = bytes.getInt(Constants.MSG_OFFSET_C_TYPE);
        bUserId = bytes.getInt(Constants.MSG_OFFSET_B_USER_ID);
        message = new byte[decipheredMessage.length - Constants.MSG_OFFSET_MESSAGE];
        bytes.get(Constants.MSG_OFFSET_MESSAGE, message);
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
