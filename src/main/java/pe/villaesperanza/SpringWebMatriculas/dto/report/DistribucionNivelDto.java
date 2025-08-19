package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DistribucionNivelDto {
    private String nivel;
    private Long total;
}