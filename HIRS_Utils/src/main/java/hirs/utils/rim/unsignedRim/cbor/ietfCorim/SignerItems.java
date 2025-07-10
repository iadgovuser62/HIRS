package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import hirs.utils.signature.cose.Cbor.CborItems;

/**
 *  Section 4.2.2.1. if the IETF Corim Specification
 *
 *  corim-signer-map = {
 *      &(signer-name: 0) => $entity-name-type-choice
 *      ? &(signer-uri: 1) => uri
 *      * $$corim-signer-map-extension
 *    }
 */
public class SignerItems extends CborItems {
    public static final int SIGNER_NAME_INT = 0;
    public static final int SIGNER_URI_INT = 1;

    public static final String SIGNER_NAME_STR = "signer-name";
    public static final String SIGNER_URI_STR = "signer-uri";

    private static final String[][] indexNames = {
            { Integer.toString(SIGNER_NAME_INT), SIGNER_NAME_STR},
            { Integer.toString(SIGNER_URI_INT),SIGNER_URI_STR }
    };

}
