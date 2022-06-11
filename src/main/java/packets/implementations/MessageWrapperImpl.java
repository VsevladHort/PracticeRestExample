package packets.implementations;

import packets.abstractions.MessageWrapper;

import javax.crypto.Cipher;

public class MessageWrapperImpl implements MessageWrapper {
    @Override
    public byte[] wrap(byte[] message, byte bSrc, byte bPktId, byte cType, byte bUserId, Cipher cipher) {
        return new byte[0];
    }
}
