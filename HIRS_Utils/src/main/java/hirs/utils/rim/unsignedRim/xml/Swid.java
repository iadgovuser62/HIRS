package hirs.utils.rim.unsignedRim.xml;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that contains definitions found in teh SWID specification  ISO/IEC 19770-2:2015
 * Used by Coswid (Rfc 9393) which is based upon SWID specification
 *
 */
public class Swid {
    // Order of variables follows the order listed in the table in section 8.5.1 of the SWID spec

    // Attributes are listed after the elements
    // SWID Elements are listed first
    public static final String SWID_SOFTWARE_IDENTITY_STR = "SoftwareIdentity";
    // Software Identity Element Attributes
    @Setter
    @Getter
    /** Flag set to true if tag is a patch tag which indicates this tag applies to pre-installation data */
    protected boolean corpus = false;
    public static final String SWID_CORPUS_STR = "corpus";
    @Setter
    @Getter
    /** Flag set to true if tag is a patch tag that implies modification to the software */
    protected boolean patch = false;
    public static final String SWID_PATCH_STR = "patch";
    @Setter
    @Getter
    /** String that desribes the "Platform" this software applies to */
    protected String swidMedia = null;
    public static final String SWID_MEDIA_STR = "media";
    @Setter
    @Getter
    /** String that provides the software component name */
    protected String softwareName = null;
    public static final String SWID_NAME_STR = "name";
    @Setter
    @Getter
    /** Flag set to true if tag is a Supplemental tag which is generally provided by different entities */
    protected boolean supplemental = false;
    public static final String SWID_SUPPLEMENTAL_STR = "supplemental";
    @Setter
    @Getter
    /** Tag Identifier - usually a UUID */
    protected String swidTagId = null;
    public static final String SWID_TAG_ID_STR = "tagId";
    @Setter
    @Getter
    /** Version of the tag */
    protected String tagVersion = null;
    public static final String SWID_TAG_VERSION_STR = "tagVersion";
    @Setter
    @Getter
    /** Swid spec version */
    protected String softwareVersion = null;
    public static final String SWID_VERSION_STR = "version";
    @Setter
    @Getter
    /** Swid spec version */
    protected String swidVersionScheme = null;
    public static final String SWID_VERSION_SCHEME_STR = "versionScheme";

    /** Entity Element */
    public static final String SWID_ENTITY_STR = "Entity";
    @Setter
    @Getter
    /** Name of the Entity that created this tag */
    protected String swidEntityName = null;
    public static final String SWID_ENTITY_NAME_STR = "name";
    @Setter
    @Getter
    /** IANA ID of the Entity that created this tag */
    protected String regId = null;
    public static final String SWID_ENTITY_REGID_STR = "regid";
    @Setter
    @Getter
    /** Role of the entity had in creating this tag */
    protected String role = null;
    public static final String SWID_ENTITY_ROL_STR = "role";
    @Setter
    @Getter
    /** hash of the cert used to sign this tag */
    protected String thumbprint = null;
    public static final String SWID_ENTITY_THUMBPRINT_STR = "thumbprint";

    // Evidence Element
    /** Evidence is a scan of the system where software which does no have a swid tag is discovered */
    public static final String SWID_EVIDENCE_STR = "Evidence";
    @Setter
    @Getter
    /** Date and time the evidence was collected */
    protected String swidEvidenceDate = null;
    public static final String SWID_EVIDENCE_DATE_STR = "date";
    @Setter
    @Getter
    /** Identifier of the device the evidence was collected from */
    protected String swidEvidenceDeviceId = null;
    public static final String SWID_EVIDENCE_DEVICE_ID_STR = "deviceId";

