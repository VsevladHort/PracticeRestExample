package packets.implementations;

import packets.Constants;
import packets.abstractions.Message;
import packets.abstractions.Packet;
import packets.exceptions.DiscardException;
import packets.utils.abstractions.CRCCalculator;
import packets.utils.implementations.CRCCalculatorImplementation;

import java.nio.ByteBuffer;

public class PacketImpl implements Packet {
    private final byte bMagic;
    private final byte bSrc;
    private final long bPktId;
    private final int wLen;
    private final short wCrc16n1;
    private final Message message;
    private final short wCrc16n2;

    public PacketImpl(byte[] bytes, int startIndex) throws DiscardException {
        if (bytes[startIndex] != Constants.MAGIC)
            throw new DiscardException("Incorrect magic byte");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        bMagic = buffer.get(startIndex + Constants.OFFSET_MAGIC);
        bSrc = buffer.get(startIndex + Constants.OFFSET_SRC);
        bPktId = buffer.getLong(startIndex + Constants.OFFSET_PKT_ID);
        wLen = buffer.getInt(startIndex + Constants.OFFSET_LEN);
        wCrc16n1 = buffer.getShort(startIndex + Constants.OFFSET_CRC);
        CRCCalculator calculator = CRCCalculatorImplementation.provide();
        if (wCrc16n1 != calculator.calculate(bytes, startIndex, Constants.OFFSET_CRC))
            throw new DiscardException("wCrc16n1 did not match");
        wCrc16n2 = buffer.getShort(startIndex + Constants.OFFSET_MSG + wLen);
        if (wCrc16n2 != calculator.calculate(bytes,
                startIndex + Constants.OFFSET_MSG,
                Constants.OFFSET_MSG + wLen))
            throw new DiscardException("wCrc16n2 did not match");
        message = new MessageImpl(buffer, wLen, startIndex);
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
