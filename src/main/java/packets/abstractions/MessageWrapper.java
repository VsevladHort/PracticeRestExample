package packets.abstractions;

import packets.exceptions.DiscardException;
import packets.utils.abstractions.Cipherer;

import javax.crypto.Cipher;

public interface MessageWrapper {
    byte[] wrap(byte[] message, byte bSrc, long bPktId, int cType, int bUserId, Cipherer cipher) throws DiscardException;
}
