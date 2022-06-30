package homework_processing.implementations;

import homework_processing.abstractions.Decryptor;
import homework_processing.abstractions.Receiver;

public class ReceiverImpl implements Receiver {
    private Thread threadLaunched;
    private final byte[] bytes;
    private final Decryptor decryptor;

    public ReceiverImpl(byte[] messages) {
        bytes = messages;
        decryptor = new DecryptorImpl();
    }

    @Override
    public void receiveMessage() {
        threadLaunched = new Thread(() -> decryptor.decrypt(bytes));
    }

    public Thread getThreadLaunched() {
        return threadLaunched;
    }
}
