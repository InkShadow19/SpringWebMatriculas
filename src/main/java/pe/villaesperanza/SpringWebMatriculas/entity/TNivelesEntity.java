package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.NivelesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "niveles")
public class TNivelesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @Column(name = "estado")
    private Integer estado = 10;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "nivelesEntity")
    private Set<TGradosEntity> grados = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "nivelesEntity")
    private Set<TMatriculasEntity> matriculas = new HashSet<>();

    public TNivelesEntity(NivelesDto nivelesDto) {

        this.identifier = UUID.randomUUID().toString();
        this.descripcion = nivelesDto.getDescripcion();
    }

    public void update(NivelesDto nivelesDto) {

        this.descripcion = nivelesDto.getDescripcion();
        if (nivelesDto.getEstado() != null) this.estado = nivelesDto.getEstado().getValue();

        this.fechaActualizacion = Instant.now();
    }

    public NivelesDto toDto() {

        NivelesDto dto = new NivelesDto();

        dto.setIdentifier(identifier);
        dto.setDescripcion(descripcion);
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (grados != null)
            dto.setGrados(grados.stream().map(TGradosEntity::toDto).toList());

        if (matriculas != null)
            dto.setMatriculas(matriculas.stream().map(TMatriculasEntity::toDto).toList());

        return dto;
    }
}
