package homework_processing.abstractions;

import packets.exceptions.DiscardException;

import java.net.UnknownHostException;

public interface Decryptor {
    void decrypt(byte[] message) throws DiscardException, UnknownHostException;
}
