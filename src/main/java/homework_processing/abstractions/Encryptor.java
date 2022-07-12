package homework_processing.abstractions;

import packets.abstractions.Message;
import packets.exceptions.DiscardException;

public interface Encryptor {
    byte[] encrypt(Message message) throws DiscardException;
}
