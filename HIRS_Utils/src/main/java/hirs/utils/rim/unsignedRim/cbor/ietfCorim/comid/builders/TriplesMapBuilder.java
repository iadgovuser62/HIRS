package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid.builders;

import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * The configuration object for the triples object, described in section 5.1.4.
 */
public class TriplesMapBuilder {
    @JsonProperty("reference-triples")
    List<ReferenceTripleRecordBuilder> referenceTripleRecordList;

    public CBORItemList build() {
        List<CBORItem> itemList = new ArrayList<>();
        if (referenceTripleRecordList != null) {
            referenceTripleRecordList.forEach(rtr -> {
                itemList.add(rtr.build());
            });
        }
        return new CBORItemList(itemList);
    }
}
