package pe.villaesperanza.SpringWebMatriculas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AniosAcademicosDto implements Serializable {

    private String identifier;
    @NotNull(message = "El año es obligatorio.")
    // Validamos que el año sea de 4 dígitos y esté en un rango lógico.
    @Min(value = 2000, message = "El año debe ser igual o mayor a 2000.")
    @Max(value = 2099, message = "El año no puede ser mayor a 2099.")
    private Integer anio;

    @NotNull(message = "El estado académico es obligatorio.")
    private EstadoAcademicoReference estadoAcademico;

    private String fechaCreacion;

    private List<MatriculasDto> matriculas = new ArrayList<>();
}
