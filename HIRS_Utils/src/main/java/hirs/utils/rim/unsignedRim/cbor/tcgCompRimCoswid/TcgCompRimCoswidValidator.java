package hirs.utils.rim.unsignedRim.cbor.tcgCompRimCoswid;


import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.CoswidConfigValidator;
import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.CoswidItems;

public class TcgCompRimCoswidValidator extends CoswidConfigValidator {
    protected TcgCompRimCoswid tcgCompRef = new TcgCompRimCoswid();

    public TcgCompRimCoswidValidator() {

    }
    @Override
    protected boolean isValidKey(String key) {
        int index = tcgCompRef.lookupIndex(key);
        boolean validity = true;
        if (index == CoswidItems.UNKNOWN_INT) {
            validity = false;
            invalidFields += key + " ";
            invalidFieldCount++;
        }
        return validity;
    }

}
