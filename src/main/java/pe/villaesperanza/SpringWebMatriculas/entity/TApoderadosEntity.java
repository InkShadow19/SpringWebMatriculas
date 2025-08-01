package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.ApoderadosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.GeneroReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "apoderados")
public class TApoderadosEntity {

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

    @Column(name = "parentesco")
    private String parentesco;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "estado")
    private Integer estado = 10;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "apoderadosEntity")
    private Set<TMatriculasEntity> matriculas = new HashSet<>();

    public TApoderadosEntity(ApoderadosDto apoderadosDto) {

        this.identifier = UUID.randomUUID().toString();
        this.dni = apoderadosDto.getDni();
        this.nombre = apoderadosDto.getNombre();
        this.apellidoPaterno = apoderadosDto.getApellidoPaterno();
        this.apellidoMaterno = apoderadosDto.getApellidoMaterno();
        if (apoderadosDto.getFechaNacimiento() != null) this.fechaNacimiento = Instant.parse(apoderadosDto.getFechaNacimiento() + "T00:00:00Z");
        this.parentesco = apoderadosDto.getParentesco();
        this.genero = apoderadosDto.getGenero().getValue();
        this.direccion = apoderadosDto.getDireccion();
        this.telefono = apoderadosDto.getTelefono();
        this.email = apoderadosDto.getEmail();
    }

    public void update(ApoderadosDto apoderadosDto) {

        if (apoderadosDto.getDni() != null) {
            this.dni = apoderadosDto.getDni();
        }
        if (apoderadosDto.getNombre() != null) {
            this.nombre = apoderadosDto.getNombre();
        }
        if (apoderadosDto.getApellidoPaterno() != null) {
            this.apellidoPaterno = apoderadosDto.getApellidoPaterno();
        }
        if (apoderadosDto.getApellidoMaterno() != null) {
            this.apellidoMaterno = apoderadosDto.getApellidoMaterno();
        }
        if (apoderadosDto.getFechaNacimiento() != null) {
            String fechaNacimientoStr = apoderadosDto.getFechaNacimiento();
            // Si la fecha no contiene 'T' o 'Z', es una fecha simple.
            if (!fechaNacimientoStr.contains("T") && !fechaNacimientoStr.contains("Z")) {
                this.fechaNacimiento = Instant.parse(fechaNacimientoStr + "T00:00:00Z");
            } else {
                this.fechaNacimiento = Instant.parse(fechaNacimientoStr);
            }
        }
        if (apoderadosDto.getParentesco() != null) {
            this.parentesco = apoderadosDto.getParentesco();
        }
        if (apoderadosDto.getDireccion() != null) {
            this.direccion = apoderadosDto.getDireccion();
        }
        if (apoderadosDto.getTelefono() != null) {
            this.telefono = apoderadosDto.getTelefono();
        }
        if (apoderadosDto.getEmail() != null) {
            this.email = apoderadosDto.getEmail();
        }
        if (apoderadosDto.getGenero() != null) {
            this.genero = apoderadosDto.getGenero().getValue();
        }
        if (apoderadosDto.getEstado() != null) {
            this.estado = apoderadosDto.getEstado().getValue();
        }
        this.fechaActualizacion = Instant.now();
    }

    public ApoderadosDto toDto() {

        ApoderadosDto dto = new ApoderadosDto();

        dto.setIdentifier(identifier);
        dto.setDni(dni);
        dto.setNombre(nombre);
        dto.setApellidoMaterno(apellidoMaterno);
        dto.setApellidoPaterno(apellidoPaterno);
        if (fechaNacimiento != null)
            dto.setFechaNacimiento(fechaNacimiento.toString());
        dto.setGenero(GeneroReference.fromInt(genero));
        dto.setParentesco(parentesco);
        dto.setDireccion(direccion);
        dto.setTelefono(telefono);
        dto.setEmail(email);
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (matriculas != null)
            dto.setMatriculas(matriculas.stream().map(TMatriculasEntity::toDto).toList());

        return dto;
    }
}
