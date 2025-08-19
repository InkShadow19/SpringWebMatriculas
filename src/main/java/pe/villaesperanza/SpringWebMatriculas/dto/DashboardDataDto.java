package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardDataDto {
    private long totalAlumnosMatriculadosAnioActivo;
    private double ingresosDelMesActual;
    private double totalDeudaPendienteGlobal;
    private long totalAlumnosMorososGlobal;
}