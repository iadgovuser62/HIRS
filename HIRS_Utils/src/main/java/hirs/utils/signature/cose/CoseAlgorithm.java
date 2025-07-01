package hirs.utils.signature.cose;

import hirs.utils.rim.unsignedRim.cbor.ietfCoswid.CoswidItems;

/**
 * Class to handle COSE algorithms specified on
 * https://www.iana.org/assignments/cose/cose.xhtml#algorithms As specified by the COSE
 * specification (rfc rfc8152) and constrained by the TCG Component Rim binding spec for CoSwid
 * Therefore only the Algorithm Combinations suited to signatures (TCG Asymmetric Sig Alg + TCG
 * Hash) are considered
 */
public class CoseAlgorithm {
  public static final int coseRsaSha512 = -259; // Uses PKCS-v1_5 padding
  public static final int coseRsaSha384 = -258;
  public static final int coseRsaSha256 = -257;
  public static final int coseEsSha512 = -36;
  public static final int coseEsSha384 = -35;
  public static final int coseEsSha256 = -7;
  public static final int coseRsaPss512 = -39;
  public static final int coseRsaPss384 = -38;
  public static final int coseRsaPss256 = -37;
  public static final int coseSha256 = -16;

  public static final String RSA_SHA512_PKCS1 = "RS512";
  public static final String RSA_SHA384_PKCS1 = "RS384";
  public static final String RSA_SHA256_PKCS1 = "RS256";
  public static final String RSA_SHA512_PSS = "PS512";
  public static final String RSA_SHA384_PSS = "PS384";
  public static final String RSA_SHA256_PSS = "PS256";
  public static final String ECDSA_SHA256 = "ES256";
  public static final String ECDSA_SHA384 = "ES384";
  public static final String ECDSA_SHA512 = "ES512";
  public static final String SHA256 = "SHA-256";

  private static final String[][] algNames = {
    {"-259", "RS512"}, // RSASSA-PKCS1-v1_5 using SHA-512
    {"-258", "RS384"}, // RSASSA-PKCS1-v1_5 using SHA-384
    {"-257", "RS256"}, // RSASSA-PKCS1-v1_5 using SHA-256
    {"-39", "PS512"}, // RSASSA-PSS w/ SHA-512
    {"-38", "PS384"}, // RSASSA-PSS w/ SHA-384
    {"-37", "PS256"}, // RSASSA-PSS w/ SHA-256
    {"-36", "ES512"}, // ECDSA w/ SHA-512
    {"-35", "ES384"}, // ECDSA w/ SHA-384
    {"-16", "SHA-256"}, // SHA-2 256-bit Hash
    {"-7", "ES256"} // ECDSA w/ SHA-256
  };

  /**
   * Searches Rfc 9393 Items Names for match to a specified item name and returns the index
   *
   * @param coseAlg Iem Name specified in rfc 8152
   * @return int tag of the cose type
   */
  public static int getAlgId(String coseAlg) {
    int algId = 0;
    for (int i = 0; i < algNames.length; i++) {
      if (coseAlg.compareToIgnoreCase(algNames[i][1]) == 0)
        return (Integer.parseInt(algNames[i][0]));
    }
    return CoswidItems.UNKNOWN_INT;
  }

  /**
   * Searches for an Rfc 8152 specified index and returns the item name associated with the index
   *
   * @param coseAlId IANA registered COSE Algorithm Value (ID)
   * @return String Algorithm name associated with the Algorithm Value (ID)
   */
  public static String getAlgName(int coseAlId) {
    int algId = 0;
    for (int i = 0; i < algNames.length; i++) {
      if (coseAlId == Integer.parseInt(algNames[i][0])) return algNames[i][1];
    }
    return CoswidItems.UNKNOWN_STR;
  }

  /**
   * Returns true of the specified COSE algorithm identifier is a supported algorithm from the ECDSA
   * family of algorithms
   *
   * @param cosAlId
   * @return
   */
  public static boolean isEcdsa(int cosAlId) {
    if ((cosAlId == CoseAlgorithm.coseEsSha256)
        || (cosAlId == CoseAlgorithm.coseEsSha384)
        || (cosAlId == CoseAlgorithm.coseEsSha512)) return true;
    else return false;
  }

  /**
   * Returns true of the specified COSE algorithm identifier is a supported algorithm from the ECDSA
   * family of algorithms
   *
   * @param coseAlgorithmName a IANA Registered COSE algorithm name
   * @return
   */
  public static boolean isEcdsaName(String coseAlgorithmName) {
    if ((coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.ECDSA_SHA256) == 0)
        || (coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.ECDSA_SHA384) == 0)
        || (coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.ECDSA_SHA512) == 0)) return true;
    else return false;
  }

  /**
   * Returns true of the specified COSE algorithm identifier is a supported algorithm from the RSA
   * family of algorithms
   *
   * @param cosAlId
   * @return
   */
  public static boolean isRsa(int cosAlId) {
    return cosAlId == CoseAlgorithm.coseRsaPss256
        || cosAlId == CoseAlgorithm.coseRsaPss384
        || cosAlId == CoseAlgorithm.coseRsaSha256;
  }

  /**
   * Returns true of the specified COSE algorithm identifier is a supported algorithm from the ECDSA
   * family of algorithms
   *
   * @param coseAlgorithmName a IANA Registered COSE algorithm name
   * @return
   */
  public static boolean isRsaName(String coseAlgorithmName) {
    if ((coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.RSA_SHA256_PKCS1) == 0)
        || (coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.RSA_SHA384_PKCS1) == 0)
        || (coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.RSA_SHA512_PKCS1) == 0)
        || (coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.RSA_SHA256_PSS) == 0)
        || (coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.RSA_SHA384_PSS) == 0)
        || (coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.RSA_SHA512_PSS) == 0)) return true;
    else return false;
  }

  public static boolean isRsaPssName(String coseAlgorithmName) {
    if ((coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.RSA_SHA256_PSS) == 0)
        || (coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.RSA_SHA384_PSS) == 0)
        || (coseAlgorithmName.compareToIgnoreCase(CoseAlgorithm.RSA_SHA512_PSS) == 0)) return true;
    else return false;
  }
}
