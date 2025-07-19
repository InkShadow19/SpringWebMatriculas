package pe.villaesperanza.SpringWebMatriculas.dto.reference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoMatriculaReference {
    
    VIGENTE(10),
    ANULADA(20),
    COMPLETADA(30),
    UNDEFINED(-1);

    private final int value;

    public static EstadoMatriculaReference fromInt(int value) {
        for (EstadoMatriculaReference enumValue : EstadoMatriculaReference.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        return UNDEFINED;
    }
}