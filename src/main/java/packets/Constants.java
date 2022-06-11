package packets;

class Constants {
    private Constants() {
    }

    static final int MAGIC = 0x13;
    static final int OFFSET_MAGIC = 0;
    static final int OFFSET_SRC = 1;
    static final int OFFSET_PKT_ID = 2;
    static final int OFFSET_LEN = 10;
    static final int OFFSET_CRC = 14;
    static final int OFFSET_MSG = 16;
    static final int MSG_OFFSET_C_TYPE = 0;
    static final int MSG_OFFSET_B_USER_ID = 4;
    static final int MSG_OFFSET_MESSAGE = 8;
}
