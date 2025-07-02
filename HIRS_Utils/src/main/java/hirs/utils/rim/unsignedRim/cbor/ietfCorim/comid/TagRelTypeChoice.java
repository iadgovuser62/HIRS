package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

@Getter
public enum TagRelTypeChoice {
    SUPPLEMENTS(0),
    REPLACES(1);

    private final int index;

    TagRelTypeChoice(int index) {
        this.index = index;
    }

    private static final Map<Integer, TagRelTypeChoice> LOOKUP =
            stream(values())
            .collect(toMap(TagRelTypeChoice::getIndex, x -> x));

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static TagRelTypeChoice fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
