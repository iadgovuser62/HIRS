package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 *  Section 5.1.4.1. of the IETF CoRim specification
 *  environment-map = non-empty<{
 *      ? &(class: 0) => class-map
 *      ? &(instance: 1) => $instance-id-type-choice
 *      ? &(group: 2) => $group-id-type-choice
 *    }>
 */
@Getter
public enum EnvironmentMapItems  {
    CLASS_MAP(0, "class-map"),
    INSTANCE_ID_TYPE_CHOICE(1, "$instance-id-type-choice"),
    GROUP_ID_TYPE_CHOICE(2, "$group-id-type-choice");

    private final int index;
    private final String key;

    EnvironmentMapItems(int index, String key) {
        this.index = index;
        this.key = key;
    }

    private static final Map<Integer, EnvironmentMapItems> LOOKUP =
            stream(values())
            .collect(toMap(EnvironmentMapItems::getIndex, x -> x));

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static EnvironmentMapItems fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
