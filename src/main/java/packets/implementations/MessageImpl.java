package packets.implementations;

import packets.Constants;
import packets.abstractions.Message;
import packets.exceptions.DiscardException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;

public class MessageImpl implements Message {
    private final int cType;
    private final int bUserId;
    private final byte[] message;

    public MessageImpl(ByteBuffer bytes, int wLen, int startIndex, Cipher cipher) throws DiscardException {
        cType = bytes.getInt(startIndex + Constants.OFFSET_MSG + Constants.MSG_OFFSET_C_TYPE);
        bUserId = bytes.getInt(startIndex + Constants.OFFSET_MSG + Constants.MSG_OFFSET_B_USER_ID);
        byte[] temp = new byte[wLen - Constants.MSG_OFFSET_MESSAGE];
        bytes.get(startIndex + Constants.OFFSET_MSG + Constants.MSG_OFFSET_MESSAGE, temp);
        try {
            message = cipher.doFinal(temp);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new DiscardException(e.getMessage());
        }
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
