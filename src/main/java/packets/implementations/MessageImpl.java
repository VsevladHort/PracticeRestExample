package packets.implementations;

import packets.abstractions.Message;

public class MessageImpl implements Message {
    private int cType;
    private int bUserId;
    private byte[] message;

    public MessageImpl(byte[] bytes, int offsetToMessage) {

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
