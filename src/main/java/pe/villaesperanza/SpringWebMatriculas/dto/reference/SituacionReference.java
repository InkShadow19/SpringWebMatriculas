package pe.villaesperanza.SpringWebMatriculas.dto.reference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SituacionReference {

    PROMOVIDO(10),
    INGRESANTE(20),
    REPITENTE(30),
    UNDEFINED(-1);

    private final int value;

    public static SituacionReference fromInt(int value) {
        for (SituacionReference enumValue : SituacionReference.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        return UNDEFINED;
    }

}
