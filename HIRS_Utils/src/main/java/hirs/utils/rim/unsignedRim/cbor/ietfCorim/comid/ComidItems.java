package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 *  Converts CBor indexes to strings per the CoRIM spec
 *
 *  concise-mid-tag = {
 *      ? &(language: 0) => text
 *      &(tag-identity: 1) => tag-identity-map
 *      ? &(entities: 2) => [ + comid-entity-map ]
 *      ? &(linked-tags: 3) => [ + linked-tag-map ]
 *      &(triples: 4) => triples-map
 *      * $$concise-mid-tag-extension
 *    }
 */
@Getter
public enum ComidItems {
    LANGUAGE(0, "language"),
    TAG_ID(1, "tag-identity"),
    COMID_ENTITY_MAP(2, "entities"),
    LINKED_TAG_MAP(3, "linked-tags"),
    TRIPLES_MAP(4, "triples");

    private final int index;
    private final String key;

    ComidItems(int index, String key) {
        this.index = index;
        this.key = key;
    }

    private static final Map<Integer, ComidItems> LOOKUP =
            stream(values())
            .collect(toMap(ComidItems::getIndex, x -> x));

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static ComidItems fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
