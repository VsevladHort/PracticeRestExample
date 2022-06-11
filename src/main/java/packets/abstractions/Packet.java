package packets.abstractions;

public interface Packet {
    byte getBMagic();

    byte getBSrc();

    long getBPktId();

    int getWLen();

    short getWCrc16n1();

    Message getMessage();

    short getWCrc16n2();
}
