package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid.builders;

import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORString;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The configuration object for the CoMID object. This object contains the format and supported fields for the CoMID,
 * which will then be used to build out the CBOR representation.
 */
@Getter
@Setter
public class ComidBuilder {
    String language;
    @JsonProperty("tag-identity")
    TagIdentityMapBuilder tagIdentityMap;
    //List<ComidEntityMapBuilder> comidEntityMapList;
    //List<LinkedTagMapBuilder> linkedTagMapList;
    @JsonProperty("triples")
    TriplesMapBuilder triplesMapBuilder;

    public CBORItem build() {
        List<CBORPair> pairList = new ArrayList<>();
        if (language != null) {
            pairList.add(new CBORPair(new CBORInteger(0), new CBORString(language)));
        }
        pairList.add(new CBORPair(new CBORInteger(1), tagIdentityMap.build()));
        pairList.add(new CBORPair(new CBORInteger(4), triplesMapBuilder.build()));
        return new CBORPairList(pairList);
    }
}
