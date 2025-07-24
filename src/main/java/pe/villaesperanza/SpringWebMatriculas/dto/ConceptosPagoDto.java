package pe.villaesperanza.SpringWebMatriculas.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class ConceptosPagoDto implements Serializable {

    private String identifier;
    @NotBlank(message = "El código del concepto es obligatorio.")
    @Size(min = 3, max = 30, message = "El código debe tener entre 3 y 30 caracteres.")
    private String codigo;

    @NotBlank(message = "La descripción del concepto es obligatoria.")
    @Size(min = 5, max = 150, message = "La descripción debe tener entre 5 y 150 caracteres.")
    private String descripcion;

    @NotNull(message = "El monto sugerido es obligatorio.")
    @DecimalMin(value = "0.0", message = "El monto sugerido no puede ser negativo.")
    private Double montoSugerido;

    @NotNull(message = "El estado no puede ser nulo.")
    private EstadoReference estado;

    private String fechaCreacion;

    private List<CronogramaPagosDto> cronogramas = new ArrayList<>();
}
