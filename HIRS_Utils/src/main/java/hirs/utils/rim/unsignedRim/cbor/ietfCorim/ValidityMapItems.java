package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import hirs.utils.signature.cose.Cbor.CborItems;

/**
 * Section 7.3 of the IETF CoRim specification
 *  validity-map = {
 *      ? &(not-before: 0) => time
 *      &(not-after: 1) => time
 *    }
 */
public class ValidityMapItems extends CborItems {
    public static final int NOT_BEFORE_INT = 0;
    public static final int NOT_AFTER_INT = 1;

    public static final String NOT_BEFORE_STR = "not-before";
    public static final String NOT_AFTER_STR = "not-after";

    private static final String[][] indexNames = {
            { Integer.toString(NOT_BEFORE_INT), NOT_BEFORE_STR},
            { Integer.toString(NOT_AFTER_INT),NOT_AFTER_STR }
    };
}
