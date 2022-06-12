package packets.utils.abstractions;

import java.util.List;

public interface CRCCalculator {
    short calculate(byte[] bytes, int start, int end);
    short calculate(List<Byte> bytes, int start, int end);
}
