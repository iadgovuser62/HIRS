package hirs.utils.signature.cose;

import com.authlete.cbor.CBORDecoder;
import com.authlete.cbor.CBORDecoderOptions;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.tag.CBORTagProcessor;
import com.authlete.cose.COSEException;
import com.authlete.cose.COSEProtectedHeader;
import com.authlete.cose.COSESign1;
import com.authlete.cose.COSESign1Builder;
import com.authlete.cose.COSEUnprotectedHeader;
import com.authlete.cose.SigStructure;
import hirs.utils.signature.cose.Cbor.CborBstr;
import hirs.utils.signature.cose.Cbor.CborTagProcessor;
import hirs.utils.rim.unsignedRim.cbor.ietfCorim.CoRim;
import hirs.utils.rim.unsignedRim.cbor.ietfCorim.CoRimParser;
import hirs.utils.rim.unsignedRim.cbor.ietfCorim.MetaMap;
import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.Coswid;
import hirs.utils.rim.unsignedRim.cbor.tcgCompRimCoswid.TcgCompRimCoswidParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parser for COSE Formatted data per RFC 9052
 */
public class CoseParser {
    @Setter
    @Getter
    int coseTag = 0;

    byte[] toBeSigned = null;

    byte[] payload = null;

    byte[] signature = null;
    // COSE Generic Header
    @Setter
    @Getter
    int algIdInt = 0;
    @Setter
    @Getter
    String algIdentifier = "";

    byte[] keyIdBytes = null;
    @Setter
    @Getter
    String keyIdentifier = "";
    @Setter
    @Getter
    String contentType = "";

    byte[] protectedHeaders = null;
    @Setter
    @Getter
    String autheleteDecode = "";

    CborTagProcessor cborTag = null;

    MetaMap mmap = null;
    /**
     * Parser constructor to fill calss variables
     * @param coseData Byte array holding the COSE data
     */
    public CoseParser(byte[] coseData ) {

        CBORDecoder cborDecoder = new CBORDecoder(coseData);
        COSESign1 sign1 = null;
        String process = "Processing Cose toBeSigned for verification: ";
        String status = "Parsing Cose Tag, expecting tag 18 (cose-sign1):";

        CborTagProcessor ctp = new CborTagProcessor(coseData);
        coseTag = ctp.getTagId();
        if ((!ctp.isCose()) && (!ctp.isCorim())){
            throw new RuntimeException("Error parsing COSE signature: COSE tag of " + coseTag + " found but only cose-sign1 (18) is supported");
        }

        try {
            status = "Decoding COSE object";
            CBORItem coseObject= cborDecoder.next();
            //System.out.println( coseObject.prettify());
            ArrayList<Object> parsedata = (ArrayList) coseObject.parse();
            COSESign1 signOne = COSESign1.build(parsedata);
            status = "Decoding COSE Protected Header";
            COSEProtectedHeader pheader = signOne.getProtectedHeader();
            status = "Decoding COSE Unprotected Header";
            COSEUnprotectedHeader uheader = signOne.getUnprotectedHeader();
            status = "Checking Cose headers for required attributes";
            if (pheader.getAlg() != null) {
                algIdInt = (int) pheader.getAlg();
            } else if (uheader.getAlg() != null) {
                algIdInt = (int) uheader.getAlg();
            } else {
                throw new RuntimeException("Algorithm ID required but not found in COSE header");
            }
            algIdentifier = CoseAlgorithm.getAlgName(algIdInt);
            if (pheader.getKid() != null) {
                keyIdBytes = pheader.getKid();
            } else if (uheader.getKid() != null) {
                keyIdBytes = uheader.getKid();
            } else {
                // CoRIM is failing here...
              //  throw new RuntimeException("Key ID required but not found in COSE header");
            }
            if (keyIdBytes != null) keyIdentifier = hexToString(keyIdBytes);

            if (pheader.getContentType() != null) {
                contentType = pheader.getContentType().toString();
            }
            // Look for corim-meta (index 8)
            if (pheader.getDecodedContent() !=null) {
                List<CBORPair> cborPairs= (List<CBORPair>) pheader.getPairs();
                Iterator pairs = cborPairs.iterator();
                while (pairs.hasNext()) {
                    CBORPair pair = (CBORPair) pairs.next();
                    if (Integer.parseInt(pair.getKey().toString()) == CoRim.CORIM_META_MAP ) {
                        byte[] corimMap = pair.getValue().encode();
                        mmap = new MetaMap(corimMap);
                    }
                }

            }

            status = "retrieving signature from COSE object";
            signature = signOne.getSignature().getValue();
            status = "Retrieving payload from COSE object";
            byte[] encodedPayload = signOne.getPayload().encode();
            payload = CborBstr.removeByteStringIfPresent(encodedPayload);
            checkForTag(payload);

        } catch (IOException e) {
            throw new RuntimeException(process+status+" :" + e.getMessage());
        } catch (COSEException e) {
            throw new RuntimeException(process+status+" :" + e.getMessage());
        }
    }

