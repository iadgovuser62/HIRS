package hirs.utils.signature.cose;

import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORDecoder;
import com.authlete.cbor.CBORDecoderOptions;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORNull;
import com.authlete.cbor.CBORTaggedItem;
import com.authlete.cbor.tag.CBORTagProcessor;
import com.authlete.cose.COSEException;
import com.authlete.cose.COSEProtectedHeader;
import com.authlete.cose.COSEProtectedHeaderBuilder;
import com.authlete.cose.COSESign1;
import com.authlete.cose.COSESign1Builder;
import com.authlete.cose.COSEUnprotectedHeader;
import com.authlete.cose.COSEUnprotectedHeaderBuilder;
import com.authlete.cose.SigStructure;
import com.authlete.cose.SigStructureBuilder;
import hirs.utils.crypto.AlgorithmsIds;
import hirs.utils.signature.SignatureFormat;
import hirs.utils.signature.cose.Cbor.CborTagProcessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for implementing rfc rfc9052 CBOR Object Signing and Encryption (COSE)
 * Refer to https://datatracker.ietf.org/doc/html/rfc9053
 *
 * COSE_Sign = [
 *        Headers,
 *        payload : bstr / nil,
 *        signatures : [+ COSE_Signature]
 *    ]
 *    From section 4.4 of rfc 9052 "How to compute a signature:
 *    1.  Create a Sig_structure and populate it with the appropriate fields.
 *    2.  Create the value ToBeSigned by encoding the Sig_structure to a
 *        byte string, using the encoding described in Section 9.
 *    3.  Call the signature creation algorithm, passing in K (the key to
 *        sign with), alg (the algorithm to sign with), and ToBeSigned (the value to sign).
 *    4.  Strip off the DER encoding from the Signature field placed on by
 *        Java.Security. Even though RFC 9052 does not specify a format,
 *        The COSE Working Groups test patterns use a "Raw" (IEEE P1363) format.
 *    5.  Place the resulting signature value in the correct location.
 *        This is the "signature" field of the COSE_Signature or COSE_Sign1 structure.
 */
public class CoseSignature implements SignatureFormat {

    byte[] toBeSigned = null;

    byte[] payload = null;

    byte[] signature = null;
    // COSE Generic Header
    @Setter
    @Getter
    int alg_id = 0;
    byte[] keyId = null;
    byte[] protectedHeaders = null;
    COSESign1Builder coseBuilder = null;

    /**
     * Default CoseSignature constructor
     */
    public CoseSignature() {
    }

    /**
     * Create toBeSigned using supplied kid and algorithm for testing only.
     * Kid will be assigned to the unprotected header for tests.
     * @param alg IANA registered COSE Algorithm String
     * @param kid Key Identifier
     * @param payload  data to be placed in the payload
     * @param cert signing cert to embed (if embedded parameter is set to true)
     * @param embedded embed signing certificate per RFC 9360
     * @return the COSE_Sign1 toBeSigned data
     */
    public byte [] createToBeSigned (String alg, String kid, byte[] payload, X509Certificate cert, boolean embedded)
            throws CertificateEncodingException, NoSuchAlgorithmException {
        alg_id = CoseAlgorithm.getAlgId(alg);
        return createToBeSigned(alg_id , kid.getBytes(StandardCharsets.UTF_8),payload, true, cert,
                embedded);
    }

