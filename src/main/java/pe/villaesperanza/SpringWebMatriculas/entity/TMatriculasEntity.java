package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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
}
