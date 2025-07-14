package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.BancosDto;
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
    }

    public void update(GradosDto gradosDto) {

        this.descripcion = gradosDto.getDescripcion();
        if (gradosDto.getEstado() != null) this.estado = gradosDto.getEstado().getValue();

        this.fechaActualizacion = Instant.now();
    }

    public void update(GradosDto gradosDto) {
        
        if (gradosDto.getDescripcion() != null) {
            this.descripcion = gradosDto.getDescripcion();
        }
        if (gradosDto.getEstado() != null) {
            this.estado = gradosDto.getEstado().getValue();
        }
        // Asumiendo que no puedes cambiar el nivel de un grado ya creado.
        // Si se pudiera, aquí iría la lógica para actualizar 'nivelesEntity'.

        this.fechaActualizacion = Instant.now();
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
