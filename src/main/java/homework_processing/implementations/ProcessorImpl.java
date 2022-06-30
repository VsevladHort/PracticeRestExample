package homework_processing.implementations;

import entities.GoodGroup;
import entities.SomethingLikeInMemoryDatabase;
import homework_processing.abstractions.Processor;
import packets.abstractions.Message;

public class ProcessorImpl implements Processor {
    @Override
    public void process(Message message) {
        SomethingLikeInMemoryDatabase db = SomethingLikeInMemoryDatabase.getInstance();
        String info = new String(message.getMessage());
        switch (message.getCType()) {
            case 1:
                String[] contentSplit = info.split(";");
                if (contentSplit.length != 2)
                    throw new IllegalStateException("Malformed message");
                // db.groups.add(new GoodGroup(contentSplit[0], contentSplit[1]));
        }
    }
}
