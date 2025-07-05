package pe.villaesperanza.SpringWebMatriculas.dto.reference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoAcademicoReference {

    ACTIVO(10),
    CERRADO(20),
    FUTURO(30),
    RETIRADO(40),
    EGRESADO(50),
    UNDEFINED(-1);

    private final int value;

    public static EstadoAcademicoReference fromInt(int value) {
        for (EstadoAcademicoReference enumValue : EstadoAcademicoReference.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        return UNDEFINED;
    }

}
