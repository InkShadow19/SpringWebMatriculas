package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.CronogramaPagosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoDeudaReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cronograma_pagos")
public class TCronogramaPagosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "monto_original")
    private Double montoOriginal;

    @Column(name = "descuento")
    private Double descuento;

    @Column(name = "mora")
    private Double mora;

    @Column(name = "monto_a_pagar")
    private Double montoAPagar;

    @Column(name = "fecha_vencimiento", nullable = false)
    private Instant fechaVencimiento;

    @Column(name = "estado_deuda")
    private Integer estadoDeuda;

    @Column(name = "estado")
    private Integer estado = 10;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @ManyToOne
    @JoinColumn(name = "id_conceptos_pago")
    private TConceptosPagoEntity conceptosPagoEntity;

    @ManyToOne
    @JoinColumn(name = "id_matriculas")
    private TMatriculasEntity matriculasEntity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "cronogramaPagosEntity")
    private Set<TPagoDetallesEntity> detalles = new HashSet<>();

    public TCronogramaPagosEntity(CronogramaPagosDto cronogramaPagosDto, TConceptosPagoEntity conceptosPagoEntity, TMatriculasEntity matriculasEntity) {

        this.conceptosPagoEntity = conceptosPagoEntity;
        this.matriculasEntity = matriculasEntity;
        this.identifier = UUID.randomUUID().toString();
        this.descripcion = cronogramaPagosDto.getDescripcion();
        this.montoOriginal = cronogramaPagosDto.getMontoOriginal();
        this.descuento = cronogramaPagosDto.getDescuento();
        this.mora = cronogramaPagosDto.getMora();
        this.montoAPagar = cronogramaPagosDto.getMontoAPagar();
        if (cronogramaPagosDto.getFechaVencimiento() != null) this.fechaVencimiento = Instant.parse(cronogramaPagosDto.getFechaVencimiento());
        this.estadoDeuda = cronogramaPagosDto.getEstadoDeuda().getValue();

        if (cronogramaPagosDto.getDetalles() != null)
            this.detalles.addAll(cronogramaPagosDto.getDetalles().parallelStream().map(x -> new TPagoDetallesEntity(x, this, null)).toList());
    }

    public CronogramaPagosDto toDto() {

        CronogramaPagosDto dto = new CronogramaPagosDto();

        dto.setIdentifier(identifier);
        dto.setDescripcion(descripcion);
        dto.setMontoOriginal(montoOriginal);
        dto.setDescuento(descuento);
        dto.setMora(mora);
        dto.setMontoAPagar(montoAPagar);
        if (fechaVencimiento != null) dto.setFechaVencimiento(fechaVencimiento.toString());
        dto.setEstadoDeuda(EstadoDeudaReference.fromInt(estadoDeuda));
        dto.setEstado(EstadoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (conceptosPagoEntity != null) dto.setConcepto(conceptosPagoEntity.getIdentifier());
        if (matriculasEntity != null) dto.setMatricula(matriculasEntity.getIdentifier());

        if (detalles != null)
            dto.setDetalles(detalles.stream().map(TPagoDetallesEntity::toDto).toList());

        return dto;
    }
}
