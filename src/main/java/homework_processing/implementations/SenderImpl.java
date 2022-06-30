package homework_processing.implementations;

import homework_processing.abstractions.Sender;

import java.net.InetAddress;

public class SenderImpl implements Sender {
    @Override
    public void sendMessage(byte[] mess, InetAddress target) {
        System.out.println("Stuff was sent: " + Thread.currentThread().getName());
    }
}
