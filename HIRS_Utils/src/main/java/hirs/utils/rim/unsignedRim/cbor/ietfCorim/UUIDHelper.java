package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Helper class to aid with UUID serialization or deserialization.
 */
public final class UUIDHelper {
    private UUIDHelper() {
        // Prevent instantiation
    }

    /**
     * Converts a given UUID to a byte array.
     *
     * @param uuid The UUID to convert.
     * @return A byte array containing the UUID bytes.
     */
    public static byte[] toBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
