package pe.villaesperanza.SpringWebMatriculas.dto;

import lombok.Getter;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.CanalReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoPagoReference;

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
    private EstadoPagoReference estado;
    private String fechaCreacion;
    private String usuario;
    private String banco;

    // --- CAMPOS AÑADIDOS PARA LA VISTA DE LISTA ---
    private String nombreEstudiante;
    private String nombreUsuario;
    private String nombreBanco; // Para el nombre completo del banco
    private String montoTotalEnPalabras; // Para el texto del monto

    // --- NUEVO CAMPO PARA LA FECHA FORMATEADA ---
    private String fechaPagoFormateada; 

    // --- NUEVO CAMPO PARA LA SUMA DE MORAS ---
    private Double totalMora;

    private List<PagoDetallesDto> detalles = new ArrayList<>();
}
