package pe.villaesperanza.SpringWebMatriculas.dto.reference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoPagoReference {
    
    CONFIRMADO(10),
    ANULADO(20),
    UNDEFINED(-1);

    private final int value;

    public static EstadoPagoReference fromInt(int value) {
        for (EstadoPagoReference enumValue : EstadoPagoReference.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        return UNDEFINED;
    }
}