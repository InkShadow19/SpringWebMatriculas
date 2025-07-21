package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UsuariosDto implements Serializable {

    private String identifier;
    private String usuario;
    private String contrasena;
    private String nombres;
    private String apellidos;   
    private String fechaNacimiento;
    private String dni;
    private EstadoReference estado;
    private String fechaCreacion;

    private String rol;
    private List<PagosDto> pagos = new ArrayList<>();
}
