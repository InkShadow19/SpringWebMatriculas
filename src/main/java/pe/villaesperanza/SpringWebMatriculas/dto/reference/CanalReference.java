package pe.villaesperanza.SpringWebMatriculas.dto.reference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CanalReference {

    BANCO(10),
    CAJA(20),
    UNDEFINED(-1);

    private final int value;

    public static CanalReference fromInt(int value) {
        for (CanalReference enumValue : CanalReference.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        return UNDEFINED;
    }

}