    /**
     * Create toBeSigned using supplied kid and algorithm for testing only.
     * Kid will be assigned to the unprotected header for tests.
     * @param algId IANA registered COSE Algorithm String
     * @param kid Key Identifier
     * @param data  data to be placed in the payload
     * @param signingCert a signing certificate used if the embedded parameter is true
     * @param embedded if true, embeds the signing certificate and thumbprint per RFC 9360
     * @return the COSE_Sign1 toBeSigned data
     * @throws CertificateEncodingException
     * @throws NoSuchAlgorithmException
     */
    private byte[] createToBeSigned (int algId, byte[] kid, byte[] data,
                                     boolean useUnprotectedKid, X509Certificate signingCert, boolean embedded)
            throws CertificateEncodingException, NoSuchAlgorithmException {
        COSEProtectedHeader pHeader = null;
        COSEUnprotectedHeader uHeader = null;
        coseBuilder = new COSESign1Builder();
        // Create protected Header , only add kid if flag is false
        COSEProtectedHeaderBuilder pHeaderBuilder;
        if (useUnprotectedKid) {
            pHeaderBuilder = new COSEProtectedHeaderBuilder().alg(alg_id);
            uHeader = new COSEUnprotectedHeaderBuilder().kid(kid).build();
            coseBuilder.unprotectedHeader(uHeader);
        } else {
            pHeaderBuilder = new COSEProtectedHeaderBuilder().alg(alg_id).kid(kid);
        }
        // Embed per RFC 9360
        if (embedded) {
            // Add x5chain
            pHeaderBuilder.put(new CBORInteger(33), signingCert.getEncoded());
            // Add x5t (thumbprint)
            List<CBORItem> x5tList = new ArrayList<>();
            x5tList.add(new CBORInteger(CoseAlgorithm.coseSha256)); // hashAlg
            x5tList.add(new CBORByteArray(getThumbprint(signingCert))); // hashValue
            CBORItemList x5tItemList = new CBORItemList(x5tList);
            pHeaderBuilder.put(new CBORInteger(34), x5tItemList);
        }
        pHeader = pHeaderBuilder.build();
        return finalizeToBeSigned(data, pHeader);
    }

    /**
     * Creates the "to be signed" data specified in rfc 9052 using an already-built protected header (such as for
     * extensions).
     * Follows steps 1 and 2 of the  "How to compute a signature" section from:
     * https://datatracker.ietf.org/doc/html/rfc9052#section-4.4
     *
     * 1.  Create a Sig_structure and populate it with the appropriate fields.
     * 2.  Create the value ToBeSigned by encoding the Sig_structure to a
     *        byte string, using the encoding described in Section 9.
     *
     * @param signCert  X509 cert holding the SKID to use as the Key Identifier.
     * @param data      Data to be signed
     * @param protectedHeader The protected header to be utilized
     * @return          the COSE_Sign1 toBeSigned data
     */
    public byte[] createToBeSigned(X509Certificate signCert, byte[] data, COSEProtectedHeader protectedHeader)
            throws NoSuchAlgorithmException {
        if(signCert == null)
            throw new RuntimeException("COSE Signature Failure: Signer Certificate waS not provided");
        if(signCert == null)
            throw new RuntimeException("COSE Signature Failure: Signer PrivateKey was not provided");
        CoseAlgorithm cAlgorithm = new CoseAlgorithm();
        coseBuilder = new COSESign1Builder();
        String alg = AlgorithmsIds.translateAlgId(AlgorithmsIds.ALG_TYPE_SIG, AlgorithmsIds.SPEC_X509_ALG, signCert.getSigAlgName(), AlgorithmsIds.SPEC_COSE_ALG);
        alg_id = cAlgorithm.getAlgId(alg);
        byte[] skid = signCert.getExtensionValue("2.5.29.14"); // OID for SKID
        // Check for required data before processing...
        if (alg_id == 0)
            throw new RuntimeException("COSE Signature Failure: Algorithm ID was not provided");
        if (skid.length == 0)
            throw new RuntimeException("COSE Signature Failure: Key Identifier was not provided");
        return finalizeToBeSigned(data, protectedHeader);
    }

    /**
     * Creates the "to be signed" data specified in rfc 9052
     * Follows steps 1 and 2 of the  "How to compute a signature" section from:
     * https://datatracker.ietf.org/doc/html/rfc9052#section-4.4
     *
     * 1.  Create a Sig_structure and populate it with the appropriate fields.
     * 2.  Create the value ToBeSigned by encoding the Sig_structure to a
     *        byte string, using the encoding described in Section 9.
     *
     * @param signCert  X509 cert holding the SKID to use as the Key Identifier.
     * @param data      Data to be signed
     * @return          the COSE_Sign1 toBeSigned data
     */
    @Override
    public byte[] createToBeSigned(X509Certificate signCert, byte[] data) throws IOException, NoSuchAlgorithmException {
        if(signCert == null)
            throw new RuntimeException("COSE Signature Failure: Signer Certificate waS not provided");
        if(signCert == null)
            throw new RuntimeException("COSE Signature Failure: Signer PrivateKey was not provided");
        CoseAlgorithm cAlgorithm = new CoseAlgorithm();
        coseBuilder = new COSESign1Builder();
        String alg = AlgorithmsIds.translateAlgId(AlgorithmsIds.ALG_TYPE_SIG, AlgorithmsIds.SPEC_X509_ALG, signCert.getSigAlgName(), AlgorithmsIds.SPEC_COSE_ALG);
        alg_id = cAlgorithm.getAlgId(alg);
        byte[] skid = signCert.getExtensionValue("2.5.29.14"); // OID for SKID
        // Check for required data before processing...
        if (alg_id == 0)
            throw new RuntimeException("COSE Signature Failure: Algorithm ID was not provided");
        if (skid.length == 0)
            throw new RuntimeException("COSE Signature Failure: Key Identifier was not provided");
        COSEProtectedHeader pHeader = new COSEProtectedHeaderBuilder().alg(alg_id).kid(skid).build();
        return finalizeToBeSigned(data, pHeader);
    }

