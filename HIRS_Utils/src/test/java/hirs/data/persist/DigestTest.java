package hirs.data.persist;

import hirs.utils.digest.Digest;
import hirs.utils.digest.DigestAlgorithm;
import hirs.utils.digest.DigestComparisonResultType;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Unit tests for the <code>Digest</code> class.
 */
public class DigestTest {
    private static final int DIGEST_LENGTH_BYTES = 20;

    /**
     * Get a test SHA1 digest.
     *
     * @return a test SHA1 digest
     */
    public static Digest getTestSHA1Digest() {
        return new Digest(DigestAlgorithm.SHA1, getTestDigest(DIGEST_LENGTH_BYTES));
    }

    /**
     * Get a test SHA1 digest filled with the given byte.
     *
     * @param fill the byte that will be used to fill the digest
     * @return a test SHA1 digest with repeated entries of the given byte
     */
    public static Digest getTestSHA1Digest(final byte fill) {
        return new Digest(DigestAlgorithm.SHA1, getTestDigest(DIGEST_LENGTH_BYTES, fill));
    }

    private static byte[] getTestDigest(final int count) {
        return getTestDigest(count, (byte) 1);
    }

    private static byte[] getTestDigest(final int count, final byte fill) {
        final byte[] ret = new byte[count];
        Arrays.fill(ret, fill);
        return ret;
    }

    private static byte[] getZeroValueDigest(final int count) {
        return new byte[count];
    }

