package hirs.utils.crypto;

import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

/**
 * Class to translate algorithm identifiers whose names differ between the
 * following specs: TCG (TCG Algorithm Registry):
 * https://trustedcomputinggroup.org/resource/tcg-algorithm-registry/ Table 3 —
 * Definition of (UINT16) TPM_ALG_ID Constants XML (XML Security Algorithm
 * Cross-Reference): https://www.w3.org/TR/xmlsec-algorithms/ RFC6931
 * (Additional XML Security Uniform Resource Identifiers (URIs)):
 * https://datatracker.ietf.org/doc/rfc6931/ CoSwid (Named Information Hash
 * Algorithm Registry): from https://www.rfc-editor.org/rfc/rfc9393.html, points
 * to:
 * https://www.iana.org/assignments/named-information/named-information.xhtml
 * COSE (COSE Header Algorithm Parameters):
 * https://www.iana.org/assignments/cose/cose.xhtml#algorithms Notes: * This
 * class only includes algorithms that pertain to asymmetric signature
 * algorithms and hash algorithms. * This class only includes algorithms that
 * are listed in the TCG Registry, since RIMs are defined by the TCG and the
 * spec states that the RIM MUST use a TCG-defined algorithm.
 */
public final class AlgorithmsIds {

    private AlgorithmsIds() {
    }

    /** Hash Algorithm type. */
    public static final String ALG_TYPE_HASH = "hash";

    /** Signature Algorithm type. */
    public static final String ALG_TYPE_SIG = "signature";

    /** Algorithm from TCG Spec. */
    public static final int SPEC_TCG_ALG = 0;

    /** Algorithm from XML Spec. */
    public static final int SPEC_XML_ALG = 1;

    /** Algorithm from CoSwid Spec. */
    public static final int SPEC_COSWID_ALG = 2;

    /** Algorithm from COSE Spec. */
    public static final int SPEC_COSE_ALG = 3;

    /** Algorithm from X.509 Spec. */
    public static final int SPEC_X509_ALG = 4;

    /** String description column. */
    public static final int STR_DESC_COL = 5;

    /** Algorithm family (e.g. ECC, RSA). */
    public static final int FAMILY_ALG = 6;

    /**
     * Array that holds the human-readable name of the spec, in the same column
     * order as the subsequent tables.
     */
    private static final String[][] ALG_TABLES_SPEC_COLUMNS = {
            // Specification order: TCG, XML, CoSwid, COSE, X509
            {"TCG", "XML", "CoSwid", "COSE", "X509"},
            {"TCG Algorithm Registry", "XML Security Algorithm Cross-Reference",
                    "Named Information Hash Algorithm Registry",
                    "COSE Header Algorithm Parameters",
                    "Java X509Certificate algorithm identifiers"}
    };

    /** Array that holds the hash alg names used in each of 4 specifications. */
    private static final String[][] HASH_ALGORITHMS = {
            // Specification order: TCG, XML, CoSwid, COSE, X509 string description
            {"TPM_ALG_SHA1", "SHA-1", "", "SHA-1", "SHA1", "SHA1"},
            {"TPM_ALG_SHA256", "SHA-256", "sha-256", "SHA-256", "SHA256", "SHA256"},
            {"TPM_ALG_SHA384", "SHA-384", "sha-384", "SHA-384", "SHA384", "SHA384"},
            {"TPM_ALG_SHA512", "SHA-512", "sha-512", "SHA-512", "SHA512", "SHA512"},
            {"TPM_ALG_SHA3_256", "", "sha3-256", "", "SHA3-256", "SHA3256"},
            {"TPM_ALG_SHA3_384", "", "sha3-384", "", "SHA3-384", "SHA384"},
            {"TPM_ALG_SHA3_512", "", "sha3-512", "SHA3-512", "SHA512"}
    };

