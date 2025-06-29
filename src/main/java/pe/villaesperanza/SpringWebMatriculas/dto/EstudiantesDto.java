package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;

import java.io.Serializable;
import java.time.Instant;
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
    private Instant fechaNacimiento;
    private GeneroReference genero;
    private String direccion;
    private String telefono;
    private String email;
    private boolean habilitado;
    private Instant fechaCreacion;

    private List<MatriculasDto> matriculas = new ArrayList<>();
}
