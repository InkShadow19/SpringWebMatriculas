package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.report.DistribucionGradoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.DistribucionNivelDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.IngresosVsDeudaDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.SituacionAlumnoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.TendenciaMatriculaDto;

import java.util.List;

@Getter
@Setter
@Builder
public class AnalisisAnualDto {
    private Double totalIngresosDelAnio;
    private List<TendenciaMatriculaDto> tendenciaMatriculas;
    private List<DistribucionNivelDto> distribucionAlumnosPorNivel;
    private List<IngresosVsDeudaDto> ingresosVsDeuda;
    private List<DistribucionGradoDto> distribucionAlumnosPorGrado;
    private List<SituacionAlumnoDto> distribucionSituacion;
}