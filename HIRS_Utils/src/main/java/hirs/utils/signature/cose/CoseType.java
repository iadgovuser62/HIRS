package hirs.utils.signature.cose;

import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.CoswidItems;

public class CoseType {
    public static final int coseSign = 98;
    public static final int coseSign1 = 18;
    public static final int coseEncrypt = 96;
    public static final int coseEncrypt0 = 16;
    public static final int coseMac = 97;
    public static final int coseMac0 = 17;

    private static final String[][] indexNames = {
            {"98", "cose-sign"},
            {"18", "cose-sign1"},
            {"96", "cose-encrypt"},
            {"16", "cose-encrypt0"},
            {"97", "cose-mac"},
            {"17", "cose-mac0"}
    };

    /**
     * Searches Rfc 9393 Items Names for match to a specified item name and returns the index
     * @param coseType  Iem Name specified in rfc 8152
     * @return int tag of the cose type
     */
    public int getType(String coseType) {
        int algId = 0;
        for (int i=0; i< indexNames.length; i++) {
            if (coseType.compareToIgnoreCase(indexNames[i][1])==0)
                return i;
        }
        return CoswidItems.UNKNOWN_INT ;
    }
    /**
     * Searches for an Rfc 8152 specified index and returns the item name associated with the index
     * @param index int rfc 8152 specified index value
     * @return String item name associated with the index
     */
    public static String getItemName(int index) {
        for (int i=0; i< indexNames.length; i++) {
            if (index == Integer.parseInt(indexNames[i][0]))
                return indexNames[i][1];
        }
        return CoswidItems.UNKNOWN_STR;
    }

    public static boolean isCoseTag(int tag){
        for (int i=0; i< indexNames.length; i++) {
            if (tag == Integer.parseInt(indexNames[i][0]))
                return true;
        }
        return false;
    }
}
