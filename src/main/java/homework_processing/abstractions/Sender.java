package homework_processing.abstractions;

import java.net.InetAddress;

public interface Sender {
    void sendMessage(byte[] mess, InetAddress target);
}
