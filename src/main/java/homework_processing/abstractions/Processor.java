package homework_processing.abstractions;

import packets.abstractions.Message;
import packets.exceptions.DiscardException;

import java.net.UnknownHostException;

public interface Processor {
    void process(Message message) throws DiscardException, UnknownHostException;
}
