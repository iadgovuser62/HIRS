package hirs.utils.rim.unsignedRim.cbor.ietfCoswid;

/**
 * This class provides support for table 9 of rfc 9393 (CoSWID Items Initial Registrations) The
 * static fields are intended to be referenced the CoSWID parser and builder
 */
public class CoswidItems {

  // Constant Index values defined in RFC 9393 Table 9
  // ---------------------------------------------------

  // concise-swid-tag map
  public static final int TAG_ID_INT = 0;
  public static final int SOFTWARE_NAME_INT = 1;
  public static final int ENTITY_INT = 2;
  public static final int EVIDENCE_INT = 3;
  public static final int LINK_INT = 4;
  public static final int SOFTWARE_META_INT = 5;
  public static final int PAYLOAD_INT = 6;
  public static final int HASH_INT = 7; // note: belongs to resource-collection group
  public static final int CORPUS_INT = 8;
  public static final int PATCH_INT = 9;
  public static final int MEDIA_INT = 10;
  public static final int SUPPLEMENTAL_INT = 11;
  public static final int TAG_VERSION_INT = 12;
  public static final int SOFTWARE_VERSION_INT = 13;
  public static final int VERSION_SCHEME_INT = 14;

  // global-attributes group
  public static final int LANG_INT = 15;

  // resource-collection group
  public static final int DIRECTORY_INT = 16;
  public static final int FILE_INT = 17;
  public static final int PROCESS_INT = 18;
  public static final int RESOURCE_INT = 19;
  public static final int SIZE_INT = 20;
  public static final int FILE_VERSION_INT = 21;
  public static final int KEY_INT = 22;
  public static final int LOCATION_INT = 23;
  public static final int FS_NAME_INT = 24;
  public static final int ROOT_STR_INT = 25;
  public static final int PATH_ELEMENTS_INT = 26;
  public static final int PROCESS_NAME_INT = 27;
  public static final int PID_INT = 28;
  public static final int TYPE_INT = 29;

  // other
  public static final int UNASSIGNED_INT = 30;

  // entity-entry map
  public static final int ENTITY_NAME_INT = 31;
  public static final int REG_ID_INT = 32;
  public static final int ROLE_INT = 33;
  public static final int THUMBPRINT_INT = 34;

  // evidence-entry map
  public static final int DATE_INT = 35;
  public static final int DEVICE_INT = 36;

  // link-entry map
  public static final int ARTIFACT_INT = 37;
  public static final int HREF_INT = 38;
  public static final int OWNERSHIP_INT = 39;
  public static final int REL_INT = 40;
  public static final int MEDIA_TYPE_INT = 41;
  public static final int USE_INT = 42;

  // software-meta-entry map
  public static final int ACTIVATION_STATUS_INT = 43;
  public static final int CHANNEL_TYPE_INT = 44;
  public static final int COLLOQUIAL_VERSION_INT = 45;
  public static final int DESCRIPTION_INT = 46;
  public static final int EDITION_INT = 47;
  public static final int ENTITLEMENT_DATA_REQUIRED_INT = 48;
  public static final int ENTITLEMENT_KEY_INT = 49;
  public static final int GENERATOR_INT = 50;
  public static final int PERSISTENT_ID_INT = 51;
  public static final int PRODUCT_INT = 52;
  public static final int PRODUCT_FAMILY_INT = 53;
  public static final int REVISION_INT = 54;
  public static final int SUMMARY_INT = 55;
  public static final int UNSPSC_CODE_INT = 56;
  public static final int UNSPSC_VERSION_INT = 57;

  // other
  public static final int UNKNOWN_INT = 99;
  // End Constant Index values
  // ---------------------------------------------------------------------------

  // Constant Item Names defined in RFC 9393 Table 9
  // -----------------------------------------------------

  // concise-swid-tag map
  public static final String TAG_ID_STR = "tag-id";
  public static final String SOFTWARE_NAME_STR = "software-name";
  public static final String ENTITY_STR = "entity";
  public static final String EVIDENCE_STR = "evidence";
  public static final String LINK_STR = "link";
  public static final String SOFTWARE_META_STR = "software-meta";
  public static final String PAYLOAD_STR = "payload";
  public static final String HASH_STR = "hash"; // belongs to resource-collection group
  public static final String CORPUS_STR = "corpus";
  public static final String PATCH_STR = "patch";
  public static final String MEDIA_STR = "media";
  public static final String SUPPLEMENTAL_STR = "supplemental";
  public static final String TAG_VERSION_STR = "tag-version";
  public static final String SOFTWARE_VERSION_STR = "software-version";
  public static final String VERSION_SCHEME_STR = "version-scheme";

  // global-attributes group
  public static final String LANG_STR = "lang";

