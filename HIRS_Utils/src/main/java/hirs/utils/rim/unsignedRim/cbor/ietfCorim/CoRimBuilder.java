package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import com.authlete.cbor.CBORByteArray;
import com.authlete.cbor.CBORInteger;
import com.authlete.cbor.CBORItem;
import com.authlete.cbor.CBORItemList;
import com.authlete.cbor.CBORPair;
import com.authlete.cbor.CBORPairList;
import com.authlete.cbor.CBORString;
import com.authlete.cbor.CBORTaggedItem;
import com.authlete.cose.COSEProtectedHeader;
import com.authlete.cose.COSEUnprotectedHeaderBuilder;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import hirs.utils.crypto.DefaultCrypto;
import hirs.utils.signature.cose.CoseAlgorithm;
import hirs.utils.signature.cose.CoseSignature;
import org.bouncycastle.asn1.x509.Extension;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Contains the logic used to build out a CoRIM from user input.
 */
public class CoRimBuilder {
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    public static int TAGGED_UNSIGNED_CORIM_MAP = 501;

    /**
     * Builds a CoRIM object from a given configuration file.
     *
     * @param configFile The input file for the CoRIM.
     * @return The byte array containing the output CoRIM, in unsigned CBOR format.
     */
    public static byte[] build(String configFile) {
        ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        // Add date formatting
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        objectMapper.setDateFormat(df);
        // Add serializer for hexadecimal byte[] deserialization
        SimpleModule module = new SimpleModule();
        module.addDeserializer(byte[].class, new HexByteArrayDeserializer());
        objectMapper.registerModule(module);
        try {
            byte[] data = Files.readAllBytes(Paths.get(configFile));
            CoRimConfig corimConfig = objectMapper.readValue(data, CoRimConfig.class);
            return createCborFromCorim(corimConfig);
        }
        catch (Exception e) {
            System.out.println("Error building CoRIM from input file " + configFile + ": " );
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    /**
     * Create signed CoRIM from existing unsigned CoRIM object.
     *
     * @param unsignedCorim The original, unsigned CoRIM object.
     * @param keyPath The path of the private key used to sign the CoRIM.
     * @param certPath The path of the public certificate used for the CoRIM.
     * @param algName The IANA algorithm used for signing the CoRIM.
     * @param isEmbedded Set to true if embedding a signing certificate per RFC 9360.
     *
     * @return A signed CoRIM object, given the original CoRIM.
     */
    public static byte[] createSignedCorim(byte[] unsignedCorim, String keyPath,
                                           String certPath, String algName, boolean isEmbedded) {
        // Read certificate from file
        X509Certificate cert = null;
        try {
            FileInputStream is = new FileInputStream(certPath);
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) certFactory.generateCertificate(is);
        }
        catch (Exception e) {
            System.out.println("Error reading public certificate from input file " + certPath + ": " );
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // Load private key
        DefaultCrypto cryptoProvider = new DefaultCrypto();
        try {
            cryptoProvider.loadPrivateKey(keyPath, cert, algName);
            algName = cryptoProvider.getAlgorithm(); // Reassign algName such that it matches crypto provider
        } catch (Exception e) {
            System.out.println("Error reading private key from input file " + keyPath + ": " );
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // Create COSE-Sign1-corim structure
        ArrayList<CBORItem> coseSign1Items = new ArrayList<>();
        COSEProtectedHeader protectedHeader = null;
        // Add protected header
        try {
            protectedHeader = createProtectedCorimHeader(CoseAlgorithm.getAlgId(algName), cert,
                    isEmbedded);
        }
        catch (Exception e) {
            System.out.println("Error creating protected COSE header for CORIM." );
            System.out.println(e.getMessage());
            System.exit(1);
        }
        coseSign1Items.add(protectedHeader);
        // Add unprotected header
        // TODO: Currently blank
        coseSign1Items.add(new COSEUnprotectedHeaderBuilder().build());
        // Add payload
        coseSign1Items.add(new CBORByteArray(unsignedCorim));
        // Add signature
        // Create signature block (Sig_structure) and ToBeSigned
        try {
            byte[] toBeSigned = new CoseSignature().createToBeSigned(cert, unsignedCorim, protectedHeader);
            byte[] signature = cryptoProvider.sign(toBeSigned);
            coseSign1Items.add(new CBORByteArray(signature));
        } catch (Exception e) {
            System.out.println("Failed to sign CoRIM. Exiting." );
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return new CBORTaggedItem(18, new CBORItemList(coseSign1Items)).encode();
    }

    public static COSEProtectedHeader createProtectedCorimHeader(int alg, X509Certificate publicCert,
                                                                 boolean isEmbedded)
            throws CertificateEncodingException, NoSuchAlgorithmException {
        // Create protected-corim-header-map
        ArrayList<CBORPair> pchMapItems = new ArrayList<>();
        pchMapItems.add(new CBORPair(new CBORInteger(1), new CBORInteger(alg))); // alg
        pchMapItems.add(new CBORPair(new CBORInteger(3),
                new CBORString("application/rim+cbor"))); // content-type
        pchMapItems.add(new CBORPair(new CBORInteger(4),
                new CBORByteArray(publicCert.getExtensionValue(Extension.subjectKeyIdentifier.getId())))); // kid
        pchMapItems.add(new CBORPair(new CBORInteger(8),
                new CBORByteArray(createCorimMetaMap(publicCert).encode()))); // corim-meta
        // Embed per RFC 9360
        if (isEmbedded) {
            // Add x5chain
            pchMapItems.add(new CBORPair(new CBORInteger(33), new CBORByteArray(publicCert.getEncoded())));
            // Add x5t (thumbprint)
            List<CBORItem> x5tList = new ArrayList<>();
            x5tList.add(new CBORInteger(CoseAlgorithm.coseSha256)); // hashAlg
            x5tList.add(new CBORByteArray(CoseSignature.getThumbprint(publicCert))); // hashValue
            CBORItemList x5tItemList = new CBORItemList(x5tList);
            pchMapItems.add(new CBORPair(new CBORInteger(34), x5tItemList));
        }
        CBORPairList pchMapList = new CBORPairList(pchMapItems);
        return new COSEProtectedHeader(pchMapList.encode(), pchMapItems);
    }

    protected static CBORPairList createCorimMetaMap(X509Certificate publicCert) {
        // Create corim-signer-map first
        ArrayList<CBORPair> csmItems = new ArrayList<>();
        // TODO: Change? (Use specified org name or from public cert instead?)
        csmItems.add(new CBORPair(new CBORInteger(0),
                new CBORString("HIRS"))); // signer-name
        CBORPairList signerMap = new CBORPairList(csmItems);

        ArrayList<CBORPair> cmmItems = new ArrayList<>();
        cmmItems.add(new CBORPair(new CBORInteger(0), signerMap)); // corim-signer-map
        return new CBORPairList(cmmItems);
    }

    protected static byte[] createCborFromCorim(CoRimConfig corimConfig) {
        CBORItem unsignedCorimMap = corimConfig.build();
        // Wrap item for tagged-unsigned-corim-map
        CBORTaggedItem taggedUnsignedCorimMap = new CBORTaggedItem(TAGGED_UNSIGNED_CORIM_MAP, unsignedCorimMap);
        return taggedUnsignedCorimMap.encode();
    }
}
