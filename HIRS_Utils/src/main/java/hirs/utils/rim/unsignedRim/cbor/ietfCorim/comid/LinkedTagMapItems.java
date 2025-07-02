package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 *  Section 5.1.3 of the IETF CORIM specification
 *
 * linked-tag-map = {
 *   &(linked-tag-id: 0) => $tag-id-type-choice
 *   &(tag-rel: 1) => $tag-rel-type-choice
 *   }
 */
@Getter
public enum LinkedTagMapItems {
    LINKED_TAG_ID(0, "linked-tag-id"),
    TAG_REL(1, "tag-rel");

    private final int index;
    private final String key;

    LinkedTagMapItems(int index, String key) {
        this.index = index;
        this.key = key;
    }

    private static final Map<Integer, LinkedTagMapItems> LOOKUP =
            stream(values())
            .collect(toMap(LinkedTagMapItems::getIndex, x -> x));

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static LinkedTagMapItems fromIndex(int index) {
        return LOOKUP.get(index);
    }
}


