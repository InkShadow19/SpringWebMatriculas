package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.ConceptosPagoDto;

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

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado = true;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "conceptosPagoEntity")
    private Set<TCronogramaPagosEntity> cronogramas = new HashSet<>();

    public TConceptosPagoEntity(ConceptosPagoDto conceptosPagoDto) {

        this.identifier = UUID.randomUUID().toString();
        this.codigo = conceptosPagoDto.getCodigo();
        this.descripcion = conceptosPagoDto.getDescripcion();
        this.montoSugerido = conceptosPagoDto.getMontoSugerido();
    }
}
