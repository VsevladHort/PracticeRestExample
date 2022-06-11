package packets.exceptions;

import java.io.IOException;

public class DiscardException extends IOException {
    public DiscardException(String msg) {
        super(msg);
    }
}
