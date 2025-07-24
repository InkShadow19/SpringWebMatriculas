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
public class NivelesDto implements Serializable {

    private String identifier;
    @NotBlank(message = "La descripción es obligatoria.")
    @Size(min = 3, max = 100, message = "La descripción debe tener entre 3 y 100 caracteres.")
    private String descripcion;

    @NotNull(message = "El estado no puede ser nulo.")
    private EstadoReference estado;

    private String fechaCreacion;

    private List<GradosDto> grados = new ArrayList<>();
    private List<MatriculasDto> matriculas = new ArrayList<>();
}
