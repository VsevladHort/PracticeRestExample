package packets.abstractions;

import javax.crypto.Cipher;

public interface MessageWrapper {
    byte[] wrap(byte[] message, byte bSrc, byte bPktId, byte cType, byte bUserId, Cipher cipher);
}
