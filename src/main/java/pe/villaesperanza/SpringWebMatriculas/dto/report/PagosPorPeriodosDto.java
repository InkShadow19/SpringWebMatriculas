package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.CanalReference;

import java.time.Instant;

@Getter
@Setter
public class PagosPorPeriodosDto {

    private String numeroTicket;
    private Instant fechaPago;
    private Double montoPagado;
    private CanalReference canalPago;
    private String nombreBanco;
    private String nombreUsuario;
}
