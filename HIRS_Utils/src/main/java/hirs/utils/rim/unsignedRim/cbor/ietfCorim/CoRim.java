package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * The Concise Reference Integrity Manifests (CoRIM)
 * specifies a data model for Endorsements and Reference Values
 * CoRims are defined by IETF ,   https://datatracker.ietf.org/doc/draft-ietf-rats-corim/
 */
@JsonTypeName("corim-map")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public class CoRim  {

    public static final int TAGGED_CORIM = 500; // CoRim notes as Reserved for backward compatibility
    public static final int TAGGED_CORIM_MAP = 501;
    public static final int TAGGED_CORIM_RESERVE1 = 502;
    public static final int TAGGED_CORIM_RESERVE2 = 503;
    public static final int TAGGED_CORIM_RESERVE3 = 504;
    public static final int TAGGED_CONCISE_SWID_TAG = 505;
    public static final int TAGGED_CONCISE_MID_TAG = 506;
    public static final int TAGGED_CORIM_RESERVE4 = 507;
    public static final int TAGGED_CONCISE_TL_TAG = 508;
    public static final int TAGGED_UEID_TYPE = 550;
    public static final int TAGGED_SVN = 552;
    public static final int TAGGED_MIN_SVN = 553;
    public static final int TAGGED_PKIX_BASE64_KEY_TYPE = 554;
    public static final int TAGGED_PKIX_BASE64_CERT_TYPE = 555;
    public static final int TAGGED_THUMBPRINT_TYPE = 557;
    public static final int TAGGED_COSE_KEY_TYPE = 558;
    public static final int TAGGED_CERT_THUMBPRINT_TYPE = 559;
    public static final int TAGGED_BYTES = 560;
    public static final int TAGGED_TAGGED_CERT_THUMBPRINT_TYPE = 561;
    public static final int TAGGED_PKIX_ASN1DER_CERT_TYPE = 562;
    public static final int TAGGED_MASK_RAW_VALUE = 563;

    public static final String TAGGED_CONCISE_SWID_TAG_STR = "concise-swid-tag";
    public static final String TAGGED_CONCISE_MID_TAG_STR = "concise-mid-tag";
    public static final String TAGGED_CONCISE_TL_TAG_STR = "concise-tl-tag";

    // Corim defines a single extra option for the COSE protected header
    public static final int CORIM_META_MAP = 8;
    public static final int CORIM_EARMARKED_LOWER_BOUND = 500;
    public static final int CORIM_EARMARKED_UPPER_BOUND = 599;
    // CoRIM defined attributes found in the IETF CoRIM Specification
    @Setter
    @Getter
    protected String id = "";
    @Setter
    @Getter
    protected int corimTag = 0;
    @Setter
    @Getter
    /** Hold a set of "dependant rims" with a URI and URI digest for each entry */
    protected HashMap<String, byte[]> dependentRims = new HashMap<>();
    @Setter
    @Getter
    protected String profile = "";
    @Setter
    @Getter
    protected String entities = "";
    @Setter
    @Getter
    long notBefore = 0;
    @Setter
    @Getter
    String notBeforeStr = "";
    @Setter
    @Getter
    long notAfter = 0;
    @Setter
    @Getter
    String notAfterStr = "";
    @Setter
    @Getter
    String entityName = "";
    @Setter
    @Getter
    String entityRegId = "";
    @Setter
    @Getter
    String entityRole = "";

    /**
     *  Default CoRim Constructor
     */
    public CoRim() {

    }

    /**
     * CoRim constructor that takes in CoRIm in the form of a Byte Array
     * @param data: holds the CoRim data to be parsed
     */
    public CoRim(byte[] data) {

    }

    /**
     * Determines if a given tag refers to CoRim. CoRIM Specifies 500 - 599 as "EARMARKED".
     * @param tag
     * @return true if the tag is defined by CoRim
     */
    public static boolean isCoRimTag(int tag) {
        if ((tag >=  CORIM_EARMARKED_LOWER_BOUND) & ( tag <= CORIM_EARMARKED_UPPER_BOUND)) return true;
        return false;
    }

    /**
     * Determines if a given tag refers to CoMid. CoRIM Specifies 506 as "tagged-concise-mid-tag".
     * @param tag
     * @return true if the tag is defined by CoRim
     */
    public static boolean isCoMidTag(int tag) {
        if (tag == TAGGED_CONCISE_MID_TAG )  return true;
        return false;
    }

    /**
     * Determines if a given tag refers to CoMid. CoRIM Specifies 505 as "tagged-concise-swid-tag".
     * @param tag
     * @return true if the tag is defined by CoRim
     */
    public static boolean isCoSwidTag(int tag) {
        if (tag == TAGGED_CONCISE_SWID_TAG )  return true;
        return false;
    }

    /**
     * Determines if a given tag refers to CoMid. CoRIM Specifies 505 as "tagged-concise-tl-tag".
     * @param tag
     * @return true if the tag is defined by CoRim
     */
    public static boolean isTlTag(int tag) {
        if (tag == TAGGED_CONCISE_TL_TAG )  return true;
        return false;
    }

    public static String getTagLabel(int coRimTag){
        String label = "unknown corim tag";
        if ((coRimTag>=TAGGED_CORIM)&&(coRimTag<=TAGGED_MASK_RAW_VALUE)) label = "tag reserved for CoRim (" + coRimTag + ")";
        switch (coRimTag) {
            case TAGGED_CONCISE_SWID_TAG: label = TAGGED_CONCISE_SWID_TAG_STR; break;
            case TAGGED_CONCISE_MID_TAG: label =  TAGGED_CONCISE_MID_TAG_STR; break;
            case TAGGED_CONCISE_TL_TAG: label = TAGGED_CONCISE_TL_TAG_STR; break;
        }
        return label;
    }


}
