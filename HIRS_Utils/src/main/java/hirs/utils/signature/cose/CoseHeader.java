package hirs.utils.signature.cose;

import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.CoswidItems;

/**
 * Class that manages rfc 8152 (COSE) defined Header parameters
 */
public class CoseHeader {
    public static final int coseSign = 98;
    public static final int alg = 1;
    public static final int crit = 2;
    public static final int contentType = 3;
    public static final int kid = 4;
    public static final int iv = 5;
    public static final int partialIv = 6;
    public static final int counterSignature = 7;

    private static final String[][] indexNames = {
            {"1", "alg"},           // algorithm used for security processing
            {"2", "crit"},          // indicate which labels the app must understand
            {"3", "content-type"},  // "CoAP Content-Formats" from IANA registry table
            {"4", "kid"},           // input to find the needed cryptographic key
            {"5", "iv"},            // holds the Initialization Vector (IV) value for encryption
            {"6", "partial-iv"},    // holds a part of the IV value
            {"7", "counter-signature"}  // one or more counter  third party signature values
    };
    /** Searches Rfc 8152 Parameter integer label for match to a specified Parameter Name and returns the Name
     * @param param  Name specified header parameter specified in rfc 8152
     * @return int tag of the header param
     */
    public int getHeaderLabel(String param) {
        int algId = 0;
        for (int i=0; i< indexNames.length; i++) {
            if (param.compareToIgnoreCase(indexNames[i][1])==0)
                return i;
        }
        return CoswidItems.UNKNOWN_INT ;
    }
    /**
     * Searches for an Rfc 8152 specified Header parameter integer label and returns the parameter name associated with the parameter
     * @param label int rfc 8152 specified Haeder parameter Label
     * @return String item name associated with the header parameter label
     */
    public String getItemName(int label) {
        int algId = 0;
        for (int i=0; i< indexNames.length; i++) {
            if (label == Integer.parseInt(indexNames[i][0]))
                return indexNames[i][1];
        }
        return CoswidItems.UNKNOWN_STR;
    }
}
