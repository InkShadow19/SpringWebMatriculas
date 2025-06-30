package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AñosAcademicosDto implements Serializable {

    private String identifier;
    private Integer anio;
    private EstadoAcademicoReference estado;
    private boolean habilitado;
    private String fechaCreacion;

    private List<MatriculasDto> matriculas = new ArrayList<>();
}
