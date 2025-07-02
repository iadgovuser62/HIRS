package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Section 7.2 of the IETF CoRim specification
 *  entity-map<role-type-choice, extension-socket> = {
 *      &(entity-name: 0) => $entity-name-type-choice
 *      ? &(reg-id: 1) => uri
 *      &(role: 2) => [ + role-type-choice ]
 *      * extension-socket
 *    }
 */
@Getter
public enum EntityItems {
    ENTITY_NAME(0, "entity-name"),
    REG_ID(1, "reg-id"),
    ROLE(2, "role");

    private final int index;
    private final String key;

    EntityItems(int index, String key) {
        this.index = index;
        this.key = key;
    }

    private static final Map<Integer, EntityItems> LOOKUP =
            stream(values())
            .collect(toMap(EntityItems::getIndex, x -> x));

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static EntityItems fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
