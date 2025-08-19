package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TendenciaMatriculaDto {
    private String mes;
    private Long total;
}