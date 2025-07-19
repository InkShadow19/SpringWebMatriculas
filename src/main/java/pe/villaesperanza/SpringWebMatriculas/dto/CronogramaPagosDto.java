package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoDeudaReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CronogramaPagosDto implements Serializable {

    private String identifier;
    private String descripcion;
    private Double montoOriginal;
    private Double descuento;
    private Double mora;
    private Double montoAPagar;
    private String fechaVencimiento;
    private EstadoDeudaReference estadoDeuda;
    private String fechaCreacion;
    private String concepto;
    private String matricula;
    
    private List<PagoDetallesDto> detalles = new ArrayList<>();
}
