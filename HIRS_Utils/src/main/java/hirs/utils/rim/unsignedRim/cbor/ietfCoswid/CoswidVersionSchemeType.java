package hirs.utils.rim.unsignedRim.cbor.ietfCoswid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * An enum pertaining to values contained in the Version Scheme table of Section 4.1 of the RFC 9393 specification.
 * Used by {@link CoswidVersionScheme}.
 * <p>
 * Valid types include:
 * <pre>{@code $version-scheme /= &(multipartnumeric: 1)
 * $version-scheme /= &(multipartnumeric-suffix: 2)
 * $version-scheme /= &(alphanumeric: 3)
 * $version-scheme /= &(decimal: 4)
 * $version-scheme /= &(semver: 16384)
 * $version-scheme /= int / text}</pre>
 */
@Getter
public enum CoswidVersionSchemeType {
    MULTIPARTNUMERIC(1, "multipartnumeric"),
    MULTIPARTNUMERIC_SUFFIX(2, "multipartnumeric-suffix"),
    ALPHANUMERIC(3, "alphanumeric"),
    DECIMAL(4, "decimal"),
    SEMVER(16384, "semver");

    private final int index;
    private final String key;

    CoswidVersionSchemeType(int index, String key) {
        this.index = index;
        this.key = key;
    }

    private static final Map<Integer, CoswidVersionSchemeType> LOOKUP =
            stream(values())
            .collect(toMap(CoswidVersionSchemeType::getIndex, x -> x));

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static CoswidVersionSchemeType fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