    // Link Element
    /** Link is a reference to any other item */
    public static final String SWID_LINK_STR = "Link";
    @Setter
    @Getter
    /** Canonical name for the item being referenced */
    protected String swidLinkArtifact = null;
    public static final String SWID_LINK_ARTIFACT_STR = "artifact";
    @Setter
    @Getter
    /** Link to the item being referenced */
    protected String href = null;
    public static final String SWID_LINK_HREF_STR = "href";
    @Setter
    @Getter
    /** String that describes the "Platform" this software applies to */
    protected String swidLinkMedia = null;
    public static final String SWID_LINK_MEDIA_STR = "media";
    @Setter
    @Getter
    /** String that describes the "Strength of ownership" of the target piece of software */
    protected String swidLinkOwnership = null;
    public static final String SWID_LINK_OWNERSHIP_STR = "ownership";
    @Setter
    @Getter
    /** String that describes the "relationship" betwen the tag abd the target software */
    protected String rel = null;
    public static final String SWID_LINK_REL_STR = "rel";
    @Setter
    @Getter
    /** String type of media the device the link refers to */
    protected String swidLinkType = null;
    public static final String SWID_LINK_TYPE_STR = "type";
    @Setter
    @Getter
    /** Determines if the target is a hard requirement  */
    protected String swidLinkUse = null;
    public static final String SWID_LINK_USE_STR = "use";

    // Meta Element
    public static final String SWID_META_STR = "Meta";

    // Payload Element
    public static final String SWID_PAYLOAD_STR = "Payload";
    @Setter
    @Getter
    /** Directory where the payload is located */
    protected String swidPayloadDirectory = null;
    public static final String SWID_PAYLOAD_DIR_STR = "directory";
    @Setter
    @Getter
    /** Flag to denote the importance of the directory*/
    public boolean swidPayloadDirectoryKey = false;
    public static final String SWID_PAYLOAD_DIR_KEY_STR = "payloadDirKey";
    /** location of the directory */
    @Setter
    @Getter
    protected String swidPayloadDirectoryLocation = null;
    public static final String SWID_PAYLOAD_DIR_LOC_STR = "location";
    /** name of the directory */
    @Setter
    @Getter
    protected String swidPayloadDirectoryName = null;
    public static final String SWID_PAYLOAD_DIR_NAME_STR = "directoryName";
    @Setter
    @Getter
    /** Root directory the directory os relative to*/
    protected String swidPayloadDirectoryRoot = null;
    public static final String SWID_PAYLOAD_DIR_ROOT_STR = "rootDirectory";
    /** file the payload refers to */
    @Setter
    @Getter
    protected String swidPayloadFile = null;
    public static final String SWID_PAYLOAD_FILE_STR = "file";
    /** name of the file the payload refers to */
    @Setter
    @Getter
    protected String swidPayloadFileName = null;
    public static final String SWID_PAYLOAD_FILE_NAME = "fileName";
    @Setter
    @Getter
    /** size the payload refers to */
    protected int swidPayloadFileSize = 0;
    public static final String SWID_PAYLOAD_FILE_SIZE = "size";
    @Setter
    @Getter
    /** version of the file the payload refers to */
    protected String swidPayloadFileVersion = null;
    public static final String SWID_PAYLOAD_FILE_VER_STR = "file-version";

    // Process
    /** OS Process information */
    public static final String SWID_PROCESS_STR = "process";
    @Setter
    @Getter
    /** Process id string (name) */
    protected String swidProcessName = null;
    public static final String SWID_PROCESS_NAME_STR = "process";
    @Setter
    @Getter
    /** Process id int (name) */
    protected int swidProcessPid = 0;
    public static final String SWID_PROCESS_PID_STR = "processId";

    // Resource
    /** OS Process information */
    public static final String SWID_RESOURCE_STR = "resource";
    @Setter
    @Getter
    /** Genric description of the resource (name) */
    protected String swidResourceType = null;
    public static final String SWID_RESOURCE_TYPE_STR = "resourceType";

    // NIST IR 8060 defined Meta fields used by Coswid and TCG PC Client RIM
    @Setter
    @Getter
    /** Version defined by NIST IR 8060 */
    protected String colloquialVersion = null;
    public static final String SWID_META_COL_VER_STR = "colloquialVersion";
    @Setter
    @Getter
    /** Product defined by NIST IR 8060 */
    protected String product = null;
    public static final String SWID_META_COL_PRODUCT_STR = "product";
    @Setter
    @Getter
    /** Revision defined by NIST IR 8060 */
    protected String revision = null;
    public static final String SWID_META_REV_STR = "revision";
    @Setter
    @Getter
    /** Edition defined by NIST IR 8060 */
    protected String edition = null;
    public static final String SWID_META_EDITION_STR = "edition";
}
