package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.BancosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bancos")
public class TBancosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "codigo", length = 10)
    private String codigo;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    @Column(name = "estado")
    private Integer estado = 10;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "bancosEntity")
    private Set<TPagosEntity> pagos = new HashSet<>();

    public TBancosEntity(BancosDto bancosDto) {

        this.identifier = UUID.randomUUID().toString();
        this.codigo = bancosDto.getCodigo();
        this.descripcion = bancosDto.getDescripcion();
    }

    public void update(BancosDto bancosDto) {

        if (bancosDto.getCodigo() != null) {
            this.codigo = bancosDto.getCodigo();
        }
        if (bancosDto.getDescripcion() != null) {
            this.descripcion = bancosDto.getDescripcion();
        }
        if (bancosDto.getEstado() != null) {
            this.estado = bancosDto.getEstado().getValue();
        }
        this.fechaActualizacion = Instant.now();
    }

    public BancosDto toDto() {

        BancosDto dto = new BancosDto();

        dto.setIdentifier(identifier);
        dto.setCodigo(codigo);
        dto.setDescripcion(descripcion);
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (pagos != null)
            dto.setPagos(pagos.stream().map(TPagosEntity::toDto).toList());

        return dto;
    }
}
