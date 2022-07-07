package homework_processing.implementations;

import homework_processing.abstractions.Decryptor;
import homework_processing.abstractions.Processor;
import packets.Constants;
import packets.abstractions.ReceivedPacket;
import packets.exceptions.DiscardException;
import packets.implementations.MessageWrapperImpl;
import packets.implementations.ReceivedPacketImpl;
import packets.utils.implementations.CRCCalculatorImplementation;
import packets.utils.implementations.CiphererSimpleImpl;

import java.net.UnknownHostException;

public class DecryptorImpl implements Decryptor {
    @Override
    public void decrypt(byte[] message) throws DiscardException, UnknownHostException {
        int startingIndex = 0;
        for (int i = 0; i < message.length; i++) {
            if (Constants.MAGIC == message[i]) {
                startingIndex = i;
                break;
            }
        }
        ReceivedPacket packet = new ReceivedPacketImpl(message, startingIndex, new CiphererSimpleImpl());
        Processor processor = new ProcessorImpl(new EncryptorImpl(new MessageWrapperImpl(CRCCalculatorImplementation.provide())));
        processor.process(packet.getMessage());
    }
}
