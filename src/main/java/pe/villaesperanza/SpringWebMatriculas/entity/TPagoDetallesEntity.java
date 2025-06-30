package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.PagoDetallesDto;

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

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado = true;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

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
        dto.setHabilitado(habilitado);
        dto.setFechaCreacion(fechaCreacion.toString());

        if (cronogramaPagosEntity != null) dto.setCronograma(cronogramaPagosEntity.getIdentifier());
        if (pagosEntity != null) dto.setPago(pagosEntity.getIdentifier());

        return dto;
    }
}
