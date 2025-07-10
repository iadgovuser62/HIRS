package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORLong;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORTaggedItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Corresponds to a <i>validity map</i>, defined in Section 7.3 of the IETF CoRIM specification.
 */
public class CoRimValidity {
    @JsonProperty("not-before")
    Date notBefore;
    @JsonProperty("not-after")
    Date notAfter;

    /**
     * Builds a CBOR representation of the validity map.
     *
     * @return The CBOR object representing the validity map.
     */
    public CBORPairList build() {
        List<CBORPair> pairs = new ArrayList<>();
        if (notBefore != null) {
            long notBeforeInt = notBefore.getTime();
            pairs.add(new CBORPair(new CBORInteger(0), new CBORTaggedItem(1,
                    new CBORLong(notBeforeInt))));
        }
        long notAfterInt = notAfter.getTime() / 1000L; // Convert to epoch time (seconds)
        pairs.add(new CBORPair(new CBORInteger(1), new CBORTaggedItem(1,
                new CBORLong(notAfterInt))));
        return new CBORPairList(pairs);
    }
}
