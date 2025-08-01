package pe.villaesperanza.SpringWebMatriculas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.villaesperanza.SpringWebMatriculas.dto.PagosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.CanalReference;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoPagoReference;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "pagos")
public class TPagosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "identifier", length = 36, nullable = false)
    private String identifier;

    @Column(name = "canal_pago")
    private Integer canalPago;

    @Column(name = "numero_ticket")
    private String numeroTicket;

    @Column(name = "monto_total_pagado")
    private Double montoTotalPagado;

    @Column(name = "fecha_pago", nullable = false)
    //private Instant fechaPago = Instant.now();
    private Instant fechaPago;

    @Column(name = "estado")
    private Integer estado = 10;

    @Column(name = "fecha_creacion", nullable = false)
    //private Instant fechaCreacion = Instant.now();
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    //private Instant fechaActualizacion = Instant.now();
    private Instant fechaActualizacion;

    @ManyToOne
    @JoinColumn(name = "id_usuarios")
    private TUsuariosEntity usuariosEntity;

    @ManyToOne
    @JoinColumn(name = "id_bancos")
    private TBancosEntity bancosEntity;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "pagosEntity")
    private Set<TPagoDetallesEntity> detalles = new HashSet<>();

    public TPagosEntity(PagosDto pagosDto, TUsuariosEntity usuariosEntity, TBancosEntity bancosEntity) {

        this.usuariosEntity = usuariosEntity;
        this.bancosEntity = bancosEntity;
        this.identifier = UUID.randomUUID().toString();
        this.canalPago = pagosDto.getCanalPago().getValue();
        this.numeroTicket = pagosDto.getNumeroTicket();
        this.montoTotalPagado = pagosDto.getMontoTotalPagado();
        //if (pagosDto.getFechaPago() != null) this.fechaPago = Instant.parse(pagosDto.getFechaPago());

        if (pagosDto.getDetalles() != null)
            this.detalles.addAll(pagosDto.getDetalles().parallelStream().map(x -> new TPagoDetallesEntity(x, null, this)).toList());
    }

    public void update(PagosDto pagosDto) {

        this.canalPago = pagosDto.getCanalPago().getValue();
        this.numeroTicket = pagosDto.getNumeroTicket();
        this.montoTotalPagado = pagosDto.getMontoTotalPagado();
        if (pagosDto.getFechaPago() != null) this.fechaPago = Instant.parse(pagosDto.getFechaPago());

        //this.fechaActualizacion = Instant.now();
    }

    public PagosDto toDto() {

        PagosDto dto = new PagosDto();

        dto.setIdentifier(identifier);
        dto.setCanalPago(CanalReference.fromInt(canalPago));
        dto.setNumeroTicket(numeroTicket);
        dto.setMontoTotalPagado(montoTotalPagado);
        dto.setEstado(EstadoPagoReference.fromInt(estado));
        dto.setFechaCreacion(fechaCreacion.toString());

        if (fechaPago != null) dto.setFechaPago(fechaPago.toString());
        if (usuariosEntity != null) dto.setUsuario(usuariosEntity.getIdentifier());
        if (bancosEntity != null) dto.setBanco(bancosEntity.getIdentifier());

        if (detalles != null)
            dto.setDetalles(detalles.stream().map(TPagoDetallesEntity::toDto).toList());

        return dto;
    }
}