    /**
     * Creates the toBeSigned structure from a pre-processed header and payload data
     * @param data
     * @param pHeader
     * @return the COSE_Sign1 toBeSigned data
     */
    private byte[] finalizeToBeSigned(byte[] data, COSEProtectedHeader pHeader){
        CBORByteArray encodedPayload = new CBORByteArray(data);
        SigStructure structure = new SigStructureBuilder()
                .signature1()
                .bodyAttributes(pHeader)
                .payload(encodedPayload)
                .build();
        toBeSigned = structure.encode();
        coseBuilder.payload(encodedPayload);
        coseBuilder.protectedHeader(pHeader);
        payload = data;
        return toBeSigned;
    }
    /**
     * follows the "The steps for verifying a signature are" of section 4.4.  Signing and Verification Process
     *  https://datatracker.ietf.org/doc/html/rfc9052#section-4.4
     *  Steps 1 and 2.
     *  Note that step 3 (verify, the final step) is handled by a Cryptographic Engine
     *
     * @param coseData
     * @return
     */
    public byte[] getToBeVerified (byte[] coseData) {
        CBORDecoder cborDecoder = new CBORDecoder(coseData);

        String process = "Processing toBeSigned for verification: ";
        String status = "Parsing Cose Tag, expecting tag 18 (cose-sign1):";

        CborTagProcessor ctp = new CborTagProcessor(coseData);
        int coseTag = ctp.getTagId();
        if (coseTag != CoseType.coseSign1) {
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
            status = "Checking Cose headers for required Algorithm Identifier";
            if (pheader.getAlg() != null) {
                Object algObject = (Object)pheader.getAlg();
                if (algObject instanceof String ) {  // library will return a String if algorithm is unknown
                    String sAlg = (String) pheader.getAlg();
                    if (sAlg.compareToIgnoreCase("unknown")==0) {
                        throw new RuntimeException("Unknown Algorithm Identifier found in COSE header");
                    }
                } else {
                    alg_id = (int) pheader.getAlg();
                }
            } else if (uheader.getAlg() != null) {
                alg_id = (int) uheader.getAlg();
            } else {
                throw new RuntimeException("Algorithm ID required but not found in COSE header");
            }
            status = "Checking Cose headers for required Key ID (kid)";
            if (pheader.getKid() != null) {
                keyId = pheader.getKid();
            } else if (uheader.getKid() != null) {
                keyId = uheader.getKid();
            } else {
               // throw new RuntimeException("Key ID required but not found in COSE header");
                System.out.println("Warning: Key ID not found in COSE header");
            }
            status = "retrieving signature from COSE object";
            signature = signOne.getSignature().getValue();
            status = "Retrieving payload from COSE object";
            payload = signOne.getPayload().encode();
            status = "Retrieving protected and unprotected header from COSE object";
            SigStructure ssb = new SigStructureBuilder().sign1(signOne).build();
            return ssb.encode();
        } catch (IOException e) {
            throw new RuntimeException("Error: " + process+status+" :" + e.getMessage());
        } catch (COSEException e) {
            throw new RuntimeException("Error: " + process+status+" :" + e.getMessage());
        }
    }

