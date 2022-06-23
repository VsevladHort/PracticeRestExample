package packets.utils.implementations;

import packets.utils.abstractions.Cipherer;

public class CiphererSimpleImpl implements Cipherer {
    @Override
    public byte[] cipher(byte[] info) {
        byte[] result = new byte[info.length];
        for (int i = 0; i < info.length; i++) {
            result[i] = info[i]++;
        }
        return result;
    }

    @Override
    public byte[] decipher(byte[] info) {
        byte[] result = new byte[info.length];
        for (int i = 0; i < info.length; i++) {
            result[i] = info[i]--;
        }
        return result;
    }
}
