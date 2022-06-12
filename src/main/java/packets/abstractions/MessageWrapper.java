package packets.abstractions;

import packets.exceptions.DiscardException;

import javax.crypto.Cipher;

public interface MessageWrapper {
    byte[] wrap(byte[] message, byte bSrc, long bPktId, int cType, int bUserId, Cipher cipher) throws DiscardException;
}
