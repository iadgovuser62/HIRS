package hirs.utils.rim.unsignedRim.common.measurement;

import lombok.Getter;

/**
 * An enum that stores the list of measurement types.
 */
@Getter
public enum MeasurementType {
    UNKNOWN("Unknown"),
    PCCLIENT("PC Client"),
    DICE("DICE");

    private final String name;

    MeasurementType(String name) {
        this.name = name;
    }
}
