package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.RolesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "roles")
public class TRolesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false, updatable = false)
    private String identifier;

    @Column(name = "descripcion", length = 50)
    private String descripcion;

    @Column(name = "estado")
    private Integer estado = 10;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "rolesEntity")
    private Set<TUsuariosEntity> usuarios = new HashSet<>();

    public TRolesEntity(RolesDto roles) {

        this.identifier = UUID.randomUUID().toString();
        this.descripcion = roles.getDescripcion();

        if (roles.getUsuarios() != null)
            this.usuarios.addAll(roles.getUsuarios().parallelStream().map(x -> new TUsuariosEntity(x, this)).toList());
    }

    public void update(RolesDto rolesDto) {

        if (rolesDto.getDescripcion() != null) {
            this.descripcion = rolesDto.getDescripcion();
        }
        if (rolesDto.getEstado() != null) {
            this.estado = rolesDto.getEstado().getValue();
        }
        this.fechaActualizacion = Instant.now();
    }

    public RolesDto toDto() {

        RolesDto dto = new RolesDto();

        dto.setIdentifier(identifier);
        dto.setDescripcion(descripcion);
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (usuarios != null)
            dto.setUsuarios(usuarios.stream().map(TUsuariosEntity::toDto).toList());

        return dto;
    }
}
