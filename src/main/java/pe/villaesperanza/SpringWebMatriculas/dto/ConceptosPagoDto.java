package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ConceptosPagoDto implements Serializable {

    private String identifier;
    private String codigo;
    private String descripcion;
    private Double montoSugerido;
    private boolean habilitado;
    private String fechaCreacion;

    private List<CronogramaPagosDto> cronogramas = new ArrayList<>();
}
