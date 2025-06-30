package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.ApoderadosDto;

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

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado = true;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "apoderadosEntity")
    private Set<TMatriculasEntity> matriculas = new HashSet<>();

    public TApoderadosEntity(ApoderadosDto apoderadosDto) {

        this.identifier = UUID.randomUUID().toString();
        this.dni = apoderadosDto.getDni();
        this.nombre = apoderadosDto.getNombre();
        this.apellidoPaterno = apoderadosDto.getApellidoPaterno();
        this.apellidoMaterno = apoderadosDto.getApellidoMaterno();
        if (apoderadosDto.getFechaNacimiento() != null) this.fechaNacimiento = Instant.parse(apoderadosDto.getFechaNacimiento());
        this.parentesco = apoderadosDto.getParentesco();
        this.genero = apoderadosDto.getGenero().getValue();
        this.direccion = apoderadosDto.getDireccion();
        this.telefono = apoderadosDto.getTelefono();
        this.email = apoderadosDto.getEmail();
    }
}
