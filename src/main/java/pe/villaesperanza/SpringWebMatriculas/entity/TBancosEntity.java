package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.BancosDto;

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

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado = true;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bancosEntity")
    private Set<TPagosEntity> pagos = new HashSet<>();

    public TBancosEntity(BancosDto bancosDto) {

        this.identifier = UUID.randomUUID().toString();
        this.codigo = bancosDto.getCodigo();
        this.descripcion = bancosDto.getDescripcion();

        if (bancosDto.getPagos() != null)
            this.pagos.addAll(bancosDto.getPagos().parallelStream().map(x -> new TPagosEntity(x, null, this)).toList());
    }

    public BancosDto toDto() {

        BancosDto dto = new BancosDto();

        dto.setIdentifier(identifier);
        dto.setCodigo(codigo);
        dto.setDescripcion(descripcion);
        dto.setHabilitado(habilitado);
        dto.setFechaCreacion(fechaCreacion.toString());

        if (pagos != null)
            dto.setPagos(pagos.stream().map(TPagosEntity::toDto).toList());

        return dto;
    }
}
