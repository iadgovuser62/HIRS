package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import hirs.utils.signature.cose.Cbor.CborItems;

/**
 *  Section 4.2.1. of the IETF CoRIm specification
 *  protected-corim-header-map = {
 *      &(alg: 1) => int
 *      &(content-type: 3) => "application/rim+cbor"
 *      &(kid: 4) => bstr
 *      &(corim-meta: 8) => bstr .cbor corim-meta-map
 *      * cose-label => cose-value
 *    }
 */
public class ProtectedCorimHeaderItems extends CborItems {
    public static final int ALG_INT = 1;
    public static final int CONTENT_TYPE_INT = 3;
    public static final int KID_INT = 4;
    public static final int CORIM_META_INT = 8;

    public static final String ALG_STR = "alg";
    public static final String CONTENT_TYPE_STR = "content-type";
    public static final String KID_STR = "kid";
    public static final String CORIM_META_STR = "corim-meta:";

    private static final String[][] indexNames = {
            { Integer.toString(ALG_INT), ALG_STR},
            { Integer.toString(CONTENT_TYPE_INT),CONTENT_TYPE_STR },
            { Integer.toString(KID_INT),KID_STR },
            { Integer.toString(CORIM_META_INT), CORIM_META_STR}
    };
}
