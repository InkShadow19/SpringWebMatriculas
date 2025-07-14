package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PorMorosidadDto {

    private String nombreEstudiante;
    private String nombreApoderado;
    private String telefonoApoderado;
    private String conceptoDeuda ;
    private Instant fechaVencimiento ;
    private Double montoPendiente ;
}
