package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Section 5.1.4.1.4.3 of the IETF CoRim specification
 * version-map = {
 *      &(version: 0) => text
 *      ? &(version-scheme: 1) => $version-scheme
 *    }
 */
@Getter
public enum VersionMapItems {
    VERSION(0, "version"),
    VERSION_SCHEME(1, "version-scheme");

    private final int index;
    private final String key;

    VersionMapItems(int index, String key) {
        this.index = index;
        this.key = key;
    }
    private static final Map<Integer, VersionMapItems> LOOKUP =
            stream(values())
            .collect(toMap(VersionMapItems::getIndex, x -> x));

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static VersionMapItems fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
