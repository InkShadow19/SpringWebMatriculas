package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.MatriculasDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.SituacionReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "matriculas")
public class TMatriculasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "codigo", length = 20)
    private Integer codigo;

    @Column(name = "situacion")
    private Integer situacion;

    @Column(name = "fecha_matricula", nullable = false)
    private Instant fechaMatricula = Instant.now();

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado = true;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @ManyToOne
    @JoinColumn(name = "id_niveles")
    private TNivelesEntity nivelesEntity;

    @ManyToOne
    @JoinColumn(name = "id_grados")
    private TGradosEntity gradosEntity;

    @ManyToOne
    @JoinColumn(name = "id_estudiantes")
    private TEstudiantesEntity estudiantesEntity;

    @ManyToOne
    @JoinColumn(name = "id_apoderados")
    private TApoderadosEntity apoderadosEntity;

    @ManyToOne
    @JoinColumn(name = "id_anios_academicos")
    private TAñosAcademicosEntity añosAcademicosEntity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "matriculasEntity")
    private Set<TCronogramaPagosEntity> cronogramas = new HashSet<>();

    public TMatriculasEntity(MatriculasDto matriculasDto, TNivelesEntity nivelesEntity,
                             TGradosEntity gradosEntity, TEstudiantesEntity estudiantesEntity,
                             TApoderadosEntity apoderadosEntity, TAñosAcademicosEntity añosAcademicosEntity) {

        this.nivelesEntity = nivelesEntity;
        this.gradosEntity = gradosEntity;
        this.estudiantesEntity = estudiantesEntity;
        this.apoderadosEntity = apoderadosEntity;
        this.añosAcademicosEntity = añosAcademicosEntity;

        this.identifier = UUID.randomUUID().toString();
        this.codigo = matriculasDto.getCodigo();
        this.situacion = matriculasDto.getSituacion().getValue();
        if (matriculasDto.getFechaMatricula() != null) this.fechaMatricula = Instant.parse(matriculasDto.getFechaMatricula());

        if (matriculasDto.getCronogramas() != null)
            this.cronogramas.addAll(matriculasDto.getCronogramas().parallelStream().map(x -> new TCronogramaPagosEntity(x, null, this)).toList());
    }

    public MatriculasDto toDto() {

        MatriculasDto dto = new MatriculasDto();

        dto.setIdentifier(identifier);
        dto.setCodigo(codigo);
        dto.setSituacion(SituacionReference.fromInt(situacion));
        if (fechaMatricula != null) dto.setFechaMatricula(fechaMatricula.toString());
        dto.setHabilitado(habilitado);
        dto.setFechaCreacion(fechaCreacion.toString());

        if (nivelesEntity != null) dto.setNivel(nivelesEntity.getIdentifier());
        if (gradosEntity != null) dto.setGrado(gradosEntity.getIdentifier());
        if (estudiantesEntity != null) dto.setEstudiante(estudiantesEntity.getIdentifier());
        if (apoderadosEntity != null) dto.setApoderado(apoderadosEntity.getIdentifier());
        if (añosAcademicosEntity != null) dto.setAnioAcademico(añosAcademicosEntity.getIdentifier());

        if (cronogramas != null)
            dto.setCronogramas(cronogramas.stream().map(TCronogramaPagosEntity::toDto).toList());

        return dto;
    }

}
