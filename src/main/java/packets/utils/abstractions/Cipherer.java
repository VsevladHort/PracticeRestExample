package packets.utils.abstractions;

import packets.exceptions.CipherException;

public interface Cipherer {
    byte[] cipher(byte[] info) throws CipherException;

    byte[] decipher(byte[] info) throws CipherException;
}
