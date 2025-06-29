package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class PagoDetallesDto implements Serializable {

    private String identifier;
    private Double montoAplicado;
    private boolean habilitado;
    private Instant fechaCreacion;

    private String cronograma;
    private String pago;
}