    /**
     * Tests that constructor throws a <code>IllegalArgumentException</code> when a
     * null <code>DigestAlgorithm</code> is passed into constructor.
     */
    @Test
    public final void nullAlgorithm() {
        final int count = 16;
        final byte[] digest = getTestDigest(count);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Digest(null, digest));
    }

    /**
     * Tests that constructor throws a <code>IllegalArgumentException</code> when a
     * null digest is passed into constructor.
     */
    @Test
    public final void nullDigest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Digest(DigestAlgorithm.MD2, null));
    }

    /**
     * Tests that constructor throws a <code>IllegalArgumentException</code> when an
     * digest that is an empty array is passed into constructor.
     */
    @Test
    public final void emptyArrayDigest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Digest(DigestAlgorithm.MD2, new byte[0]));
    }

    /**
     * Tests that MD2 digest can be created.
     */
    @Test
    public final void md2() {
        final int count = 16;
        final byte[] digest = getTestDigest(count);
        final Digest d = new Digest(DigestAlgorithm.MD2, digest);
        Assertions.assertNotNull(d);
    }

    /**
     * Tests that an MD2 digest can be recreated from a string.
     */
    @Test
    public final void testFromStringMD2() {
        final int count = 16;
        final byte[] digestBytes = getTestDigest(count);
        Digest digest = new Digest(DigestAlgorithm.MD2, digestBytes);
        String digestString = digest.toString();
        Digest digestFromString = Digest.fromString(digestString);
        Assertions.assertEquals(digest, digestFromString);
    }

    /**
     * Tests that MD2 digest cannot be created with a digest that has extra
     * bytes.
     */
    @Test
    public final void md2IllegalDigest() {
        final int count = 17;
        final byte[] digest = getTestDigest(count);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Digest(DigestAlgorithm.MD2, digest));
    }

    /**
     * Tests that MD5 digest can be created.
     */
    @Test
    public final void md5() {
        final int count = 16;
        final byte[] digest = getTestDigest(count);
        final Digest d = new Digest(DigestAlgorithm.MD5, digest);
        Assertions.assertNotNull(d);
    }

    /**
     * Tests that an MD5 digest can be recreated from a string.
     */
    @Test
    public final void testFromStringMD5() {
        final int count = 16;
        final byte[] digestBytes = getTestDigest(count);
        Digest digest = new Digest(DigestAlgorithm.MD5, digestBytes);
        String digestString = digest.toString();
        Digest digestFromString = Digest.fromString(digestString);
        Assertions.assertEquals(digest, digestFromString);
    }

    /**
     * Tests that MD5 digest cannot be created with a digest that has extra
     * bytes.
     */
    @Test
    public final void md5IllegalDigest() {
        final int count = 17;
        final byte[] digest = getTestDigest(count);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Digest(DigestAlgorithm.MD5, digest));
    }

    /**
     * Tests that SHA1 digest can be created.
     */
    @Test
    public final void sha1() {
        final int count = 20;
        final byte[] digest = getTestDigest(count);
        final Digest d = new Digest(DigestAlgorithm.SHA1, digest);
        Assertions.assertNotNull(d);
    }

    /**
     * Tests that SHA1 digest can be recreated from a string.
     */
    @Test
    public final void testFromStringSHA1() {
        final int count = 20;
        final byte[] digestBytes = getTestDigest(count);
        Digest digest = new Digest(DigestAlgorithm.SHA1, digestBytes);
        String digestString = digest.toString();
        Digest digestFromString = Digest.fromString(digestString);
        Assertions.assertEquals(digest, digestFromString);
    }

    /**
     * Tests that SHA1 digest cannot be created with a digest that has extra
     * bytes.
     */
    @Test
    public final void sha1IllegalDigest() {
        final int count = 21;
        final byte[] digest = getTestDigest(count);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Digest(DigestAlgorithm.SHA1, digest));
    }

    /**
     * Tests that SHA256 digest can be created.
     */
    @Test
    public final void sha256() {
        final int count = 32;
        final byte[] digest = getTestDigest(count);
        final Digest d = new Digest(DigestAlgorithm.SHA256, digest);
        Assertions.assertNotNull(d);
    }

    /**
     * Tests that SHA256 digest can be recreated from a string.
     */
    @Test
    public final void testFromStringSHA256() {
        final int count = 32;
        final byte[] digestBytes = getTestDigest(count);
        Digest digest = new Digest(DigestAlgorithm.SHA256, digestBytes);
        String digestString = digest.toString();
        Digest digestFromString = Digest.fromString(digestString);
        Assertions.assertEquals(digest, digestFromString);
    }

    /**
     * Tests that SHA256 digest cannot be created with a digest that has extra
     * bytes.
     */
    @Test
    public final void sha256IllegalDigest() {
        final int count = 33;
        final byte[] digest = getTestDigest(count);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Digest(DigestAlgorithm.SHA256, digest));
    }

    /**
     * Tests that SHA384 digest can be created.
     */
    @Test
    public final void sha384() {
        final int count = 48;
        final byte[] digest = getTestDigest(count);
        final Digest d = new Digest(DigestAlgorithm.SHA384, digest);
        Assertions.assertNotNull(d);
    }

    /**
     * Tests that SHA384 digest can be recreated from a string.
     */
    @Test
    public final void testFromStringSHA384() {
        final int count = 48;
        final byte[] digestBytes = getTestDigest(count);
        Digest digest = new Digest(DigestAlgorithm.SHA384, digestBytes);
        String digestString = digest.toString();
        Digest digestFromString = Digest.fromString(digestString);
        Assertions.assertEquals(digest, digestFromString);
    }

    /**
     * Tests that SHA384 digest cannot be created with a digest that has extra
     * bytes.
     */
    @Test
    public final void sha384IllegalDigest() {
        final int count = 49;
        final byte[] digest = getTestDigest(count);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Digest(DigestAlgorithm.SHA384, digest));
    }

    /**
     * Tests that SHA512 digest can be created.
     */
    @Test
    public final void sha512() {
        final int count = 64;
        final byte[] digest = getTestDigest(count);
        final Digest d = new Digest(DigestAlgorithm.SHA512, digest);
        Assertions.assertNotNull(d);
    }

    /**
     * Tests that SHA512 digest can be recreated from a string.
     */
    @Test
    public final void testFromStringSHA512() {
        final int count = 64;
        final byte[] digestBytes = getTestDigest(count);
        Digest digest = new Digest(DigestAlgorithm.SHA512, digestBytes);
        String digestString = digest.toString();
        Digest digestFromString = Digest.fromString(digestString);
        Assertions.assertEquals(digest, digestFromString);
    }

    /**
     * Tests that SHA512 digest cannot be created with a digest that has extra
     * bytes.
     */
    @Test
    public final void sha512IllegalDigest() {
        final int count = 65;
        final byte[] digest = getTestDigest(count);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Digest(DigestAlgorithm.SHA512, digest));
    }

    /**
     * Tests that the correct <code>DigestAlgorithm</code> is returned by
     * {@link Digest#getAlgorithm()}.
     */
    @Test
    public final void testGetAlgorithm() {
        final int count = 20;
        final Digest d = new Digest(DigestAlgorithm.SHA1, getTestDigest(count));
        Assertions.assertEquals(d.getAlgorithm(), DigestAlgorithm.SHA1);
    }

    /**
     * Tests that the bytes of the digest are created and do not affect the
     * underlying state of the <code>Digest</code> instance.
     */
    @Test
    public final void testGetDigest() {
        final int count = 20;
        final Digest d = new Digest(DigestAlgorithm.SHA1, getTestDigest(count));
        final byte[] digestBytes = d.getDigest();
        final byte[] testBytes = getTestDigest(count);
        Assertions.assertArrayEquals(digestBytes, testBytes);
        digestBytes[0] = (byte) (digestBytes[0] + 1);
        Assertions.assertArrayEquals(d.getDigest(), testBytes);
        Assertions.assertFalse(Arrays.equals(d.getDigest(), digestBytes));
    }

    /**
     * Tests that two <code>Digest</code>s have equal hash code for same
     * algorithm and digest.
     */
    @Test
    public final void testHashCodeEqual() {
        final int count = 20;
        final Digest d1 = new Digest(DigestAlgorithm.SHA1, getTestDigest(count));
        final Digest d2 = new Digest(DigestAlgorithm.SHA1, getTestDigest(count));
        Assertions.assertEquals(d2.hashCode(), d1.hashCode());
    }

    /**
     * Tests that two <code>Digest</code>s indicate MATCH when compared.
     */
    @Test
    public final void testMatchedComparison() {
        final int count = 20;
        final Digest d1 = new Digest(DigestAlgorithm.SHA1, getTestDigest(count));
        final Digest d2 = new Digest(DigestAlgorithm.SHA1, getTestDigest(count));
        Assertions.assertEquals(DigestComparisonResultType.MATCH, d1.compare(d2));
        Assertions.assertEquals(DigestComparisonResultType.MATCH, d2.compare(d1));
    }

    /**
     * Tests that two <code>Digest</code>s have unequal hash code for same
     * digest but different algorithm.
     */
    @Test
    public final void testHashCodeNotEqualAlgorithm() {
        final int count = 16;
        final Digest d1 = new Digest(DigestAlgorithm.MD2, getTestDigest(count));
        final Digest d2 = new Digest(DigestAlgorithm.MD5, getTestDigest(count));
        Assertions.assertNotEquals(d2.hashCode(), d1.hashCode());
    }

    /**
     * Tests that two <code>Digest</code>s indicate MISMATCH when compared.
     */
    @Test
    public final void testMismatchAlgorithm() {
        final int count = 16;
        final Digest d1 = new Digest(DigestAlgorithm.MD2, getTestDigest(count));
        final Digest d2 = new Digest(DigestAlgorithm.MD5, getTestDigest(count));
        Assertions.assertEquals(DigestComparisonResultType.MISMATCH, d1.compare(d2));
        Assertions.assertEquals(DigestComparisonResultType.MISMATCH, d2.compare(d1));
    }

    /**
     * Tests that two <code>Digest</code>s have unequal hash code for same
     * algorithm but different digest.
     */
    @Test
    public final void testHashCodeNotEqualDigest() {
        final int count = 20;
        final byte[] digest = getTestDigest(count);
        final Digest d1 = new Digest(DigestAlgorithm.SHA1, digest);
        digest[0] += 1;
        final Digest d2 = new Digest(DigestAlgorithm.SHA1, digest);
        Assertions.assertNotEquals(d2.hashCode(), d1.hashCode());
        Assertions.assertEquals(DigestComparisonResultType.MISMATCH, d1.compare(d2));
    }

    /**
     * Tests that two <code>Digest</code>s are equal for same algorithm and
     * digest.
     */
    @Test
    public final void testEqual() {
        final int count = 20;
        final Digest d1 = new Digest(DigestAlgorithm.SHA1, getTestDigest(count));
        final Digest d2 = new Digest(DigestAlgorithm.SHA1, getTestDigest(count));
        Assertions.assertEquals(d2, d1);
    }

    /**
     * Tests that two <code>Digest</code>s are unequal for same digest but
     * different algorithm.
     */
    @Test
    public final void testNotEqualAlgorithm() {
        final int count = 16;
        final Digest d1 = new Digest(DigestAlgorithm.MD2, getTestDigest(count));
        final Digest d2 = new Digest(DigestAlgorithm.MD5, getTestDigest(count));
        Assertions.assertNotEquals(d2, d1);
    }

    /**
     * Tests that two <code>Digest</code>s are unequal for same algorithm but
     * different digest.
     */
    @Test
    public final void testNotEqualDigest() {
        final int count = 20;
        final byte[] digest = getTestDigest(count);
        final Digest d1 = new Digest(DigestAlgorithm.SHA1, digest);
        digest[0] += 1;
        final Digest d2 = new Digest(DigestAlgorithm.SHA1, digest);
        Assertions.assertNotEquals(d2, d1);
    }

    /**
     * Tests that comparing a null Digest to a Digest indicates an UNKNOWN
     * comparison type.
     */
    @Test
    public final void testCompareToNull() {
        final int count = 16;
        final Digest d1 = new Digest(DigestAlgorithm.MD2, getTestDigest(count));
        Assertions.assertEquals(DigestComparisonResultType.UNKNOWN, d1.compare(null));
    }

    /**
     * Tests that comparing two Digests with hashes with values of zero gives a MATCH
     * comparison result.
     */
    @Test
    public final void testCompareToDigestWithBothZeroizedHash() {
        final int count = 20;
        final Digest d1 = new Digest(DigestAlgorithm.SHA1, getZeroValueDigest(count));
        final Digest d2 = new Digest(DigestAlgorithm.SHA1, getZeroValueDigest(count));
        Assertions.assertEquals(DigestComparisonResultType.MATCH, d1.compare(d2));
        Assertions.assertEquals(DigestComparisonResultType.MATCH, d2.compare(d1));
    }

    /**
     * Tests that comparing two Digests, one with a hash of value zero, gives a MISMATCH
     * comparison result.
     */
    @Test
    public final void testCompareToDigestWithOneZeroizedHash() {
        final int count = 20;
        final Digest d1 = new Digest(DigestAlgorithm.SHA1, getTestDigest(count));
        final Digest d2 = new Digest(DigestAlgorithm.SHA1, getZeroValueDigest(count));
        Assertions.assertEquals(DigestComparisonResultType.MISMATCH, d1.compare(d2));
        Assertions.assertEquals(DigestComparisonResultType.MISMATCH, d2.compare(d1));
    }

    /**
     * Tests that comparing two Digests with a hash of no data gives a MATCH
     * comparison result.
     */
    @Test
    public final void testCompareToDigestWithBothEmptyHash() {
        final Digest d1 = new Digest(DigestAlgorithm.SHA1, getEmptySHA1Digest());
        final Digest d2 = new Digest(DigestAlgorithm.SHA1, getEmptySHA1Digest());
        Assertions.assertEquals(DigestComparisonResultType.MATCH, d1.compare(d2));
        Assertions.assertEquals(DigestComparisonResultType.MATCH, d2.compare(d1));
    }

    /**
     * Tests that comparing two Digests, one with a hash of no data, gives a MISMATCH
     * comparison result.
     */
    @Test
    public final void testCompareToDigestWithOneEmptyHash() {
        final int count = 20;
        final Digest d1 = new Digest(DigestAlgorithm.SHA1, getTestDigest(count));
        final Digest d2 = new Digest(DigestAlgorithm.SHA1, getEmptySHA1Digest());
        Assertions.assertEquals(DigestComparisonResultType.MISMATCH, d1.compare(d2));
        Assertions.assertEquals(DigestComparisonResultType.MISMATCH, d2.compare(d1));
    }

    /**
     * Tests that if someone tries to recreate a Digest using an invalid String, an error is thrown.
     */
    @Test
    public final void testFromStringInvalid() {
        String invalidDigestString = "SHA1 00000000000000000000";
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Digest.fromString(invalidDigestString));
    }

    private byte[] getEmptySHA1Digest() {
        return DigestUtils.sha1(new byte[] {});
    }
}
