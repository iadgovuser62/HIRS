package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid.builders;

import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORTaggedItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The configuration object for the class-map, described in section 5.1.4.1.1.
 */
@Getter
@Setter
public class ComidClassBuilder {
    @JsonProperty("class-id")
    byte[] classId;
    String vendor;
    String model;
    Integer layer;
    Integer index;

    public CBORPairList build() {
        List<CBORPair> pairList = new ArrayList<>();
        if (classId != null) {
            CBORTaggedItem classIdTagged = new CBORTaggedItem(560, new CBORByteArray(classId));
            pairList.add(new CBORPair(new CBORInteger(0), classIdTagged));
        }
        if (vendor != null)
            pairList.add(new CBORPair(new CBORInteger(1), new CBORString(vendor)));
        if (model != null)
            pairList.add(new CBORPair(new CBORInteger(2), new CBORString(model)));
        if (layer != null)
            pairList.add(new CBORPair(new CBORInteger(3), new CBORInteger(layer)));
        if (index != null)
            pairList.add(new CBORPair(new CBORInteger(4), new CBORInteger(index)));
        return new CBORPairList(pairList);
    }
}
