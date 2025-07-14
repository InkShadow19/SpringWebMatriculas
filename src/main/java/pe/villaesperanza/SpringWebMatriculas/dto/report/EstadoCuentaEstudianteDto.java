package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoDeudaReference;

@Getter
@Setter
public class EstadoCuentaEstudianteDto {

    private String descripcion;
    private Double montoOriginal;
    private Double descuento;
    private Double mora;
    private Double montoAPagar;
    private String fechaVencimiento;
    private EstadoDeudaReference estadoDeuda;
}
