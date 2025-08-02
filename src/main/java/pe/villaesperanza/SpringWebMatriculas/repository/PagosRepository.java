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

    // --- CONSULTA DE BÚSQUEDA MEJORADA ---
    @Query("SELECT DISTINCT p FROM TPagosEntity p " +
           "LEFT JOIN p.detalles pd " +
           "LEFT JOIN pd.cronogramaPagosEntity cp " +
           "LEFT JOIN cp.matriculasEntity m " +
           "LEFT JOIN m.estudiantesEntity e " +
           "WHERE (:estado IS NULL OR p.estado = :estado) " +
           "AND (:canalPago IS NULL OR p.canalPago = :canalPago) " +
           "AND (:anioId IS NULL OR m.aniosAcademicosEntity.identifier = :anioId) " +
           "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR p.fechaPago >= :fechaDesde) " +
           "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR p.fechaPago <= :fechaHasta) " +
           "AND (:descripcion IS NULL " +
           "OR p.numeroTicket LIKE %:descripcion% " +
           "OR e.nombre LIKE %:descripcion% " +
           "OR e.apellidoPaterno LIKE %:descripcion% " +
           "OR e.apellidoMaterno LIKE %:descripcion% " +
           "OR e.dni LIKE %:descripcion%) " +
           "ORDER BY p.id DESC")
    Page<TPagosEntity> searchPagos(@Param("estado") Integer estado, @Param("canalPago") Integer canalPago, @Param("anioId") String anioId, @Param("descripcion") String descripcion, @Param("fechaDesde") Instant fechaDesde, @Param("fechaHasta") Instant fechaHasta, Pageable pageable);

    // --- CONSULTA DEL REPORTE MEJORADA ---
    @Query("SELECT new pe.villaesperanza.SpringWebMatriculas.dto.report.PagosPorPeriodosDto(" +
            "p.numeroTicket, p.fechaPago, " +
            "CONCAT(e.nombre, ' ', e.apellidoPaterno, ' ', e.apellidoMaterno), " +
            "p.montoTotalPagado, " +
            "CASE WHEN p.canalPago = 20 THEN 'Caja' ELSE CONCAT('Banco - ', b.descripcion) END, " +
            "CONCAT(u.nombres, ' ', u.apellidos)) " +
            "FROM TPagosEntity p " +
            "JOIN p.usuariosEntity u " +
            "LEFT JOIN p.bancosEntity b " +
            "LEFT JOIN p.detalles pd " +
            "LEFT JOIN pd.cronogramaPagosEntity c " +
            "LEFT JOIN c.matriculasEntity m " +
            "LEFT JOIN m.estudiantesEntity e " +
            "WHERE p.estado = 10 " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR p.fechaPago >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR p.fechaPago <= :fechaHasta) " +
            "GROUP BY p.id, e.id, u.id, b.id")
    List<PagosPorPeriodosDto> pagosPorPeriodos(@Param("fechaDesde") Instant fechaDesde, @Param("fechaHasta") Instant fechaHasta);

    // --- MÉTODO PARA CORRELATIVO CORREGIDO (ORDENADO POR ID) ---
    Optional<TPagosEntity> findFirstByCanalPagoOrderByIdDesc(Integer canalPago);
}