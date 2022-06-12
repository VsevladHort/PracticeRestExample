import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import packets.abstractions.MessageWrapper;
import packets.abstractions.ReceivedPacket;
import packets.exceptions.DiscardException;
import packets.implementations.MessageWrapperImpl;
import packets.implementations.ReceivedPacketImpl;
import packets.utils.implementations.CRCCalculatorImplementation;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

class PacketTest {
    private static Cipher cipher1;
    private static Cipher cipher2;

    @BeforeAll
    static void init() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        cipher1 = Cipher.getInstance("AES");
        cipher2 = Cipher.getInstance("AES");
        byte[] salt = new byte[]{
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0
        };
        Key key = new SecretKeySpec(salt, "AES");
        cipher1.init(Cipher.ENCRYPT_MODE, key);
        cipher2.init(Cipher.DECRYPT_MODE, key);
    }

    @org.junit.jupiter.api.Test
    void testWrapperAndReceivedPacketClassInteroperability() throws DiscardException {
        String message = "MESSAGE TO BE PASSED";
        MessageWrapper wrapper = new MessageWrapperImpl(CRCCalculatorImplementation.provide());
        byte srcId = 1;
        long pktId = 2;
        int cType = 3;
        int bUserId = 4;
        byte[] packet = wrapper.wrap(message.getBytes(StandardCharsets.UTF_8), srcId, pktId, cType, bUserId, cipher1);
        ReceivedPacket receivedPacket = new ReceivedPacketImpl(packet, 0, cipher2);
        Assertions.assertEquals(message, new String(receivedPacket.getMessage().getMessage(), StandardCharsets.UTF_8));
        Assertions.assertEquals(srcId, receivedPacket.getBSrc());
        Assertions.assertEquals(pktId, receivedPacket.getBPktId());
        Assertions.assertEquals(cType, receivedPacket.getMessage().getCType());
        Assertions.assertEquals(bUserId, receivedPacket.getMessage().getBUserId());
    }

    @Test()
    void testPacketTooSmallForReceivedPacket() {
        Assertions.assertThrows(DiscardException.class, () -> {
            byte[] salt = new byte[16];
            new ReceivedPacketImpl(salt, 0, cipher2);
        });
        Assertions.assertThrows(DiscardException.class, () -> {
            byte[] salt = new byte[50];
            new ReceivedPacketImpl(salt, 42, cipher2);
        });
    }

    @Test()
    void testMagicByteAbsent() {
        Assertions.assertThrows(DiscardException.class, () -> {
            byte[] salt = new byte[32];
            new ReceivedPacketImpl(salt, 0, cipher2);
        });
    }

    @Test()
    void testIncorrectCrcFirst() {
        Assertions.assertThrows(DiscardException.class, () -> {
            byte[] part1 = new byte[]{
                    19, 1, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 9,
                    0, 1,
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    1,
                    12, 12
            };
            new ReceivedPacketImpl(part1, 0, cipher2);
        });
    }

    @Test()
    void testIncorrectCrcSecond() {
        Assertions.assertThrows(DiscardException.class, () -> {
            byte[] part1 = new byte[]{
                    19, 1, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 9,
                    88, 82,
                    0, 0, 0, 0,
                    0, 0, 0, 0,
                    1,
                    0, 1
            };
            new ReceivedPacketImpl(part1, 0, cipher2);
        });
    }
}
