package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IngresosVsDeudaDto {
    private String mes;
    private Double ingresos;
    private Double deudaVencida;
}