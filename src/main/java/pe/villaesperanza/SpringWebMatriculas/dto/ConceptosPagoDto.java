package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class ConceptosPagoDto implements Serializable {

    private String identifier;
    private String codigo;
    private String descripcion;
    private Double montoSugerido;
    private boolean habilitado;
    private Instant fechaCreacion;
}
