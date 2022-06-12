import org.junit.jupiter.api.Assertions;
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
import java.security.SecureRandom;

class PacketTest {
    @org.junit.jupiter.api.Test
    void testWrapperAndReceivedPacketClassInteroperability() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, DiscardException {
        String message = "MESSAGE TO BE PASSED";
        Cipher cipher1 = Cipher.getInstance("AES");
        Cipher cipher2 = Cipher.getInstance("AES");
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        Key key = new SecretKeySpec(salt, "AES");
        cipher1.init(Cipher.ENCRYPT_MODE, key);
        cipher2.init(Cipher.DECRYPT_MODE, key);
        MessageWrapper wrapper = new MessageWrapperImpl(CRCCalculatorImplementation.provide());
        byte[] packet = wrapper.wrap(message.getBytes(StandardCharsets.UTF_8), (byte) 1, 2, 3, 4, cipher1);
        ReceivedPacket receivedPacket = new ReceivedPacketImpl(packet, 0, cipher2);
        Assertions.assertEquals(message, new String(receivedPacket.getMessage().getMessage(), StandardCharsets.UTF_8));
    }
}
