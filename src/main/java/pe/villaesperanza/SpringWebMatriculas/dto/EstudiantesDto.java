package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EstudiantesDto implements Serializable {

    private String identifier;
    private String dni;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String fechaNacimiento;
    private GeneroReference genero;
    private String direccion;
    private String telefono;
    private String email;
    private EstadoAcademicoReference estadoAcademico;
    private EstadoReference estado;
    private String fechaCreacion;

    private List<MatriculasDto> matriculas = new ArrayList<>();
}
