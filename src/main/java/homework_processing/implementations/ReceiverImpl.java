package homework_processing.implementations;

import homework_processing.abstractions.Receiver;

import java.util.ArrayList;
import java.util.List;

public class ReceiverImpl implements Receiver {
    private final List<byte[]> listOfMessages;
    private List<Thread> listOfThreadsLaunched;

    public ReceiverImpl(List<byte[]> listOfMessages) {
        this.listOfMessages = listOfMessages;
        listOfThreadsLaunched = new ArrayList<>();
    }

    @Override
    public void receiveMessage() {
        listOfMessages.forEach(receivedPacket -> listOfThreadsLaunched.add(new Thread(() -> {

        })));
        listOfThreadsLaunched.forEach(Thread::start);
        listOfThreadsLaunched.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        });
    }
}
