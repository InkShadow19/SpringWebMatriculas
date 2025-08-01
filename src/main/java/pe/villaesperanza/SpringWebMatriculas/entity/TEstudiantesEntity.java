package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.EstudiantesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "estudiantes")
public class TEstudiantesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "dni", length = 15)
    private String dni;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "apellido_paterno", length = 100)
    private String apellidoPaterno;

    @Column(name = "apellidoMaterno", length = 100)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento", nullable = false)
    private Instant fechaNacimiento;

    @Column(name = "genero")
    private Integer genero;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "estado_academico")
    private Integer estadoAcademico;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "estudiantesEntity")
    private Set<TMatriculasEntity> matriculas = new HashSet<>();

    public TEstudiantesEntity(EstudiantesDto estudiantesDto) {

        this.identifier = UUID.randomUUID().toString();
        this.dni = estudiantesDto.getDni();
        this.nombre = estudiantesDto.getNombre();
        this.apellidoPaterno = estudiantesDto.getApellidoPaterno();
        this.apellidoMaterno = estudiantesDto.getApellidoMaterno();
        if (estudiantesDto.getFechaNacimiento() != null) this.fechaNacimiento = Instant.parse(estudiantesDto.getFechaNacimiento() + "T00:00:00Z");
        this.genero = estudiantesDto.getGenero().getValue();
        this.direccion = estudiantesDto.getDireccion();
        this.telefono = estudiantesDto.getTelefono();
        this.email = estudiantesDto.getEmail();
        this.estadoAcademico = estudiantesDto.getEstadoAcademico().getValue();
    }

    public void update(EstudiantesDto estudiantesDto) {

        if (estudiantesDto.getDni() != null) {
            this.dni = estudiantesDto.getDni();
        }
        if (estudiantesDto.getNombre() != null) {
            this.nombre = estudiantesDto.getNombre();
        }
        if (estudiantesDto.getApellidoPaterno() != null) {
            this.apellidoPaterno = estudiantesDto.getApellidoPaterno();
        }
        if (estudiantesDto.getApellidoMaterno() != null) {
            this.apellidoMaterno = estudiantesDto.getApellidoMaterno();
        }
        if (estudiantesDto.getFechaNacimiento() != null) {
            String fechaNacimientoStr = estudiantesDto.getFechaNacimiento();
            // Si la fecha no contiene 'T' o 'Z', es una fecha simple.
            if (!fechaNacimientoStr.contains("T") && !fechaNacimientoStr.contains("Z")) {
                this.fechaNacimiento = Instant.parse(fechaNacimientoStr + "T00:00:00Z");
            } else {
                this.fechaNacimiento = Instant.parse(fechaNacimientoStr);
            }
        }
        if (estudiantesDto.getDireccion() != null) {
            this.direccion = estudiantesDto.getDireccion();
        }
        if (estudiantesDto.getTelefono() != null) {
            this.telefono = estudiantesDto.getTelefono();
        }
        if (estudiantesDto.getEmail() != null) {
            this.email = estudiantesDto.getEmail();
        }
        if (estudiantesDto.getGenero() != null) {
            this.genero = estudiantesDto.getGenero().getValue();
        }
        if (estudiantesDto.getEstadoAcademico() != null) {
            this.estadoAcademico = estudiantesDto.getEstadoAcademico().getValue();
        }
        this.fechaActualizacion = Instant.now();
    }

    public EstudiantesDto toDto() {

        EstudiantesDto dto = new EstudiantesDto();

        dto.setIdentifier(identifier);
        dto.setDni(dni);
        dto.setNombre(nombre);
        dto.setApellidoMaterno(apellidoMaterno);
        dto.setApellidoPaterno(apellidoPaterno);
        if (fechaNacimiento != null)
            dto.setFechaNacimiento(fechaNacimiento.toString());
        dto.setGenero(GeneroReference.fromInt(genero));
        dto.setDireccion(direccion);
        dto.setTelefono(telefono);
        dto.setEmail(email);
        dto.setEstadoAcademico(EstadoAcademicoReference.fromInt(estadoAcademico));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (matriculas != null)
            dto.setMatriculas(matriculas.stream().map(TMatriculasEntity::toDto).toList());

        return dto;
    }
}
