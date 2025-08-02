package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

// ... (imports)
@Getter
@Setter
@NoArgsConstructor
public class PagosPorPeriodosDto implements Serializable {
    private String numeroTicket;
    private Instant fechaPago;
    private String nombreEstudiante;
    private Double montoPagado;
    private String canalYBanco;
    private String nombreUsuario;

    public PagosPorPeriodosDto(String numeroTicket, Instant fechaPago, String nombreEstudiante, Double montoPagado, String canalYBanco, String nombreUsuario) {
        this.numeroTicket = numeroTicket;
        this.fechaPago = fechaPago;
        this.nombreEstudiante = nombreEstudiante;
        this.montoPagado = montoPagado;
        this.canalYBanco = canalYBanco;
        this.nombreUsuario = nombreUsuario;
    }
}