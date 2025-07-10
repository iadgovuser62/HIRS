package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Custom deserializer helper class for Jackson to parse hex strings into byte arrays.
 */
public class HexByteArrayDeserializer extends StdDeserializer<byte[]> {

    /** Default constructor. */
    public HexByteArrayDeserializer() {
        super(byte[].class);
    }

    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String hexString = p.getText().trim();
        return hexStringToByteArray(hexString);
    }

    /**
     * Converts a hex string to a byte array.
     * Assumes the input string length is even and contains only valid hex characters.
     *
     * @param s the hex string
     * @return the byte array
     */
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have even length");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int high = Character.digit(s.charAt(i), 16);
            int low = Character.digit(s.charAt(i + 1), 16);
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("Invalid hex character in string");
            }
            data[i / 2] = (byte) ((high << 4) + low);
        }
        return data;
    }
}
