package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.AniosAcademicosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoAcademicoReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "anios_academicos")
public class TAniosAcademicosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "estado_academico")
    private Integer estadoAcademico;

    @Column(name = "estado")
    private Integer estado = 10;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "aniosAcademicosEntity")
    private Set<TMatriculasEntity> matriculas = new HashSet<>();

    public TAniosAcademicosEntity(AniosAcademicosDto aniosAcademicosDto) {

        this.identifier = UUID.randomUUID().toString();
        this.anio = aniosAcademicosDto.getAnio();
        this.estadoAcademico = aniosAcademicosDto.getEstadoAcademico().getValue();

        if (aniosAcademicosDto.getMatriculas() != null)
            this.matriculas.addAll(aniosAcademicosDto.getMatriculas().parallelStream().map(x -> new TMatriculasEntity(x, null, null, null, null, this)).toList());
    }

    public AniosAcademicosDto toDto() {

        AniosAcademicosDto dto = new AniosAcademicosDto();

        dto.setIdentifier(identifier);
        dto.setAnio(anio);
        dto.setEstadoAcademico(EstadoAcademicoReference.fromInt(estadoAcademico));
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (matriculas != null)
            dto.setMatriculas(matriculas.stream().map(TMatriculasEntity::toDto).toList());

        return dto;
    }
}
