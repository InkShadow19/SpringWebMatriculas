package pe.villaesperanza.SpringWebMatriculas.dto.reference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoReference {

    ACTIVO(10),
    INACTIVO(20),
    UNDEFINED(-1);

    private final int value;

    public static EstadoReference fromInt(int value) {
        for (EstadoReference enumValue : EstadoReference.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        return UNDEFINED;
    }

}
