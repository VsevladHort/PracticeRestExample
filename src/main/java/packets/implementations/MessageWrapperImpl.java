package packets.implementations;

import packets.abstractions.MessageWrapper;

public class MessageWrapperImpl implements MessageWrapper {
    @Override
    public byte[] wrap(byte[] message, byte bSrc, byte bPktId, byte cType, byte bUserId) {
        return new byte[0];
    }
}
