package hirs.utils.signature.cose.Cbor;

/**
 * Support class for handling CBOR Items with a map
 * Classes hat extend this class must populate the indexNames [String index,String itemName] array
 * where i is the numerical index converted to a string and  iem name is taken from the specification
 */
public class CborItems {

    private static final String[][] indexNames = new String[0][0];
    public static final String UNKNOWN_STR = "Unknown";
    public static final int UNKNOWN_INT=99;

    /** Searches  Items Names for match to a specified item name and returns the index
     * @param itemName  Iem Name specified in the spec
     * @return int index of the item
     */
    public static int getIndex(String itemName) {
        for (int i=0; i< indexNames.length; i++) {
            if (itemName.compareToIgnoreCase(indexNames[i][1])==0)
                return i;
        }
        return UNKNOWN_INT ;
    }

    /**
     * Searches for an Rfc 9393 specified index and returns the item name associated with the index
     * @param index int rfc 939 sepcified index value
     * @return String item name associated with the index
     */
    public static String getItemName(int index) {
        int algId = 0;
        for (int i=0; i< indexNames.length; i++) {
            if (index == Integer.parseInt(indexNames[i][0]))
                return indexNames[i][1];
        }
        return UNKNOWN_STR;
    }
}


