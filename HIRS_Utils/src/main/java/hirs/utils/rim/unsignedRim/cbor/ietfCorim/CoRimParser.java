package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import com.authlete.cbor.CBORDecoder;
import com.authlete.cbor.CBORItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import hirs.utils.signature.cose.Cbor.CborTagProcessor;
import hirs.utils.rim.unsignedRim.cbor.ietfCorim.comid.Comid;
import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.Coswid;
import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.CoswidParser;
import hirs.utils.rim.unsignedRim.common.IanaHashAlg;
import hirs.utils.rim.unsignedRim.common.measurement.Measurement;
import hirs.utils.rim.unsignedRim.common.measurement.MeasurementType;
import lombok.Getter;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Long.valueOf;

/**
 * Class that parses a Cbor encoded CoRim object.
 * CoRims are defined by IETF , current version of the spec is:  https://datatracker.ietf.org/doc/draft-ietf-rats-corim/
 * Note that when document becomes an approved specification an rfc number will be assigned and this link will change.
 */
public class CoRimParser extends CoRim  {
    Map<String, Object> parsedData = null;
    protected JsonNode rootNode = null;
    protected IanaHashAlg algInfo = null;
    protected CoRim corim = null;
    protected List<Comid> comidList = new ArrayList<>();
    protected List<Coswid> coswidList = new ArrayList<Coswid>();
    List<Object[]> dependentRims = new ArrayList<>();
    /** Contains a list of measurements pertaining to various objects within the CoRIM (CoMID, CoSWID, etc.).
     * Populated after construction and parsing. */
    @Getter
    List<Measurement> measurements = new ArrayList<>();

