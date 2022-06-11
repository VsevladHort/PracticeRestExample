package packets.utils.abstractions;

public interface CRCCalculator {
    short calculate(byte[] bytes, int start, int end);

    CRCCalculator provide();
}