    public byte[] getToBeVerified (byte[] coseData, byte[] detachedPayload){

        coseBuilder = new COSESign1Builder();
        CBORDecoder cborDecoder = new CBORDecoder(coseData);
        COSEProtectedHeader pheader = null;
        COSESign1 sign1 = null;
        String process = "Processing toBeSigned for verification: ";
        String status = "Parsing Cose Tag, expecting tag 18 (cose-sign1):";

        CborTagProcessor ctp = new CborTagProcessor(coseData);
        int coseTag = ctp.getTagId();
        if (coseTag != CoseType.coseSign1) {
            throw new RuntimeException("Error parsing COSE signature: COSE tag of " + coseTag + " found but only cose-sign1 (18) is supported");
        }
        try {
            status = "Decoding COSE object";
            CBORItem coseObject= cborDecoder.next();
            //System.out.println( coseObject.prettify());
            ArrayList<Object> parsedata = (ArrayList) coseObject.parse();
            COSESign1 signOne = COSESign1.build(parsedata);
            status = "Decoding COSE Protected Header";
            pheader = signOne.getProtectedHeader();
            status = "Decoding COSE Unprotected Header";
            COSEUnprotectedHeader uheader = signOne.getUnprotectedHeader();
            status = "Checking Cose headers for required Algorithm Identifier";
            if (pheader.getAlg() != null) {
                Object algObject = (Object)pheader.getAlg();
                if (algObject instanceof String ) {  // library will return a String if algorithm is unknown
                    String sAlg = (String) pheader.getAlg();
                    if (sAlg.compareToIgnoreCase("unknown")==0) {
                        throw new RuntimeException("Unknown Algorithm Identifier found in COSE header");
                    }
                } else {
                    alg_id = (int) pheader.getAlg();
                }
            } else if (uheader.getAlg() != null) {
                alg_id = (int) uheader.getAlg();
            } else {
                throw new RuntimeException("Algorithm ID required but not found in COSE header");
            }
            status = "retrieving signature from COSE object";
            signature = signOne.getSignature().getValue();
            status = "Retrieving payload from COSE object";
            payload = signOne.getPayload().encode();
        } catch (COSEException | IOException e) {
            throw new RuntimeException(e);
        }
        // use the protected header from the signed structure and the supplied payload to create the toBeVerified data
        return finalizeToBeSigned(detachedPayload,pheader);
    }
    @Override
    /**
     *   Performs step 4 of  the  "How to compute a signature" section from:
     *      * https://datatracker.ietf.org/doc/html/rfc9052#section-4.4
     *
     *   4. Place the resulting signature value in the correct location.
     *      This is the "signature" field of the COSE_Signature or COSE_Sign1 structure.
     *
     * @param  signatureBytes data generated from step 3. Note step 3 is performed by a Cryptographic Engine
     */
    public void addSignature(byte[] signatureBytes) throws IOException {
        signature = signatureBytes.clone();
        coseBuilder.signature(signatureBytes);
    }

    @Override
    /**
     * Encodes the signature data an updates class variables.
     * @return byte array holding the singed data
     */
    public byte[] getSignedData( ) throws IOException {
        COSESign1 signatureData = coseBuilder.build();
        // Set local variables for future use

        protectedHeaders = signatureData.getProtectedHeader().getValue();
        CBORTaggedItem taggedCose = new CBORTaggedItem(CoseType.coseSign1, signatureData);
        return taggedCose.encode();
    }

    /**
     * Obtain the SHA-256 thumbprint of an X.509 certificate (used for embedding).
     *
     * @param cert The input X.509 certificate.
     * @return The SHA-256 thumbprint corresponding to the certificate.
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is unsupported
     * @throws CertificateEncodingException if the certificate cannot be encoded to DER
     */
    public static byte[] getThumbprint(X509Certificate cert) throws NoSuchAlgorithmException,
            CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(cert.getEncoded());
        return md.digest();
    }

    public void setNilPayload() {

        coseBuilder.payload(CBORNull.INSTANCE);
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

    public byte[] getKeyId() {
        return keyId == null ? null : keyId.clone();
    }

    public byte[] getProtectedHeaders() {
        return protectedHeaders == null ? null : protectedHeaders.clone();
    }

    public void setToBeSigned(byte[] toBeSigned) {
        this.toBeSigned = toBeSigned == null ? null : toBeSigned.clone();
    }

    public void setPayload(byte[] payload) {
        this.payload = payload == null ? null : payload.clone();
    }

    public void setSignature(byte[] signature) {
        this.signature = signature == null ? null : signature.clone();
    }

    public void setAlg_id(int alg_id) {
        this.alg_id = alg_id;
    }

    public void setKeyId(byte[] keyId) {
        this.keyId = keyId == null ? null : keyId.clone();
    }

    public void setProtectedHeaders(byte[] protectedHeaders) {
        this.protectedHeaders = protectedHeaders == null ? null : protectedHeaders.clone();
    }
}
