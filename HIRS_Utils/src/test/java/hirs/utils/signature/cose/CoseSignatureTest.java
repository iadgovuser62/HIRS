package hirs.utils.signature.cose;

import hirs.utils.crypto.DefaultCrypto;
import hirs.utils.signature.cose.CoseSignature;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Tests the toBeSigned() for COSE_SIGN1 structures used by TCG Component RIM
 * Uses data from https://github.com/cose-wg/Examples/tree/master/sign1-tests
 */
public class CoseSignatureTest {
    /**
     *  Tests the generation of "to be verified" data from a known good test pattern
     *  1. Read in a test Cose file into a byte array
     *  2. Read in a test (expected) toBeSigned data into a byte array
     *  3. Generate toBeVerified from Cose Builder
     *  4. Compare tobBeVerified from Cose Builder against expected values, throw an error if different
     */
    @Test
    public final void testToBeVerified() throws IOException {
        String coseFile = "cose/sign-pass-01.cose", toBeSignedFile="cose/cose-pass-01-toBeSigned.bin";

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream coseStream = classLoader.getResourceAsStream(coseFile);
        byte[] coseData = coseStream.readAllBytes();
        InputStream tbsStream = classLoader.getResourceAsStream(toBeSignedFile);
        byte[] tbsData = tbsStream.readAllBytes();

        CoseSignature coseSig = new CoseSignature();
        byte[] toBeSigned = coseSig.getToBeVerified(coseData);

        assertArrayEquals(tbsData,toBeSigned);
    }
    /**
     *  Tests the generation of "toBeSigned" data from a known good test pattern
     *  1. Read in a test Cose file into a byte array
     *  2. Read in a test (expected) toBeSigned data into a byte array
     *  3. Generate toBeSigned from Cose Builder
     *  4. Compare tobBeSigned from Cose Builder against expected values, throw an error if different
     */
    @Test
    public final void testToBeSigned() throws IOException, CertificateException, NoSuchAlgorithmException {
        String  toBeSignedFile="cose/cose-pass-03-toBeSigned.bin", contentFile="cose/sign-pass-cose-content.bin",certFile = "certificates/COMP_OEM1_rim_signer_rsa_3k_sha384.pem";;

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream tbsStream = classLoader.getResourceAsStream(toBeSignedFile);
        byte[] tbsData = tbsStream.readAllBytes();
        InputStream contStream = classLoader.getResourceAsStream(contentFile);
        byte[] contData = contStream.readAllBytes();
        InputStream certStream = classLoader.getResourceAsStream(certFile);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certStream);

        CoseSignature coseSig = new CoseSignature();
        byte[] toBeSigned = coseSig.createToBeSigned("ES256","11", contData, cert, false);

        assertArrayEquals(tbsData, toBeSigned);
    }

    /**
     *  Tests the getSignature from a known good test pattern
     *  1. Read in a test Cose file into a byte array
     *  2. Read in a test (expected) toBeSigned data into a byte array
     *  3. Generate toBeSigned from Cose Builder (parses the cose data)
     *  4. Compare getSignature() output from Cose Builder against expected values, throw an error if different
     */
    @Test
    public final void testGetSignatureData() throws IOException {
        String coseFile = "cose/sign-pass-02.cose", sigFile="cose/cose-pass-02-sig.bin";

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream coseStream = classLoader.getResourceAsStream(coseFile);
        byte[] coseData = coseStream.readAllBytes();
        InputStream sigStream = classLoader.getResourceAsStream(sigFile);
        byte[] sigData = sigStream.readAllBytes();

        CoseSignature coseSig = new CoseSignature();
        byte[] toBeVerified = coseSig.getToBeVerified(coseData);
        byte[] signature  =  coseSig.getSignature();

        assertArrayEquals(sigData, signature);
    }

    /**
     * Tests the addSignature() method.
     * A Cose Sign1 signature is created by signing tes data and comparing against a known good test pattern.
     * Note that ecc and rsa-pss use a random input to the signature process making it impossible to check against known data.
     * Only RSA PKCS1 can be tested here.
     */
    @Test
    public final void testAddSignature() throws Exception {
        String keyFile = "src/test/resources/keys/COMP_OEM1_rim_signer_rsa_3k_sha384.key", certFile = "certificates/COMP_OEM1_rim_signer_rsa_3k_sha384.pem";
        String contentFile="cose/sign-pass-cose-content.bin", signedFile= "cose/sign_pass_rsa_3072_sha384.cose";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream contStream = classLoader.getResourceAsStream(contentFile);
        byte[] contData = contStream.readAllBytes();
        InputStream certStream = classLoader.getResourceAsStream(certFile);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certStream);
        InputStream signedStream = classLoader.getResourceAsStream(signedFile);
        byte[] signedCoseData = signedStream.readAllBytes();

        DefaultCrypto crypto = new DefaultCrypto();
        crypto.loadPrivateKey(keyFile, cert, "");

        CoseSignature coseSig = new CoseSignature();
        byte[] toBeSigned = coseSig.createToBeSigned("ES256","11", contData, cert, false);

        byte[] signature = crypto.sign(toBeSigned);

        coseSig.addSignature(signature);
        byte[] signedRim = coseSig.getSignedData();

        assertArrayEquals(signedCoseData, signedRim);
    }

}
