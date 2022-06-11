package packets.abstractions;

public interface Message {

    int getCType();

    int getBUserId();

    byte[] getMessage();
}
