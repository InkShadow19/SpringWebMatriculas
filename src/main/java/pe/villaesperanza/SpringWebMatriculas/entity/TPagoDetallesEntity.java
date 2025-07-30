package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.PagoDetallesDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "pago_detalle")
public class TPagoDetallesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "monto_aplicado")
    private Double montoAplicado;

    @Column(name = "estado")
    private Integer estado = 10;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @ManyToOne
    @JoinColumn(name = "id_cronograma_pagos")
    private TCronogramaPagosEntity cronogramaPagosEntity;

    @ManyToOne
    @JoinColumn(name = "id_pagos")
    private TPagosEntity pagosEntity;

    public TPagoDetallesEntity(PagoDetallesDto detallesDto, TCronogramaPagosEntity cronogramaPagosEntity, TPagosEntity pagosEntity) {

        this.cronogramaPagosEntity = cronogramaPagosEntity;
        this.pagosEntity = pagosEntity;
        this.identifier = UUID.randomUUID().toString();
        this.montoAplicado = detallesDto.getMontoAplicado();
    }

    public PagoDetallesDto toDto() {

        PagoDetallesDto dto = new PagoDetallesDto();

        dto.setIdentifier(identifier);
        dto.setMontoAplicado(montoAplicado);
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());
        if (cronogramaPagosEntity != null) {
            dto.setCronograma(cronogramaPagosEntity.getIdentifier());
            dto.setDescripcionCronograma(cronogramaPagosEntity.getDescripcion());
        }
        if (pagosEntity != null) dto.setPago(pagosEntity.getIdentifier());

        return dto;
    }
}
