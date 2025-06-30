package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.RolesDto;

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

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "descripcion", length = 50)
    private String descripcion;

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado = true;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "rolesEntity")
    private Set<TUsuariosEntity> usuarios = new HashSet<>();

    public TRolesEntity(RolesDto roles) {

        this.identifier = UUID.randomUUID().toString();
        this.descripcion = roles.getDescripcion();
    }

    public RolesDto toDto() {

        RolesDto dto = new RolesDto();

        dto.setIdentifier(identifier);
        dto.setDescripcion(descripcion);
        dto.setHabilitado(habilitado);
        dto.setFechaCreacion(fechaCreacion.toString());

        return dto;
    }
}
