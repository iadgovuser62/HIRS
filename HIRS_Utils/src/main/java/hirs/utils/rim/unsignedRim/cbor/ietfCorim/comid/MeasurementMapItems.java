package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 *  Section 5.1.4.1.4 of the IETF CORIM specification
 *
 *  measurement-map = {
 *      ? &(mkey: 0) => $measured-element-type-choice
 *      &(mval: 1) => measurement-values-map
 *      ? &(authorized-by: 2) => [ + $crypto-key-type-choice ]
 *    }
 *
 */
@Getter
public enum MeasurementMapItems {
    MKEY(0, "mkey"),
    MVAL(1, "mval"),
    AUTHORIZED_BY(2, "authorized-by");

    private final int index;
    private final String key;

    MeasurementMapItems(int index, String key) {
        this.index = index;
        this.key = key;
    }

    private static final Map<Integer, MeasurementMapItems> LOOKUP =
            stream(values())
            .collect(toMap(MeasurementMapItems::getIndex, x -> x));

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static MeasurementMapItems fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
