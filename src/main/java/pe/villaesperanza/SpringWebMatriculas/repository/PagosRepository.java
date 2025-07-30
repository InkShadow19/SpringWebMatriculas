package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.dto.report.PagosPorPeriodosDto;
import pe.villaesperanza.SpringWebMatriculas.entity.TPagosEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagosRepository extends JpaRepository<TPagosEntity, Long> {

    Optional<TPagosEntity> findByIdentifier(String identifier);

    /*@Query("SELECT r FROM TPagosEntity r " +
            "WHERE (:estado is NULL OR r.estado = :estado) " +
            "AND (:canalPago is NULL OR r.canalPago = :canalPago) " +
            "AND (:ticket IS NULL OR r.numeroTicket LIKE %:ticket%) " +
            "AND (:montoTotalPagado IS NULL OR r.montoTotalPagado = :montoTotalPagado) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaPago >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaPago <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TPagosEntity> searchPagos(Integer estado, Integer canalPago, String ticket, Double montoTotalPagado, Instant fechaDesde, Instant fechaHasta, Pageable pageable);*/

    // --- CONSULTA DE BÚSQUEDA MEJORADA ---
    @Query("SELECT DISTINCT p FROM TPagosEntity p " +
           "LEFT JOIN p.detalles pd " +
           "LEFT JOIN pd.cronogramaPagosEntity cp " +
           "LEFT JOIN cp.matriculasEntity m " +
           "LEFT JOIN m.estudiantesEntity e " +
           "WHERE (:estado IS NULL OR p.estado = :estado) " +
           "AND (:canalPago IS NULL OR p.canalPago = :canalPago) " +
           "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR p.fechaPago >= :fechaDesde) " +
           "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR p.fechaPago <= :fechaHasta) " +
           "AND (:descripcion IS NULL " +
           "OR p.numeroTicket LIKE %:descripcion% " +
           "OR e.nombre LIKE %:descripcion% " +
           "OR e.apellidoPaterno LIKE %:descripcion% " +
           "OR e.apellidoMaterno LIKE %:descripcion% " +
           "OR e.dni LIKE %:descripcion%) " +
           "ORDER BY p.id DESC")
    Page<TPagosEntity> searchPagos(
            @Param("estado") Integer estado,
            @Param("canalPago") Integer canalPago,
            @Param("descripcion") String descripcion,
            @Param("fechaDesde") Instant fechaDesde,
            @Param("fechaHasta") Instant fechaHasta,
            Pageable pageable);

    @Query("SELECT new pe.villaesperanza.SpringWebMatriculas.dto.report.PagosPorPeriodosDto(" +
            "r.numeroTicket, r.fechaPago, r.montoTotalPagado, r.canalPago, " +
            "r.bancosEntity.descripcion, " +
            "CONCAT(r.usuariosEntity.nombres, ' ', r.usuariosEntity.apellidos)) " +
            "FROM TPagosEntity r " +
            "WHERE (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaPago >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaPago <= :fechaHasta)")
    List<PagosPorPeriodosDto> pagosPorPeriodos(@Param("fechaDesde") Instant fechaDesde, @Param("fechaHasta") Instant fechaHasta);

    // --- MÉTODO PARA CORRELATIVO CORREGIDO (ORDENADO POR ID) ---
    Optional<TPagosEntity> findFirstByCanalPagoOrderByIdDesc(Integer canalPago);
}