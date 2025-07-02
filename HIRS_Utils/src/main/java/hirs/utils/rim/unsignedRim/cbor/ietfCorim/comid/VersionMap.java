package hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid;

import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORPairList;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.CoswidVersionScheme;
import lombok.Getter;
import lombok.Setter;

/**
 * A class corresponding to a {@code version-map} defined in Section 5.1.4.1.4.3 of the IETF CoRIM specification.
 * Contains details about a measured environment's version.
 */
@Setter
@Getter
@JsonTypeName("version-map")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public class VersionMap {
    /** Corresponds to the {@code version} field, representing the version string.*/
    String version;
    /** Corresponds to the {@code version-scheme} field, optionally indicating the format used for the {@link #version}
     * field.*/
    CoswidVersionScheme versionScheme;

    public VersionMap(CBORItem versionMap) {
        var list = ((CBORPairList)versionMap).getPairs();

        for (var currItem : list) {
            var currKey = (int)currItem.getKey().parse();
            var currVal = VersionMapItems.fromIndex(currKey);
            if (currVal != null) {
                switch (currVal) {
                    case VERSION -> version = (String)currItem.getValue().parse(); // Version
                    // Version scheme
                    case VERSION_SCHEME -> versionScheme = new CoswidVersionScheme(currItem.getValue().parse());
                }
            }
        }
    }
}
