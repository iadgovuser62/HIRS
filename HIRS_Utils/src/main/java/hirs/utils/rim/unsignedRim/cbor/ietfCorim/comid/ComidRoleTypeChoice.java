package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Defines a {@code $comid-role-type-choice} as described in Section 5.1.2 of the IETF CoRIM specification.
 */
@Getter
public enum ComidRoleTypeChoice {
    TAG_CREATOR(0),
    CREATOR(1),
    MAINTAINER(2);

    private final int index;

    private static final Map<Integer, ComidRoleTypeChoice> LOOKUP =
            stream(values())
            .collect(toMap(ComidRoleTypeChoice::getIndex, x -> x));

    ComidRoleTypeChoice(int index) {
        this.index = index;
    }

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static ComidRoleTypeChoice fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
