package packets.implementations;

import packets.Constants;
import packets.abstractions.Message;
import packets.abstractions.ReceivedPacket;
import packets.exceptions.DiscardException;
import packets.utils.abstractions.CRCCalculator;
import packets.utils.implementations.CRCCalculatorImplementation;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceivedPacketImpl implements ReceivedPacket {
    private final byte bMagic;
    private final byte bSrc;
    private final long bPktId;
    private final int wLen;
    private final short wCrc16n1;
    private final Message message;
    private final short wCrc16n2;
    private static final Logger LOGGER = Logger.getLogger(ReceivedPacketImpl.class.getName());

    public ReceivedPacketImpl(byte[] bytes, int startIndex, Cipher cipher) throws DiscardException {
        if (bytes.length < Constants.MIN_LENGTH)
            throw new DiscardException("Length of the packet less than min required");
        if (bytes[startIndex] != Constants.MAGIC)
            throw new DiscardException("Incorrect magic byte");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        bMagic = buffer.get(startIndex + Constants.OFFSET_MAGIC);
        bSrc = buffer.get(startIndex + Constants.OFFSET_SRC);
        bPktId = buffer.getLong(startIndex + Constants.OFFSET_PKT_ID);
        wLen = buffer.getInt(startIndex + Constants.OFFSET_LEN);
        wCrc16n1 = buffer.getShort(startIndex + Constants.OFFSET_CRC);
        LOGGER.log(Level.INFO, "crc16n1 in receivedPacket from source: " + wCrc16n1);
        CRCCalculator calculator = CRCCalculatorImplementation.provide();
        LOGGER.log(Level.INFO, "crc16n1 in receivedPacket calculated locally: " +
                calculator.calculate(bytes, startIndex, Constants.OFFSET_CRC));
        if (wCrc16n1 != calculator.calculate(bytes, startIndex, Constants.OFFSET_CRC))
            throw new DiscardException("wCrc16n1 did not match");
        wCrc16n2 = buffer.getShort(startIndex + Constants.OFFSET_MSG + wLen);
        LOGGER.log(Level.INFO, "crc16n2 in receivedPacket from source: " + wCrc16n2);
        LOGGER.log(Level.INFO, "crc16n2 in receivedPacket calculated locally: " +
                calculator.calculate(bytes, startIndex, Constants.OFFSET_CRC));
        if (wCrc16n2 != calculator.calculate(bytes,
                startIndex + Constants.OFFSET_MSG,
                Constants.OFFSET_MSG + wLen))
            throw new DiscardException("wCrc16n2 did not match");
        message = new MessageImpl(buffer, wLen, startIndex, cipher);
    }

    @Override
    public byte getBMagic() {
        return bMagic;
    }

    @Override
    public byte getBSrc() {
        return bSrc;
    }

    @Override
    public long getBPktId() {
        return bPktId;
    }

    @Override
    public int getWLen() {
        return wLen;
    }

    @Override
    public short getWCrc16n1() {
        return wCrc16n1;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public short getWCrc16n2() {
        return wCrc16n2;
    }
}
