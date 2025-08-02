package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // Este constructor es el que usará la consulta
public class MorosidadAgrupadaDto {

    private String nombreEstudiante;
    private String nombreApoderado;
    private String telefonoApoderado;
    private Long cuotasVencidas; // Cantidad de deudas vencidas
    private Double montoTotalAdeudado; // Suma de los montos
    
}