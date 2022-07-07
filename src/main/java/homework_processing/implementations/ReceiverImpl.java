package homework_processing.implementations;

import homework_processing.abstractions.Decryptor;
import homework_processing.abstractions.Receiver;
import packets.exceptions.DiscardException;

import java.net.UnknownHostException;

public class ReceiverImpl implements Receiver {
    private Thread threadLaunched;
    private final byte[] bytes;
    private final Decryptor decryptor;

    public ReceiverImpl(byte[] messages) {
        bytes = messages;
        decryptor = new DecryptorImpl();
    }

    @Override
    public void receiveMessage() throws DiscardException, UnknownHostException {
        decryptor.decrypt(bytes);
    }

    /**
     * returns a thread launches by the receiver, null if not thread was launched
     */
    public Thread getThreadLaunched() {
        return threadLaunched;
    }
}
