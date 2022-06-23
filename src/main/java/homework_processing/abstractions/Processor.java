package homework_processing.abstractions;

import packets.abstractions.Message;

public interface Processor {
    void process(Message message);
}
