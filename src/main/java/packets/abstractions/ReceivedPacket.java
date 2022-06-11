package packets.abstractions;

public interface ReceivedPacket {
    byte getBMagic();

    byte getBSrc();

    long getBPktId();

    int getWLen();

    short getWCrc16n1();

    Message getMessage();

    short getWCrc16n2();
}