    /**
     * Constructor used to parse Cbor Encoded Corim data.
     * @param corimData byte arra holding the cbor encoded corim data
     */
    public CoRimParser(byte[] corimData) {
        byte[] untaggedCorim = corimData, uri_digest = null;
        String status = "", uri = "";
        int uri_digest_alg = 0;
        Format format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            CBORDecoder cborDecoder = new CBORDecoder(corimData);
            CBORItem corimObject= cborDecoder.next();
            LinkedHashMap CoRimMap = (LinkedHashMap)corimObject.parse();
            id = (String)CoRimMap.get(CorimItems.CORIM_ID_TYPE_CHOICE_INT);
            ArrayList tagTypeChoice =  (ArrayList)CoRimMap.get(CorimItems.CONCISE_TAG_TYPE_CHOICE_INT);
            // Parse list of concise tags contained within the corim-map
            for (int tagCount = 0; tagCount < tagTypeChoice.size(); tagCount++) {
                status = "Processing CoRim tag";
                CborTagProcessor ctp = new CborTagProcessor((byte[]) tagTypeChoice.get(tagCount));
                byte[] corimContent = ctp.getContent();
                if (ctp.isCorim()) {
                    corimTag = ctp.getTagId();
                    status = "Process CoRim tagged content";

                    if (ctp.isCoswid()) { // process content as CoSwid
                        CoswidParser cospar = new CoswidParser(corimContent);
                        coswidList.add(cospar.getCoswid());
                    } else if (ctp.isComid()) { // process content as CoMid
                        comidList.add(new Comid(corimContent));
                    } else if (ctp.isCotl()) { // process content as CoTL, not supported for now so throw an exception ...
                        // Todo add CoTL processing here.
                        throw new RuntimeException("Error parsing CoRim data, CoTL data (CBor Tag 508) found within the CoRIM data is not currently supported");
                    }
                    // process corim defined data
                    id = (String) CoRimMap.get(CorimItems.CORIM_ID_TYPE_CHOICE_INT);
                    status = "Process CoRim dependent-rims";
                    ArrayList dependantRims = (ArrayList) CoRimMap.get(CorimItems.CORIM_LOCATOR_MAP_INT);
                    if (dependantRims != null) {
                        Iterator corimLocators = dependantRims.iterator();
                        if (corimLocators != null) {
                            while (corimLocators.hasNext()) {
                                status = "processing CoRim locators";
                                LinkedHashMap locator = (LinkedHashMap) corimLocators.next();
                                if (locator.get(0) != null) uri = (String) locator.get(0).toString();
                                if (locator.get(1) != null) { //
                                    List<List<String>> list = new ArrayList<>();
                                    ArrayList thumbprint = (ArrayList) locator.get(1); //sec-common-hash-entry
                                    uri_digest_alg = thumbprint.get(0).hashCode();
                                    byte[] digest = (byte[]) thumbprint.get(1);
                                    uri_digest = new byte[digest.length];
                                    System.arraycopy(digest, 0, uri_digest, 0, digest.length);
                                }
                                if (uri != null) {
                                    dependentRims.add(new Object[]{uri, uri_digest_alg, uri_digest});
                                }
                            }
                        }
                        status = "Processing CoRim profile";
                        LinkedHashMap profileList = (LinkedHashMap) CoRimMap.get(CorimItems.PROFILE_TYPE_CHOICE_INT);
                        if (profileList != null) {
                            CborTagProcessor ctpProfile = new CborTagProcessor();
                            if (!ctpProfile.isOid()) {
                                profile = ctpProfile.getOid();
                            } else {
                                profile = (String) CoRimMap.get(CorimItems.PROFILE_TYPE_CHOICE_INT);
                            }
                        }
                        status = "Process CoRim validity-map";
                        LinkedHashMap validityMap = (LinkedHashMap) CoRimMap.get(CorimItems.VALIDITY_MAP_INT);

                        if (validityMap.get(0) != null) {  // not before
                            int before = (int) validityMap.get(0);
                            notBefore = valueOf(before);
                            Date date = new Date(notBefore * 1000);
                            notBeforeStr = format.format(date);
                        }
                        if (validityMap.get(1) != null) {  // not before
                            int after = (int) validityMap.get(1);
                            notAfter = valueOf(after);
                            Date date = new Date(notAfter * 1000);
                            notAfterStr = format.format(date);
                        }
                        status = " Processing CoRim entities";
                        ArrayList entities = (ArrayList) CoRimMap.get(CorimItems.CORIM_ENTITY_MAP_INT);
                        LinkedHashMap corimEntityMap = (LinkedHashMap) entities.get(0);
                        entityName = corimEntityMap.get(0).toString();
                        if (corimEntityMap.get(1) != null) {
                            entityRegId = corimEntityMap.get(1).toString();
                        }
                        if (corimEntityMap.get(2) != null) {
                            ArrayList role = (ArrayList) corimEntityMap.get(2);
                            if (role.get(0) != null) {
                                int roleVal = (int) role.get(0);
                                switch (roleVal) {
                                    case 0:
                                        entityRole = "tag-creator";
                                        break;
                                    case 1:
                                        entityRole = "manifest-creator";
                                        break;
                                    case 2:
                                        entityRole = "manifest-signer";
                                        break;
                                    default:
                                        entityRole = "unknown CoRim role";
                                        break;
                                }
                            } else {
                                entityRole = "unspecified CoRim role";
                            }
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error parsing CoRim data: "+ status + ": " +e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Error parsing CoRim data: "+ status + ": " +e.getMessage());
        }

        // Add measurement list
        extractMeasurements();
    }

    /**
     * Extracts a list of measurements from various objects belonging to the CoRIM, including CoSWIDs or CoMIDs.
     */
    private void extractMeasurements() {
        // CoSWIDs
        for (Coswid cswid : coswidList) {
            measurements.addAll(cswid.getMeasurements());
        }

        // CoMIDs
        for (Comid cmid : comidList) {
            // Start with reference triples
            // Traverse from the current reference triple -> reference claims list -> mval -> digests
            var refTripleList = cmid.getTriples().getReferenceTriples();
            for (var refTriple : refTripleList) {
                var currRefClaims = refTriple.getRefClaims();
                var manufacturer = refTriple.getRefEnv().getComidClass().getVendor();
                var model = refTriple.getRefEnv().getComidClass().getModel();
                var index = refTriple.getRefEnv().getComidClass().getIndex();

                for (var refClaims : currRefClaims) {
                    // Get info about version for current measured environment
                    var envVersion = refClaims.getMval().getVersion();
                    var serialNum = refClaims.getMval().getSerialNumber();
                    var currDigests = refClaims.getMval().getDigests();
                    for (var digest : currDigests) {
                        Measurement measurement = new Measurement();
                        measurement.setMeasurementType(MeasurementType.UNKNOWN); // TODO: Possibly change?
                        measurement.setAlg(digest.getAlg());
                        measurement.setMeasurementBytes(digest.getVal());
                        if (manufacturer != null) measurement.setManufacturer(manufacturer);
                        if (model != null) measurement.setModel(model);
                        if (index != null) measurement.setIndex(index);
                        if (envVersion != null) measurement.setRevision(envVersion.getVersion());
                        if (serialNum != null) measurement.setSerialNumber(serialNum);
                        measurements.add(measurement);
                    }
                }
            }
        }
    }

    /**
     * Provides a human-readable representation of the DoRim object
     * @return String representing the CoRIm object in human-readable form
     */
    public String toString() {
        HexFormat hexTool =  HexFormat.of();
        String indent = "    ";
        String returnString = "";
        returnString += indent + "Corim id = " + getId()  + "\n";
        returnString += indent + "Corim tag = " + getTagLabel(getCorimTag())  + " (" + getCorimTag() + ") " +  "\n";
        // Iterate through CoMID list
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        // Add serializer for hexadecimal byte[] printing
        SimpleModule module = new SimpleModule();
        module.addSerializer(byte[].class, new HexByteArraySerializer());
        mapper.registerModule(module);
        // Enable print features
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        for (int currComid = 0; currComid < comidList.size(); currComid++) {
            returnString += indent + "CoMID at index " + currComid + ":" + "\n";
            try {
                returnString += mapper.writeValueAsString(comidList.get(currComid)).indent(4);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (!dependentRims.isEmpty()) {
            returnString += indent + "Dependent RIMs (manifests or related files):" + "\n" ;
            for (Object[] row : dependentRims) {
                returnString += indent + indent + "Uri = " + row[0] + "\n";
                if (row[1] != null ){
                    String hash = hexTool.formatHex((byte[])row [2]);
                    returnString +=  indent + indent + "URI digest = " + hash + "\n";
                    int alg = (int) row[1];
                    IanaHashAlg Algorithm = IanaHashAlg.getAlgFromId(alg);
                    returnString +=  indent + indent + indent + "URI digest Algorithm = " + Algorithm.getAlgName() + "\n";
                }
                if (! profile.isEmpty()) {
                    returnString += indent + "Profile is:" + profile + "\n";
                }
            }
        } // dependentRims
        // Validity
        if ((! notBeforeStr.isEmpty()) || (! notAfterStr.isEmpty())) {
            returnString += indent + "Corim Validity:" + "\n";
        }
        if (! notBeforeStr.isEmpty()) {
            returnString += indent + indent + "notBefore: " + notBeforeStr + "\n";
        }
        if (! notAfterStr.isEmpty()) {
            returnString += indent + indent + "notAfter: " + notAfterStr + "\n";
        }
        // Process Entity Map
        if (! entityName.isEmpty()){
            returnString += indent + "Entity Info: " + "\n";
            returnString += indent + indent +  "Entity Name: " + entityName + "\n";
        }
        if (! entityRegId.isEmpty()){
            returnString += indent + indent +  "Entity Registration ID (URI): " +  entityRegId + "\n";
        }
        if (! entityRole.isEmpty()){
            returnString += indent + indent +  "Entity Role: " +  entityRole + "\n";
        }
        return returnString;
    }
}
