package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

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
    private EstadoReference estado;
    private String fechaCreacion;

    private List<CronogramaPagosDto> cronogramas = new ArrayList<>();
}
