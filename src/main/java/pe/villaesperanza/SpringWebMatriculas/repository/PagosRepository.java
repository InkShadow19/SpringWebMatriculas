package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TPagosEntity;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PagosRepository extends JpaRepository<TPagosEntity, Long> {

    Optional<TPagosEntity> findByIdentifier(String identifier);

    @Query("SELECT r FROM TPagosEntity r " +
            "WHERE (:estado is NULL OR r.estado = :estado) " +
            "AND (:canalPago is NULL OR r.canalPago = :canalPago) " +
            "AND (:ticket IS NULL OR r.numeroTicket LIKE %:ticket%) " +
            "AND (:monto IS NULL OR r.montoTotalPagado LIKE %:monto%) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaPago >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaPago <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TPagosEntity> searchPagos(Integer estado, Integer canalPago, String ticket, Double monto, Instant fechaDesde, Instant fechaHasta, Pageable pageable);
}