    /** Array that holds the signing alg names used in each of 4 specifications. */
    private static final String[][] SIG_ALGORITHMS = {
            // RFC8017: Signature scheme for RSASSA-PSS and RSASSA-PKCS1-v1_5, Sections 8.1
            // and 8.2
            // https://datatracker.ietf.org/doc/html/rfc8017
            // https://www.rfc-editor.org/rfc/rfc8017
            // TPM
            // TPM_ALG_RSASSA:
            // RSASSA-PKCS1-v1_5 combines RSASP1 and RSAVP1 primitives w/ EMSA-PKCS1-v1_5
            // encoding
            // method
            // TPM_ALG_RSAPSS:
            // RSASSA-PSS combines RSASP1 and RSAVP1 primitives w/ EMSA-PSS encoding method
            // XML RSA-SHA256, RSA-SHA384, RSA-SHA512:
            // This implies the PKCS#1 v1.5 padding algorithm [RFC3447]
            // XML doesn't account for PSS padding so these will be blank
            // COSWID
            // COSWID has only hash algorithms so there's no column for this
            //
            // COSE
            // RS256: RSASSA-PKCS1-v1_5 using SHA-256
            // PS256: RSASSA-PSS w/ SHA-256

            // Specification order: TCG assymmetric & TCG hash, XML, CoSwid, COSE, X509
            // string descriptions,
            // Signature family(Ecc vd Rsa)
            // Note: TCG does not combine assymmetric & hash into one name for a signing
            // algorithm,
            // so table combines these 2 names with a * (in the first columnn)
            // CoSwid empty column left in for column # alignment consistent with the other
            // tables
            {"TPM_ALG_ECDSA*TPM_ALG_SHA256", "ECDSA-SHA256", "", "ES256", "ECDSA and SHA256",
                    "SHA256withECDSA", "ECC"},
            {"TPM_ALG_ECDSA*TPM_ALG_SHA384", "ECDSA-SHA384", "", "ES384", "ECDSA and SHA384",
                    "SHA384withECDSA", "ECC"},
            {"TPM_ALG_ECDSA*TPM_ALG_SHA512", "ECDSA-SHA512", "", "ES512", "ECDSA and SHA512",
                    "SHA512withECDSA", "ECC"},
            {"TPM_ALG_RSASSA*TPM_ALG_SHA256", "RSA-SHA256", "", "RS256",
                    "RSA with PKCS1-v1_5 padding and SHA256",
                    "SHA256withRSA", "RSA"},
            {"TPM_ALG_RSASSA*TPM_ALG_SHA384", "RSA-SHA384", "", "RS384",
                    "RSA with PKCS1-v1_5 padding and SHA384",
                    "SHA384withRSA", "RSA"},
            {"TPM_ALG_RSASSA*TPM_ALG_SHA512", "RSA-SHA512", "", "RS512",
                    "RSA with PKCS1-v1_5 padding and SHA512",
                    "SHA512withRSA", "RSA"},
            {"TPM_ALG_RSAPSS*TPM_ALG_SHA256", "", "", "PS256",
                    "RSA with PSS padding and SHA256", "TBD", "RSA"},
            {"TPM_ALG_RSAPSS*TPM_ALG_SHA384", "", "", "PS384",
                    "RSA with PSS padding and SHA384", "TBD", "RSA"},
            {"TPM_ALG_RSAPSS*TPM_ALG_SHA512", "", "", "PS512",
                    "RSA with PSS padding and SHA512", "TBD", "RSA"}
    };

    /**
     * Searches algorithm array for match to original spec's alg string, translates
     * that to desired spec alg name.
     *
     * @param algType
     *            type of algorithm (hash, signature)
     * @param originalSpec
     *            int id of specification for original algorithm
     * @param originalAlg
     *            string id of original algorithm
     * @param newSpec
     *            int id of specification for new algorithm
     * @return Name of new algorithm ID
     */
    public static String translateAlgId(final String algType, final int originalSpec,
                                        final String originalAlg, final int newSpec)
            throws NoSuchAlgorithmException {

        String newAlgId = "";

        if ((newSpec != SPEC_TCG_ALG) && (newSpec != SPEC_XML_ALG) && (newSpec != SPEC_COSWID_ALG)
                && (newSpec != SPEC_COSE_ALG) && (newSpec != SPEC_X509_ALG)) {
            throw new IllegalArgumentException("Invalid new spec");
        }

        int algIdRow = findAlgId(algType, originalSpec, originalAlg);
        if (algIdRow >= 0) {
            if (algType.compareTo(ALG_TYPE_HASH) == 0) {
                newAlgId = HASH_ALGORITHMS[algIdRow][newSpec];
            } else if (algType.compareTo(ALG_TYPE_SIG) == 0) {
                newAlgId = SIG_ALGORITHMS[algIdRow][newSpec];
            }

            if (newAlgId.compareTo("") == 0) {
                throw new NoSuchElementException(
                        "Algorithm " + algType + " from " + ALG_TABLES_SPEC_COLUMNS[0][originalSpec]
                                + " spec is not defined in " + ALG_TABLES_SPEC_COLUMNS[0][newSpec]
                                + " spec.");
            }
        }

        return newAlgId;
    }

    /**
     * Searches algorithm array for match to spec's alg string, returns true if
     * found.
     *
     * @param algType
     *            type of algorithm (hash, signature)
     * @param spec
     *            int id of specification for algorithm
     * @param alg
     *            string id of algorithm
     * @return true if alg found
     */
    public static boolean isValid(final String algType, final int spec, final String alg)
            throws NoSuchAlgorithmException {
        return findAlgId(algType, spec, alg) >= 0;
    }