  // resource-collection group
  public static final String DIRECTORY_STR = "directory";
  public static final String FILE_STR = "file";
  public static final String PROCESS_STR = "process";
  public static final String RESOURCE_STR = "resource";
  public static final String SIZE_STR = "size";
  public static final String FILE_VERSION_STR = "file-version";
  public static final String KEY_STR = "key";
  public static final String LOCATION_STR = "location";
  public static final String FS_NAME_STR = "fs-name";
  public static final String ROOT_STR = "root";
  public static final String PATH_ELEMENTS_STR = "path-elements";
  public static final String PROCESS_NAME_STR = "process-name";
  public static final String PID_STR = "pid";
  public static final String TYPE_STR = "type";

  // other
  public static final String UNASSIGNED_STR = "Unassigned";

  // entity-entry map
  public static final String ENTITY_NAME_STR = "entity-name";
  public static final String REG_ID_STR = "reg-id";
  public static final String ROLE_STR = "role";
  public static final String THUMBPRINT_STR = "thumbprint";

  // evidence-entry map
  public static final String DATE_STR = "date";
  public static final String DEVICE_STR = "device";

  // link-entry map
  public static final String ARTIFACT_STR = "artifact";
  public static final String HREF_STR = "href";
  public static final String OWNERSHIP_STR = "ownership";
  public static final String REL_STR = "rel";
  public static final String MEDIA_TYPE_STR = "media-type";
  public static final String USE_STR = "use";

  // software-meta-entry map
  public static final String ACTIVATION_STATUS_STR = "activation-status";
  public static final String CHANNEL_TYPE_STR = "channel-type";
  public static final String COLLOQUIAL_VERSION_STR = "colloquial-version";
  public static final String DESCRIPTION_STR = "description";
  public static final String EDITION_STR = "edition";
  public static final String ENTITLEMENT_DATA_REQUIRED_STR = "entitlement-data-required";
  public static final String ENTITLEMENT_KEY_STR = "entitlement-key";
  public static final String GENERATOR_STR = "generator";
  public static final String PERSISTENT_ID_STR = "persistent-id";
  public static final String PRODUCT_STR = "product";
  public static final String PRODUCT_FAMILY_STR = "product-family";
  public static final String REVISION_STR = "revision";
  public static final String SUMMARY_STR = "summary";
  public static final String UNSPSC_CODE_STR = "unspsc-code";
  public static final String UNSPSC_VERSION_STR = "unspsc-version";

  // other
  public static final String UNKNOWN_STR = "unknown";

  // End Constant Item Names
  // -----------------------------------------------------------------------------

  private static final String[][] indexNames = {
    {"0", "tag-id"},
    {"1", "software-name"},
    {"2", "entity"},
    {"3", "evidence"},
    {"4", "link"},
    {"5", "software-meta"},
    {"6", "payload"},
    {"7", "hash"},
    {"8", "corpus"},
    {"9", "patch"},
    {"10", "media"},
    {"11", "supplemental"},
    {"12", "tag-version"},
    {"13", "software-version"},
    {"14", "version-scheme"},
    {"15", "lang"},
    {"16", "directory"},
    {"17", "file"},
    {"18", "process"},
    {"19", "resource"},
    {"20", "size"},
    {"21", "file-version"},
    {"22", "key"},
    {"23", "location"},
    {"24", "fs-name"},
    {"25", "root"},
    {"26", "path-elements"},
    {"27", "process-name"},
    {"28", "pid"},
    {"29", "type"},
    {"30", "Unassigned"},
    {"31", "entity-name"},
    {"32", "reg-id"},
    {"33", "role"},
    {"34", "thumbprint"},
    {"35", "date"},
    {"36", "device-id"},
    {"37", "artifact"},
    {"38", "href"},
    {"39", "ownership"},
    {"40", "rel"},
    {"41", "media-type"},
    {"42", "use"},
    {"43", "activation-status"},
    {"44", "channel-type"},
    {"45", "colloquial-version"},
    {"46", "description"},
    {"47", "edition"},
    {"48", "entitlement-data-required"},
    {"49", "entitlement-key"},
    {"50", "generator"},
    {"51", "persistent-id"},
    {"52", "product"},
    {"53", "product-family"},
    {"54", "revision"},
    {"55", "summary"},
    {"56", "unspsc-code"},
    {"57", "unspsc-version"}
  };

  /**
   * Searches Rfc 9393 Items Names for match to a specified item name and returns the index
   *
   * @param itemName Iem Name specified in section 6.1 of rfc 9393
   * @return int id of algorithm
   */
  public static int getIndex(String itemName) {
    for (int i = 0; i < indexNames.length; i++) {
      if (itemName.compareToIgnoreCase(indexNames[i][1]) == 0) return i;
    }
    return UNKNOWN_INT;
  }

  /**
   * Searches for an rfc 9393 specified index and returns the item name associated with the index
   *
   * @param index int rfc 939 sepcified index value
   * @return String item name associated with the index
   */
  public static String getItemName(int index) {
    for (int i = 0; i < indexNames.length; i++) {
      if (index == Integer.parseInt(indexNames[i][0])) return indexNames[i][1];
    }
    return UNKNOWN_STR;
  }
}
