package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import hirs.utils.signature.cose.Cbor.CborItems;

/**
 *  Section 4.1.3. of the IETF CoRIM specification
 *  corim-locator-map = {
 *      &(href: 0) => uri / [ + uri ]
 *      ? &(thumbprint: 1) => digest
 *    }
 */
public class LocatorItems extends CborItems {

    public static final int HREF_INT = 0;
    public static final int THUMBPRINT_INT = 1;

    public static final String HREF_STR = "href";
    public static final String THUMBPRINT_STR = "thumbprint";

    private static final String[][] indexNames = {
            { Integer.toString(HREF_INT), HREF_STR},
            { Integer.toString(THUMBPRINT_INT),THUMBPRINT_STR }
    };
}
