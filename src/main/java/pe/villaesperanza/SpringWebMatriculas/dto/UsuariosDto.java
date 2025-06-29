package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UsuariosDto implements Serializable {

    private String identifier;
    private String usuario;
    private String contraseña;
    private String nombres;
    private String apellidos;
    private boolean habilitado;
    private Instant fechaCreacion;

    private String rol;
    private List<PagosDto> pagos = new ArrayList<>();
}
