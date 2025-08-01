package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoMatriculaReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.SituacionReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MatriculasDto implements Serializable {

    private String identifier;
    private String codigo;
    private String procedencia;
    private SituacionReference situacion;
    private String fechaMatricula;
    private EstadoMatriculaReference estado;
    private String fechaCreacion;
    private String nivel;
    private String grado;
    private String estudiante;
    private String apoderado;
    private String anioAcademico;

    // --- CAMPOS AÑADIDOS PARA RECIBIR LOS DESCUENTOS ---
    private Double descuentoMatricula;
    private Double descuentoPension;

    // --- CAMPO AÑADIDO PARA LA VALIDACIÓN ---
    private EstadoAcademicoReference estadoAnioAcademico;

    private List<CronogramaPagosDto> cronogramas = new ArrayList<>();
}