    /**
     * Checks the payload for a valid tag
     * by parsing the first byte of the payload as a tag
     * and checking for one of the supported tags by this application
     * If a supported tag is found the payload and coswidTag references are adjusted
     * @param payloadData
     * @return true if a valid tag is found
     */
    private boolean checkForTag(byte[] payloadData) {
        boolean tagFound = false;
        CborTagProcessor tmpTag = new CborTagProcessor(payloadData);
        if (tmpTag.isTagged()) {
            cborTag = tmpTag;
            tagFound = true;
            payload = tmpTag.getContent();
        } else {
            cborTag = new CborTagProcessor();
        }
        return tagFound;
    }
    /**
     * Method to print hex data
     * @param data
     */
    public String hexToString(byte[] data){
        StringBuilder sb2 = new StringBuilder();
        for (byte b : data) {
            sb2.append(String.format("%02X", b));
        }
        return sb2.toString();
    }

    /**
     * Looks up the COSE types defined in Table 1 of RFC 9052.
     * Also proccesses CoRim options for COSE.
     * @param tag the CBOR Tag (int) defined in Table 1
     * @return a String defined in Table 1 that corresponds to the tag
     */
    public String coseTagLookup(int tag){
        String coseType = "";
        switch (tag) {
            case 98: coseType = "cose-sign"; break;
            case 18: coseType = "cose-sign1"; break;
            case 96: coseType = "cose-encrypt"; break;
            case 16: coseType = "cose-encrypt0"; break;
            case 97: coseType = "cose-mac"; break;
            case 17: coseType = "cose-mac0"; break;
            default: coseType = CoRim.getTagLabel(tag); break;
        }
        return coseType;
    }

    /**
     * Default toString
     * @return Authelete defined representation of COSE data
     */
    public String toString()   {
        try {
            return toString("pretty");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param format  empty (default String) or "pretty"
     * @return a formated string representation of the data in the COSE object
     */
    public String toString(String format) throws IOException {
       String returnString = "";
       boolean isCoRim = false;
       if (format.compareToIgnoreCase("pretty")==0) {
           returnString = "COSE Signed object:\n";
           returnString += "tag = " + coseTagLookup(coseTag) + "\n";
           returnString += "Protected Header Contents: " + "\n";
           returnString += "  Algorithm = " + algIdentifier + "\n";
           returnString += "  KeyId = " + keyIdentifier + "\n";
           if(! contentType.isEmpty()) returnString += "  Content Type = " + contentType + "\n";
           // If Content Type indicates CoRim then process meta-map within the Protected Header
           if (mmap != null) {
               returnString += "  Signer Name = " + mmap.getSignerName() + "\n";
               if (! mmap.getSignerUri().isEmpty())returnString += "  Signer URI = " + mmap.getSignerUri() + "\n";
               if (! mmap.getNotBeforeStr().isEmpty())returnString += "  Validity notBefore = " + mmap.getNotBeforeStr() + "\n";
               if (! mmap.getNotAfterStr().isEmpty())returnString += "  Validity notAfter = " + mmap.getNotAfterStr() + "\n";
           }
           returnString += "COSE Payload: " + "\n";

           if (contentType.compareToIgnoreCase("application/rim+cbor")==0) {
               returnString += "  Processing payload as CoRim:"  + "\n";
               CoRimParser cparser = new CoRimParser(payload);
               returnString += cparser.toString();
           } else if (! cborTag.isTagged()) {
               returnString += "Untagged Payload of length " + payload.length + " bytes found\n";
               String pdata = hexToString(payload);
               String formattedPdata = pdata.replaceAll("(.{100})", "$1\n");
               returnString += "Payload data: \n" + formattedPdata + "\n";
           } else {
               returnString += "Payload tag of type " + cborTag.getTagId() + " found: \n";
               // Process tags of type we know
               if (cborTag.isCoswid()){
                   Coswid cswid = new Coswid();
                   TcgCompRimCoswidParser cswidParser = new TcgCompRimCoswidParser(payload);
                   returnString += cswidParser.toString(format);
               } else {   // Else just dump the raw data
                   returnString += "Data found is:\n";
                   String pdata = hexToString(payload);
                   String formattedPdata = pdata.replaceAll("(.{100})", "$1\n");
                   returnString += "Payload data: \n" + formattedPdata + "\n";
               }
           }
           String sig = hexToString(signature);
           String formattedSig = sig.replaceAll("(.{100})", "$1\n");
           returnString += "Signature = \n" + formattedSig;
       } else {
           returnString = autheleteDecode;
       }
        return returnString;
    }

    public byte[] getToBeSigned() {
        return toBeSigned == null ? null : toBeSigned.clone();
    }

    public byte[] getPayload() {
        return payload == null ? null : payload.clone();
    }

    public byte[] getSignature() {
        return signature == null ? null : signature.clone();
    }

    public byte[] getKeyIdBytes() {
        return keyIdBytes == null ? null : keyIdBytes.clone();
    }

    public byte[] getProtectedHeaders() {
        return protectedHeaders == null ? null : protectedHeaders.clone();
    }

    // ✅ Safe SETTERS
    public void setToBeSigned(byte[] toBeSigned) {
        this.toBeSigned = toBeSigned == null ? null : toBeSigned.clone();
    }

    public void setPayload(byte[] payload) {
        this.payload = payload == null ? null : payload.clone();
    }

    public void setSignature(byte[] signature) {
        this.signature = signature == null ? null : signature.clone();
    }

    public void setKeyIdBytes(byte[] keyIdBytes) {
        this.keyIdBytes = keyIdBytes == null ? null : keyIdBytes.clone();
    }

    public void setProtectedHeaders(byte[] protectedHeaders) {
        this.protectedHeaders = protectedHeaders == null ? null : protectedHeaders.clone();
    }

}
