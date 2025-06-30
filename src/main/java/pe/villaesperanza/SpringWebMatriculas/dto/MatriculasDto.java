package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.SituacionReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MatriculasDto implements Serializable {

    private String identifier;
    private Integer codigo;
    private SituacionReference situacion;
    private String fechaMatricula;
    private boolean habilitado;
    private String fechaCreacion;

    private String nivel;
    private String grado;
    private String estudiante;
    private String apoderado;
    private String anioAcademico;
    private List<CronogramaPagosDto> cronogramas = new ArrayList<>();
}
