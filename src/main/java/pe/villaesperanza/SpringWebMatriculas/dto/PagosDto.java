package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.CanalReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PagosDto implements Serializable {

    private String identifier;
    private CanalReference canalPago;
    private String numeroTicket;
    private Double montoTotalPagado;
    private String fechaPago;
    private EstadoReference estado;
    private String fechaCreacion;

    private String usuario;
    private String banco;
    private List<PagoDetallesDto> detalles = new ArrayList<>();
}
