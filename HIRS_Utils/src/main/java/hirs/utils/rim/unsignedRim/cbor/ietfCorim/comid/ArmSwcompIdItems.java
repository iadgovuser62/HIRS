package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Section 3.1.3.1 of the IETF draft-ydb-rats-cca-endorsements specification
 *
 * arm-swcomp-id = {
 *   arm.measurement-type => text
 *   arm.version => text
 *   arm.signer-id => arm.hash-type
 * }
 */
@Getter
public enum ArmSwcompIdItems {
    MEASUREMENT_TYPE(1, "arm.measurement-type"),
    VERSION(4, "arm.version"),
    SIGNER_ID(5, "arm.signer-id");

    private final int index;
    private final String key;

    private static final Map<Integer, ArmSwcompIdItems> LOOKUP =
            stream(values())
            .collect(toMap(ArmSwcompIdItems::getIndex, x -> x));

    ArmSwcompIdItems(int index, String key) {
        this.index = index;
        this.key = key;
    }

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static ArmSwcompIdItems fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
