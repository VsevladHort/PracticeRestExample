import org.junit.jupiter.api.Assertions;
import packets.abstractions.MessageWrapper;
import packets.abstractions.ReceivedPacket;
import packets.exceptions.DiscardException;
import packets.implementations.MessageWrapperImpl;
import packets.implementations.ReceivedPacketImpl;
import packets.utils.implementations.CRCCalculatorImplementation;

import java.nio.charset.StandardCharsets;

public class MultiThreadingTest {
    @org.junit.jupiter.api.Test
    void testMultithreading() throws DiscardException {
        String[] messages = {""};
    }
}
