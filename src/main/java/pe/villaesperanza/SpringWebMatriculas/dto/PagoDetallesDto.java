package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.io.Serializable;

@Getter
@Setter
public class PagoDetallesDto implements Serializable {

    private String identifier;
    private Double montoAplicado;
    private EstadoReference estado;
    private String fechaCreacion;

    private String cronograma;
    private String pago;
}
