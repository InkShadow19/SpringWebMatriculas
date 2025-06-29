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
    private Instant fechaPago;

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado = true;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion = Instant.now();

    @ManyToOne
    @JoinColumn(name = "id_usuarios")
    private TUsuariosEntity usuariosEntity;

    @ManyToOne
    @JoinColumn(name = "id_bancos")
    private TBancosEntity bancosEntity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pagosEntity")
    private Set<TPagoDetallesEntity> detalles = new HashSet<>();
}
