package pe.villaesperanza.SpringWebMatriculas.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.SituacionReference;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor // <-- Añadido: Es una buena práctica tener un constructor sin argumentos.
public class AlumnoPorGradoDto {

    // (Tus variables existentes se mantienen intactas)
    private String dniEstudiante;
    private String nombreEstudiante;
    private String nombreApoderado;
    private String telefonoApoderado;
    private Instant fechaMatricula;
    private String situacionAlumno;

    // --- CONSTRUCTOR AÑADIDO ---
    // Este es el constructor que la consulta @Query en tu repositorio busca.
    // Recibe los datos de la consulta y los asigna a TUS variables.
    public AlumnoPorGradoDto(
            String dni,
            String nombreAlumno,
            String nombreApoderado,
            String telefono,
            Instant fechaMatricula,
            Integer situacionValue
    ) {
        this.dniEstudiante = dni;
        this.nombreEstudiante = nombreAlumno;
        this.nombreApoderado = nombreApoderado;
        this.telefonoApoderado = telefono;
        this.fechaMatricula = fechaMatricula;
        // Se convierte el número de la situación (ej: 10) a su nombre en texto (ej: "PROMOVIDO")
        this.situacionAlumno = SituacionReference.fromInt(situacionValue).name();
    }
}