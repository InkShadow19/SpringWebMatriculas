package pe.villaesperanza.SpringWebMatriculas.dto.reference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GeneroReference {

    MASCULINO(10),
    FEMENINO(20),
    HELICOPTERO_APACHE(30),
    UNDEFINED(-1);

    private final int value;

    public static GeneroReference fromInt(int value) {
        for (GeneroReference enumValue : GeneroReference.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        return UNDEFINED;
    }

}
