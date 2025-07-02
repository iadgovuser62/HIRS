package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import hirs.utils.signature.cose.Cbor.CborItems;

/**
 * Section 4.2.2. of the IETF CoRIM specification
 *  corim-meta-map = {
 *      &(signer: 0) => corim-signer-map
 *      ? &(signature-validity: 1) => validity-map
 *    }
 */
public class MetaItems extends CborItems {
    public static final int SIGNER_INT = 0;
    public static final int SIGNATURE_VALIDITY_INT = 1;

    public static final String SIGNER_STR = "href";
    public static final String SIGNATURE_VALIDITY_STR = "thumbprint";

    private static final String[][] indexNames = {
            { Integer.toString(SIGNER_INT), SIGNER_STR},
            { Integer.toString(SIGNATURE_VALIDITY_INT),SIGNATURE_VALIDITY_STR }
    };
}
