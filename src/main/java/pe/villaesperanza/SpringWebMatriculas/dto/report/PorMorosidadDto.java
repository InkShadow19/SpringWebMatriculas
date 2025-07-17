package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor // <-- Añadido: Buena práctica para tener un constructor vacío.
public class PorMorosidadDto implements Serializable {

    private String nombreEstudiante;
    private String nombreApoderado;
    private String telefonoApoderado;
    private String conceptoDeuda;
    private Instant fechaVencimiento;
    private Double montoPendiente;

    // Este es el constructor que la consulta del repositorio necesita para funcionar.
    public PorMorosidadDto(String nombreEstudiante, String nombreApoderado, String telefonoApoderado, String conceptoDeuda, Instant fechaVencimiento, Double montoPendiente) {
        this.nombreEstudiante = nombreEstudiante;
        this.nombreApoderado = nombreApoderado;
        this.telefonoApoderado = telefonoApoderado;
        this.conceptoDeuda = conceptoDeuda;
        this.fechaVencimiento = fechaVencimiento;
        this.montoPendiente = montoPendiente;
    }
}