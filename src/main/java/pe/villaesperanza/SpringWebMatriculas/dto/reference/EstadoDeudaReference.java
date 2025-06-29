package pe.villaesperanza.SpringWebMatriculas.dto.reference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoDeudaReference {

    PENDIENTE(10),
    PAGADO(20),
    VENCIDO(30),
    ANULADO(40),
    UNDEFINED(-1);

    private final int value;

    public static EstadoDeudaReference fromInt(int value) {
        for (EstadoDeudaReference enumValue : EstadoDeudaReference.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        return UNDEFINED;
    }

}
