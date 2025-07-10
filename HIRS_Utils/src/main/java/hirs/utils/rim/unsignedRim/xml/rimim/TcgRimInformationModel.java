package hirs.utils.rim.unsignedRim.xml.rimim;

import hirs.utils.rim.unsignedRim.xml.Swid;
import lombok.Getter;
import lombok.Setter;

public class TcgRimInformationModel extends Swid {
    @Setter
    @Getter
    /** Spec used to define this tag */
    protected String rimBindingSpec = null;
    protected static final String RIM_BINDING_SPEC_STR = "bindingSpec";
    @Setter
    @Getter
    /** Version of the spec used to define this tag */
    protected String rimBindingSpecVersion = null;
    protected static final String  RIM_BINDING_SPEC_VERSION_STR = "bindingSpecVersion";
    @Setter
    @Getter
    /** Direct, indirect, or Composite */
    protected String rimPayloadType = null;
    protected static final String RIM_PAYLOAD_TYPE_STR = "payloadType";
    @Setter
    @Getter
    protected String rimPlatformManufacturer = null;
    protected static final String RIM_Platform_MANUFACTURER_STR = "platformManufacturer";
    @Setter
    @Getter
    /** Manufacturer of the target device (e.g. the component manufacturer) */
    protected String rimPlatformManufacturerID = null;
    protected static final String RIM_PLATFORM_MANUFACTURER_ID_STR = "platformManufacturerID";
    @Setter
    @Getter
    /** Model of the target device (e.g. the component model) */
    protected String rimPlatformModel = null;
    protected static final String RIM_PLATFORM_MODEL_STR = "platformModel";
}
