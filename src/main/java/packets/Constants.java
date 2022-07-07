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

    public static final int TYPE_RESPONSE_OK = 0;

    public static final int TYPE_REQUEST_TERMINATE_CONNECTION = 1;
    public static final int TYPE_REQUEST_ADD_GROUP = 1;
    public static final int TYPE_REQUEST_ADD_GOOD = 2;
    public static final int TYPE_REQUEST_FIND_GOOD_AMOUNT = 3;
    public static final int TYPE_REQUEST_LOWER_GOOD_AMOUNT = 4;
    public static final int TYPE_REQUEST_ADD_GOOD_AMOUNT = 5;
    public static final int TYPE_REQUEST_SET_PRICE_FOR_GOOD = 6;
}
