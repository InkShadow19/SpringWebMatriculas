package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PagoDetallesDto implements Serializable {

    private String identifier;
    private Double montoAplicado;
    private boolean habilitado;
    private String fechaCreacion;

    private String cronograma;
    private String pago;
}
