package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RolesDto implements Serializable {

    private String identifier;
    private String descripcion;
    private EstadoReference estado;
    private String fechaCreacion;

    private List<UsuariosDto> usuarios = new ArrayList<>();
}
