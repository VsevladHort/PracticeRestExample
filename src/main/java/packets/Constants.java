package packets;

public class Constants {
    private Constants() {
    }

    public static final byte MAGIC = 0x13;
    public static final int OFFSET_MAGIC = 0;
    public static final int OFFSET_SRC = 1;
    public static final int OFFSET_PKT_ID = 2;
    public static final int OFFSET_LEN = 10;
    public static final int OFFSET_CRC = 14;
    public static final int OFFSET_MSG = 16;
    public static final int MSG_OFFSET_C_TYPE = 0;
    public static final int MSG_OFFSET_B_USER_ID = 4;
    public static final int MSG_OFFSET_MESSAGE = 8;
    public static final int MIN_LENGTH = OFFSET_MSG + MSG_OFFSET_MESSAGE + Short.BYTES;
}
