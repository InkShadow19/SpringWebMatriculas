package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.GradosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "grados")
public class TGradosEntity {

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

    @ManyToOne
    @JoinColumn(name = "id_niveles")
    private TNivelesEntity nivelesEntity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "gradosEntity")
    private Set<TMatriculasEntity> matriculas = new HashSet<>();

    public TGradosEntity(GradosDto gradosDto, TNivelesEntity nivelesEntity) {

        this.nivelesEntity = nivelesEntity;
        this.identifier = UUID.randomUUID().toString();
        this.descripcion = gradosDto.getDescripcion();

        if (gradosDto.getMatriculas() != null)
            this.matriculas.addAll(gradosDto.getMatriculas().parallelStream().map(x -> new TMatriculasEntity(x, null, this, null, null, null)).toList());
    }

    public GradosDto toDto() {

        GradosDto dto = new GradosDto();

        dto.setIdentifier(identifier);
        dto.setDescripcion(descripcion);
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (nivelesEntity != null) dto.setNivel(nivelesEntity.getIdentifier());

        if (matriculas != null)
            dto.setMatriculas(matriculas.stream().map(TMatriculasEntity::toDto).toList());

        return dto;
    }
}
