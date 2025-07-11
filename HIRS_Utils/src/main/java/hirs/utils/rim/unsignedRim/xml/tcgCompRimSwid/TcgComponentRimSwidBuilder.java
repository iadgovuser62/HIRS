package hirs.utils.rim.unsignedRim.xml.tcgCompRimSwid;

import hirs.utils.rim.unsignedRim.xml.pcclientrim.PcClientRimBuilder;
import hirs.utils.swid.SwidTagConstants;
import hirs.utils.xjc.SoftwareMeta;
import jakarta.json.JsonObject;

import javax.xml.namespace.QName;
import java.util.Map;

public class TcgComponentRimSwidBuilder extends PcClientRimBuilder {
    public static final String COMPONENT_MANUFACTURER_STR = "platformManufacturerStr";
    public static final String COMPONENT_MANUFACTURER_ID = "platformManufacturerId";
    public static final QName _COMPONENT_MANUFACTURER_STR = new QName("https://trustedcomputinggroup.org/wp-content/uploads/TCG_RIM_Model", "componentManufacturerStr", "rim");
    public static final QName _COMPONENT_MANUFACTURER_ID = new QName("https://trustedcomputinggroup.org/wp-content/uploads/TCG_RIM_Model", "componentManufacturerId", "rim");

    @Override
    protected SoftwareMeta createSoftwareMeta(JsonObject jsonObject) {
        SoftwareMeta softwareMeta = this.objectFactory.createSoftwareMeta();
        Map<QName, String> attributes = softwareMeta.getOtherAttributes();
        this.addNonNullAttribute(attributes, SwidTagConstants._COLLOQUIAL_VERSION, jsonObject.getString("colloquialVersion", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._EDITION, jsonObject.getString("edition", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._PRODUCT, jsonObject.getString("product", ""), true);
        this.addNonNullAttribute(attributes, SwidTagConstants._REVISION, jsonObject.getString("revision", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._PAYLOAD_TYPE, jsonObject.getString("PayloadType", ""));
        this.addNonNullAttribute(attributes, _COMPONENT_MANUFACTURER_STR, jsonObject.getString("componentManufacturerStr", "") , true);
        this.addNonNullAttribute(attributes, _COMPONENT_MANUFACTURER_ID, jsonObject.getString("componentManufacturerID", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._FIRMWARE_MANUFACTURER_STR, jsonObject.getString("firmwareManufacturerStr", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._FIRMWARE_MANUFACTURER_ID, jsonObject.getString("firmwareManufacturerId", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._FIRMWARE_MODEL, jsonObject.getString("firmwareModel", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._FIRMWARE_VERSION, jsonObject.getString("firmwareVersion", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._BINDING_SPEC, jsonObject.getString("bindingSpec", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._BINDING_SPEC_VERSION, jsonObject.getString("bindingSpecVersion", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._PC_URI_LOCAL, jsonObject.getString("pcURIlocal", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._PC_URI_GLOBAL, jsonObject.getString("pcURIGlobal", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._RIM_LINK_HASH, jsonObject.getString("rimLinkHash", ""));
        return softwareMeta;
    }
}
