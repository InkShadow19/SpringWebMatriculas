package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.CanalReference;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class PagosPorPeriodosDto implements Serializable {

    private String numeroTicket;
    private Instant fechaPago;
    private Double montoPagado;
    private CanalReference canalPago;
    private String nombreBanco;
    private String nombreUsuario;

    // Este es el constructor que la consulta necesita.
    public PagosPorPeriodosDto(String numeroTicket, Instant fechaPago, Double montoPagado, Integer canalPagoValue, String nombreBanco, String nombreUsuario) {
        this.numeroTicket = numeroTicket;
        this.fechaPago = fechaPago;
        this.montoPagado = montoPagado;
        this.canalPago = CanalReference.fromInt(canalPagoValue);
        this.nombreBanco = nombreBanco;
        this.nombreUsuario = nombreUsuario;
    }
}