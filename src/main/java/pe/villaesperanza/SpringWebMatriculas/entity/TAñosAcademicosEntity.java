package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.AñosAcademicosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "anios_academicos")
public class TAñosAcademicosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado = true;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "añosAcademicosEntity")
    private Set<TMatriculasEntity> matriculas = new HashSet<>();

    public TAñosAcademicosEntity(AñosAcademicosDto añosAcademicosDto) {

        this.identifier = UUID.randomUUID().toString();
        this.anio = añosAcademicosDto.getAnio();
        this.estado = añosAcademicosDto.getEstado().getValue();

        if (añosAcademicosDto.getMatriculas() != null)
            this.matriculas.addAll(añosAcademicosDto.getMatriculas().parallelStream().map(x -> new TMatriculasEntity(x, null, null, null, null, this)).toList());
    }

    public AñosAcademicosDto toDto() {

        AñosAcademicosDto dto = new AñosAcademicosDto();

        dto.setIdentifier(identifier);
        dto.setAnio(anio);
        dto.setEstado(EstadoAcademicoReference.fromInt(estado));
        dto.setHabilitado(habilitado);
        dto.setFechaCreacion(fechaCreacion.toString());

        if (matriculas != null)
            dto.setMatriculas(matriculas.stream().map(TMatriculasEntity::toDto).toList());

        return dto;
    }
}
