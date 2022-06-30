package homework_processing.implementations;

import homework_processing.abstractions.Decryptor;
import homework_processing.abstractions.Processor;
import packets.abstractions.ReceivedPacket;
import packets.exceptions.DiscardException;
import packets.implementations.MessageWrapperImpl;
import packets.implementations.ReceivedPacketImpl;
import packets.utils.implementations.CRCCalculatorImplementation;
import packets.utils.implementations.CiphererSimpleImpl;

public class DecryptorImpl implements Decryptor {
    @Override
    public void decrypt(byte[] message) {
        try {
            ReceivedPacket packet = new ReceivedPacketImpl(message, 0, new CiphererSimpleImpl());
            Processor processor = new ProcessorImpl(new EncryptorImpl(new MessageWrapperImpl(CRCCalculatorImplementation.provide())));
            processor.process(packet.getMessage());
        } catch (DiscardException ignored) {
        }
    }
}
