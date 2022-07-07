package homework_processing.abstractions;

import packets.exceptions.DiscardException;

import java.net.UnknownHostException;

public interface Receiver {
    void receiveMessage() throws DiscardException, UnknownHostException;
}
