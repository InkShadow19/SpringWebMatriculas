package pe.villaesperanza.SpringWebMatriculas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class UsuariosDto implements Serializable {

    private String identifier;
    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 4, max = 20, message = "El nombre de usuario debe tener entre 4 y 20 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "El nombre de usuario solo puede contener letras, números y guiones.")
    private String usuario;

    private String contrasena;

    @NotBlank(message = "Los nombres son obligatorios.")
    @Size(min = 2, max = 90, message = "Los nombres deben tener entre 2 y 90 caracteres.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Los nombres solo pueden contener letras y espacios.")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios.")
    @Size(min = 2, max = 90, message = "Los apellidos deben tener entre 2 y 90 caracteres.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Los apellidos solo pueden contener letras y espacios.")
    private String apellidos;

    @NotBlank(message = "La fecha de nacimiento es obligatoria.")
    private String fechaNacimiento;
    
    // Suponiendo que el genero es requerido al crear
    private GeneroReference genero; 

    @NotBlank(message = "El DNI es obligatorio.")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe contener exactamente 8 dígitos.")
    private String dni;

    private EstadoReference estado;
    private String fechaCreacion;

    @NotBlank(message = "Debe seleccionar un rol.")
    private String rol;
    
    private List<PagosDto> pagos = new ArrayList<>();
}
