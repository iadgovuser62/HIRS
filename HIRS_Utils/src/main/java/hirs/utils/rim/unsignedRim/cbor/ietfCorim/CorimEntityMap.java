package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORTaggedItem;
import com.authlete.cbor.CBORizer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 *  Corresponds to a {@code corim-entity-map}. See Section 4.1.5 of the IETF CoRIM specification.
 */
@Getter
@Setter
public class CorimEntityMap {
    @JsonProperty("entity-name")
    String entityName;
    @JsonProperty("reg-id")
    String regId;
    List<Integer> role;

    /**
     * Builds a corim-entity-map CBOR representation.
     *
     * @return The CBOR representation of a corim-entity-map.
     */
    public CBORPairList build() {
        List<CBORPair> pairs = new ArrayList<>();
        pairs.add(new CBORPair(new CBORInteger(0), new CBORString(entityName))); // entity-name
        CBORTaggedItem regIdURI = new CBORTaggedItem(32, new CBORString(regId)); // reg-id (URI tag)
        pairs.add(new CBORPair(new CBORInteger(1), regIdURI)); // reg-id
        CBORItemList roleList = (CBORItemList) new CBORizer().cborize(role);
        pairs.add(new CBORPair(new CBORInteger(2), roleList)); // reg-id
        return new CBORPairList(pairs);
    }
}
