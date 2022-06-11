package packets.implementations;

import packets.Constants;
import packets.abstractions.Message;

import java.nio.ByteBuffer;

public class MessageImpl implements Message {
    private int cType;
    private int bUserId;
    private byte[] message;

    public MessageImpl(ByteBuffer bytes, int wLen) {
        cType = bytes.getInt(Constants.OFFSET_MSG + Constants.MSG_OFFSET_C_TYPE);
        bUserId = bytes.getInt(Constants.OFFSET_MSG + Constants.MSG_OFFSET_B_USER_ID);
        message = new byte[wLen];
        bytes.get(Constants.OFFSET_MSG + Constants.MSG_OFFSET_MESSAGE, message);
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
