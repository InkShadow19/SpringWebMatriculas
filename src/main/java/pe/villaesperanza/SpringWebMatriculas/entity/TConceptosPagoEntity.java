package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.ConceptosPagoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "conceptos_pago")
public class TConceptosPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "codigo", length = 10)
    private String codigo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "monto_sugerido")
    private Double montoSugerido;

    @Column(name = "estado")
    private Integer estado = 10;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "conceptosPagoEntity")
    private Set<TCronogramaPagosEntity> cronogramas = new HashSet<>();

    public TConceptosPagoEntity(ConceptosPagoDto conceptosPagoDto) {

        this.identifier = UUID.randomUUID().toString();
        this.codigo = conceptosPagoDto.getCodigo();
        this.descripcion = conceptosPagoDto.getDescripcion();
        this.montoSugerido = conceptosPagoDto.getMontoSugerido();

        if (conceptosPagoDto.getCronogramas() != null)
            this.cronogramas.addAll(conceptosPagoDto.getCronogramas().parallelStream().map(x -> new TCronogramaPagosEntity(x, this, null)).toList());
    }

    public void update(ConceptosPagoDto conceptosPagoDto) {
        
        if (conceptosPagoDto.getCodigo() != null) {
            this.codigo = conceptosPagoDto.getCodigo();
        }
        if (conceptosPagoDto.getDescripcion() != null) {
            this.descripcion = conceptosPagoDto.getDescripcion();
        }
        if (conceptosPagoDto.getMontoSugerido() != null) {
            this.montoSugerido = conceptosPagoDto.getMontoSugerido();
        }
        if (conceptosPagoDto.getEstado() != null) {
            this.estado = conceptosPagoDto.getEstado().getValue();
        }
        this.fechaActualizacion = Instant.now();
    }

    public ConceptosPagoDto toDto() {

        ConceptosPagoDto dto = new ConceptosPagoDto();

        dto.setIdentifier(identifier);
        dto.setCodigo(codigo);
        dto.setDescripcion(descripcion);
        dto.setMontoSugerido(montoSugerido);
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (cronogramas != null)
            dto.setCronogramas(cronogramas.stream().map(TCronogramaPagosEntity::toDto).toList());

        return dto;
    }
}
