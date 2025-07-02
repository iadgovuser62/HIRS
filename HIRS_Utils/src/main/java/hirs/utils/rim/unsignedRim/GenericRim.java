package hirs.utils.rim.unsignedRim;

import hirs.utils.rim.unsignedRim.common.measurement.Measurement;

import java.util.List;

public interface GenericRim {

    /**
     * RIM types used by the ACA as well as the rim-tool (rim-tool create, verify and print commands)
     *
     * Signed RIMs
     * SIGTYPE_COSE: IETF RFC 9052 defined CBOR Signatures (https://datatracker.ietf.org/doc/html/rfc9052)
     * SIGTYPE_DSIG: W3C Defined Signatures for XML (https://www.w3.org/TR/xmldsig-core1/)
     *
     * Unsigned RIM Types used for PC:
     *   RIMTYPE_PCRIM: TCG TCG Defined PC Client RIM which uses SWID
     *   RIMTYPE_TCG_PC_RIM_DSIG: TCG PC-RIM which uses SWID and has a DSIG signature
     *
     * Unsigned Types used for PC Components:
     *   RIMTYPE_COSWID: IETF RFC 9393 defined CoSWID (Concise SWID) tags
     *   RIMTYPE_COMP_SWID: TCG Component-RIM which uses SWID
     *   RIMTYPE_COMP_COSWID: TCG Component-RIM which uses CoSWID (Concise SWID)
     *   RIMTYPE_CORIM_COSWID: IETF CoRIM (Concise RIM) which envelopes a CoSWID (Concise SWID)
     *   RIMTYPE_CORIM_COMID: IETF CoRIM (Concise RIM) which envelopes a comid
     */
    // Types used for signature only; can sign any data (mainly for testing)
    //String SIGTYPE_COSE = "cose";

    final static String SIGTYPE_COSE = "cose";
    final static String SIGTYPE_DSIG = "dsig";

    final static String RIMTYPE_PCRIM = "pcrim";
    final static String RIMTYPE_COSWID = "coswid";
    final static String RIMTYPE_COMP_SWID = "comp_swid";
    final static String RIMTYPE_COMP_COSWID = "comp_coswid";
    final static String RIMTYPE_CORIM_COMID = "corim_comid";
    final static String RIMTYPE_CORIM_COSWID = "corim_coswid";

    /**
     * Returns a unique identifier String describing the type of RIM
     * @return the RIM type
     */
    public String getRimType();

    /**
     * Returns a unique identifier String (Manufacturer+Model in most cases)
     * or perhaps hash of a string to use as a DB lookup value for the RIMs Digests and the RIM itself.
     * @return the Rim ID
     */
    public String getRimID();

    /**
     * Retrieves the Signer info for the RIM.
     * @return String representing the SKID of the RIM Signer
     */
    public String getSignerId();

    /**
     * Runs checks on the rim to check validity
     * Should include signature checks, content checks, and formatting checks
     * requires a cert chain to verify the RIMs signature. SignerId would provide the reference for the ACA to look up the certs
     * @return true if valid, false if not
     */
    public boolean isValid();

    /**
     * Returns a list of Measurement objects for the given rim identifier that were found in the payload (if any).
     * @return List<Measurement> that holds the reference measurements
     */
    public List<Measurement> getReferenceMeasurements();

    /**
     * ReferencedRims is a list of RimId's references found in the payload (if any)
     */
    public String getReferencedRims();

    /**
     *  produces an object specific sting with info about the object.
     */
    public String toString();
}
