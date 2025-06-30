package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GradosDto implements Serializable {

    private String identifier;
    private String descripcion;
    private boolean habilitado;
    private String fechaCreacion;

    private String nivel;
    private List<MatriculasDto> matriculas = new ArrayList<>();
}