    /**
     * Searches algorithm array for match to spec's alg string, returns row in array
     * where found.
     *
     * @param algType
     *            type of algorithm (hash, signature)
     * @param spec
     *            int id of specification for original algorithm
     * @param alg
     *            string id of algorithm
     * @return row in array if alg found, -1 if not found
     */
    private static int findAlgId(final String algType, final int spec, final String alg)
            throws NoSuchAlgorithmException {

        int index = -1;

        if ((spec != SPEC_TCG_ALG) && (spec != SPEC_XML_ALG) && (spec != SPEC_COSWID_ALG)
                && (spec != SPEC_COSE_ALG) && (spec != SPEC_X509_ALG)) {
            throw new IllegalArgumentException("Invalid original spec");
        }

        if (algType.compareTo(ALG_TYPE_HASH) == 0) {
            for (int i = 0; i < HASH_ALGORITHMS.length; i++) {
                if (alg.compareTo(HASH_ALGORITHMS[i][spec]) == 0) {
                    index = i;
                }
            }
        } else if (algType.compareTo(ALG_TYPE_SIG) == 0) {
            if (spec == SPEC_COSWID_ALG) {
                throw new NoSuchElementException("There is no COSWID signing algorithm");
            }
            for (int i = 0; i < SIG_ALGORITHMS.length; i++) {
                if (alg.compareTo(SIG_ALGORITHMS[i][spec]) == 0) {
                    index = i;
                }
            }
        } else {
            throw new NoSuchAlgorithmException("Invalid algorithm type " + algType);
        }
        if (index == -1) {
            throw new NoSuchElementException(
                    "Algorithm " + algType + " is not defined in " + ALG_TABLES_SPEC_COLUMNS[0][spec]
                            + " spec");
        }
        return index;
    }

    /**
     * Returns string with name of specification for algorithm & name of algorithm.
     *
     * @param algType
     *            type of algorithm (hash, signature)
     * @param originalSpec
     *            int id of specification for original algorithm
     * @param originalAlg
     *            string id of original algorithm
     * @param newSpec
     *            int id of specification for new algorithm
     * @return human-readable name of spec and algorithm
     */
    public static String toString(final String algType, final int originalSpec, final String originalAlg,
                                  final int newSpec) throws NoSuchAlgorithmException {
        String newAlg = translateAlgId(algType, originalSpec, originalAlg, newSpec);

        return "Original specification: " + ALG_TABLES_SPEC_COLUMNS[0][originalSpec] + " ("
                + ALG_TABLES_SPEC_COLUMNS[1][originalSpec] + ")\nOriginal " + algType + " algorithm: "
                + originalAlg + "\nNew specification: " + ALG_TABLES_SPEC_COLUMNS[0][newSpec] + " ("
                + ALG_TABLES_SPEC_COLUMNS[1][newSpec] + ")\nNew " + algType + " algorithm: " + newAlg
                + "\nDescription of algorithm: " + STR_DESC_COL;
    }

    /**
     * Retrieves the algorithm family (e.g., "RSA", "ECC") for the specified signature algorithm
     * in the given specification.
     *
     * @param spec
     *            the specification index (e.g., TCG, XML, COSE)
     * @param alg
     *            the algorithm identifier (e.g., "ES256", "RSA-SHA256")
     * @return the algorithm family name
     * @throws NoSuchAlgorithmException if the algorithm type or specification is invalid
     */
    public static String algFamily(final int spec, final String alg) throws NoSuchAlgorithmException {
        int row = findAlgId(ALG_TYPE_SIG, spec, alg);
        return SIG_ALGORITHMS[row][FAMILY_ALG];
    }

    /**
     * Determines whether specified signature algorithm belongs to ECC (Elliptic Curve Cryptography) family.
     *
     * @param spec
     *            the specification index (e.g., TCG, XML, COSE)
     * @param alg
     *            the algorithm identifier (e.g., "ES256", "ECDSA-SHA256")
     * @return {@code true} if the algorithm is ECC-based; {@code false} otherwise
     * @throws NoSuchAlgorithmException if the algorithm type or specification is invalid
     */
    public static boolean isEcc(final int spec, final String alg) throws NoSuchAlgorithmException {
        return algFamily(spec, alg).compareToIgnoreCase("ECC") == 0;
    }

    /**
     * Determines whether the specified signature algorithm belongs to the RSA family.
     *
     * @param spec
     *            the specification index (e.g., TCG, XML, COSE)
     * @param alg
     *            the algorithm identifier (e.g., "RS256", "RSA-SHA256")
     * @return {@code true} if the algorithm is RSA-based; {@code false} otherwise
     * @throws NoSuchAlgorithmException if the algorithm type or specification is invalid
     */
    public static boolean isRsa(final int spec, final String alg) throws NoSuchAlgorithmException {
        return algFamily(spec, alg).compareToIgnoreCase("RSA") == 0;
    }
}
