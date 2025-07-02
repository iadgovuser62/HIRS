package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Section 5.1.4. of the IETF CoRim specification
 * triples-map = non-empty<{
 *      ? &(reference-triples: 0) =>
 *        [ + reference-triple-record ]
 *      ? &(endorsed-triples: 1) =>
 *        [ + endorsed-triple-record ]
 *      ? &(identity-triples: 2) =>
 *        [ + identity-triple-record ]
 *      ? &(attest-key-triples: 3) =>
 *        [ + attest-key-triple-record ]
 *      ? &(dependency-triples: 4) =>
 *        [ + domain-dependency-triple-record ]
 *      ? &(membership-triples: 5) =>
 *        [ + domain-membership-triple-record ]
 *      ? &(coswid-triples: 6) =>
 *        [ + coswid-triple-record ]
 *      ? &(conditional-endorsement-series-triples: 8) =>
 *        [ + conditional-endorsement-series-triple-record ]
 *      ? &(conditional-endorsement-triples: 10) =>
 *        [ + conditional-endorsement-triple-record ]
 *      * $$triples-map-extension
 *    }>
 */
@Getter
public enum TriplesItems {
    REFERENCE_TRIPLES(0, "reference-triples"),
    ENDORSED_TRIPLES(1, "endorsed-triples"),
    IDENTITY_TRIPLES(2, "identity-triples"),
    ATTEST_KEY_TRIPLES(3, "attest-key-triples"),
    DEPENDENCY_TRIPLES(4, "dependency-triples"),
    MEMBERSHIP_TRIPLES(5, "membership-triples"),
    COSWID_TRIPLES(6, "coswid-triples"),
    CONDITIONAL_ENDORSEMENT_SERIES_TRIPLES(8, "conditional-endorsement-series-triples"),
    CONDITIONAL_ENDORSEMENT_TRIPLES(10, "conditional-series-triples");

    private final int index;
    private final String key;

    TriplesItems(int index, String key) {
        this.index = index;
        this.key = key;
    }

    private static final Map<Integer, TriplesItems> LOOKUP =
            stream(values())
            .collect(toMap(TriplesItems::getIndex, x -> x));

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static TriplesItems fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
