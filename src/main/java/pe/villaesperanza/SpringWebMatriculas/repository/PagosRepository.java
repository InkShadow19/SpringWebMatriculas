package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.villaesperanza.SpringWebMatriculas.dto.report.IngresosVsDeudaProjection;
import pe.villaesperanza.SpringWebMatriculas.dto.report.PagosPorPeriodosDto;
import pe.villaesperanza.SpringWebMatriculas.entity.TPagosEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagosRepository extends JpaRepository<TPagosEntity, Long> {

    Optional<TPagosEntity> findByIdentifier(String identifier);

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

    Optional<TPagosEntity> findFirstByCanalPagoOrderByIdDesc(Integer canalPago);

    // --- MÉTODOS PARA DASHBOARD ---

    // KPI 2: Ingresos del Mes Calendario Actual
    @Query("SELECT COALESCE(SUM(p.montoTotalPagado), 0.0) FROM TPagosEntity p WHERE p.estado = 10 AND p.fechaPago >= :inicioMes AND p.fechaPago < :inicioMesSiguiente")
    double sumIngresosDelMes(@Param("inicioMes") Instant inicioMes, @Param("inicioMesSiguiente") Instant inicioMesSiguiente);

    // KPI Contextual (Análisis Anual): Total de Ingresos del Año por Fecha de Pago
    @Query("SELECT COALESCE(SUM(p.montoTotalPagado), 0.0) FROM TPagosEntity p " +
           "WHERE p.estado = 10 " +
           "AND p.fechaPago >= :inicioAnio AND p.fechaPago < :finAnio")
    double sumIngresosDelAnioPorFechaPago(@Param("inicioAnio") Instant inicioAnio, @Param("finAnio") Instant finAnio);

    // Gráfico 3 (Análisis Anual): Ingresos vs. Deuda Vencida
    @Query(value = "SELECT " +
                   "    mes.num AS mes, " +
                   "    COALESCE(ingresos.total, 0.0) AS ingresos, " +
                   "    COALESCE(deudas.total, 0.0) AS deudaVencida " +
                   "FROM " +
                   "    (SELECT 1 AS num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL " +
                   "     SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL " +
                   "     SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL " +
                   "     SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12) AS mes " +
                   "LEFT JOIN " +
                   "    (SELECT MONTH(p.fecha_pago) AS mes_num, SUM(p.monto_total_pagado) AS total " +
                   "     FROM pagos p " +
                   "     WHERE p.estado = 10 " +
                   "     AND YEAR(p.fecha_pago) = (SELECT anio FROM anios_academicos WHERE identifier = :anioId) " +
                   "     GROUP BY mes_num) AS ingresos ON mes.num = ingresos.mes_num " +
                   "LEFT JOIN " +
                   "    (SELECT MONTH(cp.fecha_vencimiento) AS mes_num, SUM(cp.monto_a_pagar + 10.00) AS total " +
                   "     FROM cronograma_pagos cp JOIN matriculas m ON cp.id_matriculas = m.id " +
                   "     WHERE m.id_anios_academicos = (SELECT id FROM anios_academicos WHERE identifier = :anioId) " +
                   "     AND cp.estado_deuda = 10 AND cp.fecha_vencimiento < :fechaLimite " +
                   "     GROUP BY mes_num) AS deudas ON mes.num = deudas.mes_num " +
                   "ORDER BY mes.num",
           nativeQuery = true)
    List<IngresosVsDeudaProjection> getIngresosVsDeudaPorAnioNativo(@Param("anioId") String anioId, @Param("fechaLimite") Instant fechaLimite);
}