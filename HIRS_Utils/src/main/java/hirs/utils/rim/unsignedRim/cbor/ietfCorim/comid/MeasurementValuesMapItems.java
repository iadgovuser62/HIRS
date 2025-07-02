package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import lombok.Getter;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Section 5.1.4.1.4.2 of the IETF CoRim specification
 *
 * measurement-values-map = non-empty<{
 *      ? &(version: 0) => version-map
 *      ? &(svn: 1) => svn-type-choice
 *      ? &(digests: 2) => digests-type
 *      ? &(flags: 3) => flags-map
 *      ? (
 *          &(raw-value: 4) => $raw-value-type-choice,
 *          ? &(raw-value-mask: 5) => raw-value-mask-type
 *        )
 *      ? &(mac-addr: 6) => mac-addr-type-choice
 *      ? &(ip-addr: 7) =>  ip-addr-type-choice
 *      ? &(serial-number: 8) => text
 *      ? &(ueid: 9) => ueid-type
 *      ? &(uuid: 10) => uuid-type
 *      ? &(name: 11) => text
 *      ? &(cryptokeys: 13) => [ + $crypto-key-type-choice ]
 *      ? &(integrity-registers: 14) => integrity-registers
 *      * $$measurement-values-map-extension
 *    }>
 */
@Getter
public enum MeasurementValuesMapItems {
    VERSION_MAP(0, "version-map"),
    SVN(1, "svn"),
    DIGESTS(2, "digests"),
    FLAGS(3, "flags"),
    RAW_VALUE(4, "raw-value"),
    RAW_VALUE_MASK(5, "raw-value-mask"),
    MAC_ADDR(6, "mac-addr"),
    IP_ADDR(7, "ip-addr"),
    SERIAL_NUMBER(8, "serial-number"),
    UEID(9, "ueid"),
    UUID(10, "uuid"),
    NAME(11, "name"),
    CRYPTOKEYS(13, "cryptokeys"),
    INTEGRITY_REGISTERS(14, "integrity-registers");

    private final int index;
    private final String key;

    MeasurementValuesMapItems(int index, String key) {
        this.index = index;
        this.key = key;
    }

    private static final Map<Integer, MeasurementValuesMapItems> LOOKUP =
            stream(values())
            .collect(toMap(MeasurementValuesMapItems::getIndex, x -> x));

    /**
     * Method to return an enum value from an integer index.
     *
     * @param index The index to reference.
     * @return The enum value, if present, or {@code null} otherwise.
     */
    public static MeasurementValuesMapItems fromIndex(int index) {
        return LOOKUP.get(index);
    }
}
