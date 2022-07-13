package homework_processing.implementations;

import homework_processing.abstractions.Decryptor;
import homework_processing.abstractions.Receiver;
import packets.exceptions.DiscardException;

import java.net.UnknownHostException;

public class ReceiverImplInMemory implements Receiver {
    private Thread threadLaunched;
    private final byte[] bytes;
    private final Decryptor decryptor;

    public ReceiverImplInMemory(byte[] messages) {
        bytes = messages;
        decryptor = new DecryptorImplInMemory();
    }

    @Override
    public void receiveMessage() throws DiscardException, UnknownHostException {
        decryptor.decrypt(bytes);
    }

    /**
     * returns a thread launches by the receiver, null if no thread was launched
     */
    public Thread getThreadLaunched() {
        return threadLaunched;
    }
}
