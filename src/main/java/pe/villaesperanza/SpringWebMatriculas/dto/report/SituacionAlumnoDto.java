package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SituacionAlumnoDto {
    private String situacion;
    private Long total;
}