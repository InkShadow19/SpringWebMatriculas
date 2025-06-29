package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.CanalReference;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PagosDto implements Serializable {

    private String identifier;
    private CanalReference canalPago;
    private String numeroTicket;
    private Double montoTotalPagado;
    private Instant fechaPago;
    private boolean habilitado;
    private Instant fechaCreacion;

    private String usuario;
    private String banco;
    private List<PagoDetallesDto> detalles = new ArrayList<>();
}
