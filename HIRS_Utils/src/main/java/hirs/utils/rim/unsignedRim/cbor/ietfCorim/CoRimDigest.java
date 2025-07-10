package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid.ComidDigest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 *  Represents a {@code digest} containing hash information relevant to CoMID measurements. See Section 7.7
 *  of the IETF CoRIM specification.
 *  <p>
 *  Note that this is conceptually the same as {@link ComidDigest}, though this class is used exclusively for CoRIM
 *  CBOR building.
 */
@Getter
@Setter
public class CoRimDigest {
    int alg;
    byte[] val;

    /**
     * Builds a CBOR representation of the digest.
     *
     * @return The CBOR object representing the digest.
     */
    public CBORItemList build() {
        List<CBORItem> itemList = new ArrayList<>();
        itemList.add(new CBORInteger(alg)); // alg
        itemList.add(new CBORByteArray(val)); // val
        return new CBORItemList(itemList);
    }
}