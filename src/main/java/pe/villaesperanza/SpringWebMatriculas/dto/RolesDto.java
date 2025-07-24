package pe.villaesperanza.SpringWebMatriculas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
    @NotBlank(message = "La descripción del rol es obligatoria.")
    @Size(min = 3, max = 50, message = "La descripción debe tener entre 3 y 50 caracteres.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "La descripción solo puede contener letras y espacios.")
    private String descripcion;

    @NotNull(message = "El estado no puede ser nulo.")
    private EstadoReference estado;

    private String fechaCreacion;

    private List<UsuariosDto> usuarios = new ArrayList<>();
}
