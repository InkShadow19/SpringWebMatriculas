package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;

@Getter
@Setter
@Builder 
public class UserProfileDto {
    private String identifier;
    private String usuario;
    private String nombres;
    private String apellidos;
    private String fechaNacimiento;
    private GeneroReference genero;
    private String dni;
    private EstadoReference estado;
    private String rol;

    // --- CAMPOS ADICIONALES PARA ENRIQUECER EL PERFIL ---
    private long totalPagosRegistrados;
}
