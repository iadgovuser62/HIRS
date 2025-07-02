package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Section 5.1.4.1.1 of the IETF Specification
 *
 * class-map = non-empty<{
 *      ? &(class-id: 0) => $class-id-type-choice
 *      ? &(vendor: 1) => tstr
 *      ? &(model: 2) => tstr
 *      ? &(layer: 3) => uint
 *      ? &(index: 4) => uint
 *    }>
 */
@Getter
public enum ClassItems {
    CLASS_ID(0, "class-id"),
    VENDOR(1, "vendor"),
    MODEL(2, "model"),
    LAYER(3, "layer"),
    INDEX(4, "index");

    private final int index;
    private final String key;

    private static final Map<Integer, ClassItems> LOOKUP =
            stream(values())
            .collect(toMap(ClassItems::getIndex, x -> x));

    ClassItems(int index, String key) {
        this.index = index;
        this.key = key;
    }

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static ClassItems fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
