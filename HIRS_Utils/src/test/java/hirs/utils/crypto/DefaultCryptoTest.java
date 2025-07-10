package hirs.utils.crypto;

import hirs.utils.crypto.DefaultCrypto;
import hirs.utils.signature.cose.CoseSignature;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the Default Crypto implementation.
 * Note that major methods for sign and verify are tested in system tests and are not tested here.
 * Other test covered in other testing is noted in the comments below.
 */
public class DefaultCryptoTest {

    /**
     *  Test the load of a JsonWebKey used for testing
     *  Uses the algid and kid tha the JsonWebkey provides to prove it was loaded and ready for use
     *  Note that pem key loads were tested in CoseSignatureTest.testAddSignature()
     */
    @Test
    public final void testLoadJsonWebTokenEccPrivateKey()  throws Exception {
        String keyFile = "src/test/resources/keys/signed-01.json.key";

        DefaultCrypto crypto = new DefaultCrypto();
        crypto.loadPrivateKey(keyFile, null , "");

        String alg = crypto.getAlgorithm();
        String kid = crypto.getKid();

        assertEquals("ES256", alg);
        assertEquals("11", kid);
    }

    /**
     * Test loading a PemPrivate key with cert option. Uses the getAlgorithm function to tst the key was loaded.
     * @throws Exception
     */
    @Test
    public final void testLoadPemPrivateKeyWithCert()  throws Exception {
        String keyFile = "src/test/resources/keys/COMP_OEM1_rim_signer_rsa_3k_sha384.key";
        String certFile = "certificates/COMP_OEM1_rim_signer_rsa_3k_sha384.pem";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream certStream = classLoader.getResourceAsStream(certFile);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certStream);

        DefaultCrypto crypto = new DefaultCrypto();
        crypto.loadPrivateKey(keyFile, cert, "");

        String alg = crypto.getAlgorithm();

        assertEquals("RS384", alg);

    }
    /**
     * Test loading a PemPrivate key with cert option. Uses the getAlgorithm function to tst the key was loaded.
     * @throws Exception
     */
    @Test
    public final void testLoadPemPrivateKeyWithAlg()  throws Exception {
        String keyFile = "src/test/resources/keys/COMP_OEM1_rim_signer_rsa_3k_sha384.key";
        String certFile = "certificates/COMP_OEM1_rim_signer_rsa_3k_sha384.pem";

        DefaultCrypto crypto = new DefaultCrypto();
        crypto.loadPrivateKey(keyFile, null, "RS384");

        String alg = crypto.getAlgorithm();

        assertEquals("RS384", alg);

    }

    /**
     * Tests the DerEncodeRawSignature() method.
     * This method removes der encoding introduced by java.security when signing data and converts it to a "raw" format used by COSE.
     * Note that ecc and rsa-pss use a random input to the signature process making it impossible to check against known data.
     * Only RSA PKCS1 can be tested here. Since an rsa3072 bit key is used the raw signature length is expected to be 384.
     */
    @Test
    public final void testRemoveDerFromSignature() throws Exception {
        String keyFile = "src/test/resources/keys/COMP_OEM1_rim_signer_rsa_3k_sha384.key", certFile = "certificates/COMP_OEM1_rim_signer_rsa_3k_sha384.pem";
        String contentFile="cose/sign-pass-cose-content.bin";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream contStream = classLoader.getResourceAsStream(contentFile);
        byte[] contData = contStream.readAllBytes();
        InputStream certStream = classLoader.getResourceAsStream(certFile);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certStream);

        DefaultCrypto crypto = new DefaultCrypto();
        crypto.loadPrivateKey(keyFile, cert, "");

        CoseSignature coseSig = new CoseSignature();
        byte[] toBeSigned = coseSig.createToBeSigned("ES256","11", contData, cert, false);

        byte[] signedData = crypto.sign(toBeSigned);

        assertEquals(384, signedData.length);

    }
}
