package pe.villaesperanza.SpringWebMatriculas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BancosDto implements Serializable {

    private String identifier;
    @NotBlank(message = "El código del banco es obligatorio.")
    @Size(min = 2, max = 15, message = "El código debe tener entre 2 y 15 caracteres.")
    private String codigo;

    @NotBlank(message = "La descripción del banco es obligatoria.")
    @Size(min = 3, max = 150, message = "La descripción debe tener entre 3 y 150 caracteres.")
    private String descripcion;

    @NotNull(message = "El estado no puede ser nulo.")
    private EstadoReference estado;

    private String fechaCreacion;

    private List<PagosDto> pagos = new ArrayList<>();
}
