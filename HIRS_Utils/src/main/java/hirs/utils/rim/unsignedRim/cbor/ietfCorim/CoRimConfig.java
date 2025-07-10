package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORTaggedItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid.builders.ComidBuilder;
import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.Coswid;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The configuration object for the CoRIM object. This object contains the format and supported fields for the CoRIM,
 * which will then be used to build out the CoRIM.
 */
@Getter
@Setter
public class CoRimConfig {
    String id;
    @JsonProperty("comid-tags")
    List<ComidBuilder> comidTags;
    @JsonProperty("coswid-tags")
    List<Coswid> coswidTags;
    @JsonProperty("dependent-rims")
    List<CoRimLocatorMap> dependentRims;
    String profile;
    @JsonProperty("validity-map")
    CoRimValidity validityMap;
    List<CorimEntityMap> entities;

    /**
     * Builds a CBOR representation of the CoRIM.
     *
     * @return The CBOR object representing the CoRIM.
     */
    public CBORItem build() {
        // corim-map
        List<CBORPair> corimMapPairs = new ArrayList<>();
        corimMapPairs.add(new CBORPair(new CBORInteger(0), new CBORString(this.id))); // id
        // tags (consolidate both CoSWID and CoMID tags)
        List<CBORItem> tagList = new ArrayList<>();
        // CoMID tags
        if (comidTags != null) {
            comidTags.forEach(comidTag -> {
                CBORTaggedItem taggedComid =
                        new CBORTaggedItem(506, comidTag.build()); // Tagged CoMID
                tagList.add(new CBORByteArray(taggedComid.encode()));
            });
        }
        corimMapPairs.add(new CBORPair(new CBORInteger(1), new CBORItemList(tagList))); // tags
        if (dependentRims != null) {
            List<CBORItem> drItems = new ArrayList<>();
            dependentRims.forEach(rim ->
                    drItems.add(rim.build()));
            CBORItemList drList = new CBORItemList(drItems);
            corimMapPairs.add(new CBORPair(new CBORInteger(2), drList));
        }
        if (profile != null)
            corimMapPairs.add(new CBORPair(new CBORInteger(3), new CBORString(this.profile))); // profile
        if (validityMap != null)
            corimMapPairs.add(new CBORPair(new CBORInteger(4), this.validityMap.build())); // validity-map
        if (entities != null) {
            List<CBORItem> eItems = new ArrayList<>();
            entities.forEach(entity ->
                    eItems.add(entity.build()));
            CBORItemList eList = new CBORItemList(eItems);
            corimMapPairs.add(new CBORPair(new CBORInteger(5), eList)); // entities
        }
        return new CBORPairList(corimMapPairs);
    }
}
