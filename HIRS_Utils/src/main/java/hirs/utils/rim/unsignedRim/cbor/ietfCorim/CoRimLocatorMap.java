package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORTaggedItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class pertaining to a <i>locator map</i>, defined in Section 4.1.3 of the IETF CoRIM specification.
 */
@Getter
@Setter
public class CoRimLocatorMap {
    List<String> href;
    CoRimDigest thumbprint;

    /**
     * Builds a CBOR representation of the locator map.
     *
     * @return The CBOR object representing the locator map.
     */
    public CBORPairList build() {
        List<CBORPair> pairList = new ArrayList<>();
        List<CBORItem> drItems = new ArrayList<>();
        if (href.size() > 1) {
            href.forEach(u -> {
                CBORTaggedItem hrefURI = new CBORTaggedItem(32, new CBORString(u)); // href (URI tag)
                drItems.add(hrefURI);
            });
            CBORItemList drList = new CBORItemList(drItems);
            pairList.add(new CBORPair(new CBORInteger(0), drList));
        }
        else {
            CBORTaggedItem hrefURI = new CBORTaggedItem(32, new CBORString(href.get(0)));
            pairList.add(new CBORPair(new CBORInteger(0), hrefURI));
        }
        if (thumbprint != null)
            pairList.add(new CBORPair(new CBORInteger(1), thumbprint.build()));
        return new CBORPairList(pairList);
    }
}
