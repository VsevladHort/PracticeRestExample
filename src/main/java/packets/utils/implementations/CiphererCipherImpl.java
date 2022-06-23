package packets.utils.implementations;

import packets.exceptions.CipherException;
import packets.utils.abstractions.Cipherer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class CiphererCipherImpl implements Cipherer {
    private Cipher cipher;
    private Cipher decipher;

    public CiphererCipherImpl(Cipher cipher, Cipher decipher) {
        this.cipher = cipher;
        this.decipher = decipher;
    }

    @Override
    public byte[] cipher(byte[] info) throws CipherException {
        try {
            return cipher.doFinal(info);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new CipherException(e.getMessage(), e);
        }
    }

    @Override
    public byte[] decipher(byte[] info) throws CipherException {
        try {
            return decipher.doFinal(info);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new CipherException(e.getMessage(), e);
        }
    }
}
