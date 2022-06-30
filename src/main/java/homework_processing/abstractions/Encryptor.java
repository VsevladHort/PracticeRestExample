package homework_processing.abstractions;

import packets.abstractions.Message;

public interface Encryptor {
    byte[] encrypt(Message message);
}
