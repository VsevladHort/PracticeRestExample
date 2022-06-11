package packets.abstractions;

public interface MessageWrapper {
    byte[] wrap(byte[] message, byte bSrc, byte bPktId, byte cType, byte bUserId);
}
