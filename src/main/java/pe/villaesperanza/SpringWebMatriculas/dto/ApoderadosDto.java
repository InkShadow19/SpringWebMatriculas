package pe.villaesperanza.SpringWebMatriculas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ApoderadosDto implements Serializable {

    private String identifier;
    @NotBlank(message = "El DNI no puede estar vacío.")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe contener exactamente 8 dígitos.")
    private String dni;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    private String nombre;

    @NotBlank(message = "El apellido paterno no puede estar vacío.")
    @Size(min = 2, max = 50, message = "El apellido paterno debe tener entre 2 y 50 caracteres.")
    private String apellidoPaterno;

    @Size(max = 50, message = "El apellido materno no debe exceder los 50 caracteres.")
    private String apellidoMaterno;

    @NotBlank(message = "La fecha de nacimiento es obligatoria.")
    private String fechaNacimiento;

    @NotNull(message = "El género es obligatorio.")
    private GeneroReference genero;

    @NotBlank(message = "El parentesco no puede estar vacío.")
    private String parentesco;

    @Pattern(regexp = "^$|\\d{9}", message = "El teléfono debe tener 9 dígitos o estar vacío.")
    private String telefono;

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El formato del correo electrónico no es válido.")
    private String email;

    private String direccion;

    @NotNull(message = "El estado es obligatorio.")
    private EstadoReference estado;

    private String fechaCreacion;

    private List<MatriculasDto> matriculas = new ArrayList<>();
}
