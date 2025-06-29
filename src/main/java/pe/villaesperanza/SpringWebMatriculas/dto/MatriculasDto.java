package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.SituacionReference;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class MatriculasDto implements Serializable {

    private String identifier;
    private Integer codigo;
    private SituacionReference situacion;
    private Instant fechaMatricula;
    private boolean habilitado;
    private Instant fechaCreacion;
}
