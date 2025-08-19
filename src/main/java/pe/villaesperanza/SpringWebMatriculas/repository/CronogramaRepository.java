package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.villaesperanza.SpringWebMatriculas.dto.report.MorosidadAgrupadaDto;
import pe.villaesperanza.SpringWebMatriculas.entity.TCronogramaPagosEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CronogramaRepository extends JpaRepository<TCronogramaPagosEntity, Long> {

    @Query("SELECT r FROM TCronogramaPagosEntity r " +
           "JOIN r.matriculasEntity m " +
           "WHERE m.estudiantesEntity.identifier = :estudiante " +
           "AND m.aniosAcademicosEntity.anio = :anio " +
           "AND m.estado IN (10, 30) " +
           "ORDER BY r.fechaVencimiento ASC")
    List<TCronogramaPagosEntity> estadoCuenta(@Param("estudiante") String estudiante, @Param("anio") Integer anio);

    @Query("SELECT new pe.villaesperanza.SpringWebMatriculas.dto.report.MorosidadAgrupadaDto(" +
            "CONCAT(m.estudiantesEntity.nombre, ' ', m.estudiantesEntity.apellidoPaterno, ' ', m.estudiantesEntity.apellidoMaterno), " +
            "CONCAT(m.apoderadosEntity.nombre, ' ', m.apoderadosEntity.apellidoPaterno, ' ', m.apoderadosEntity.apellidoMaterno), " +
            "m.apoderadosEntity.telefono, " +
            "COUNT(r.id), " +
            "SUM(r.montoAPagar + 10.00)) " +
            "FROM TCronogramaPagosEntity r JOIN r.matriculasEntity m " +
            "WHERE r.estadoDeuda = 10 " +
            "AND r.fechaVencimiento < :fechaLimite " +
            "AND m.aniosAcademicosEntity.anio = :anio " +
            "AND (:nivel IS NULL OR m.nivelesEntity.identifier = :nivel) " +
            "AND (:grado IS NULL OR m.gradosEntity.identifier = :grado) " +
            "GROUP BY m.id, m.estudiantesEntity.id, m.apoderadosEntity.id " +
            "ORDER BY SUM(r.montoAPagar + 10.00) DESC")
    List<MorosidadAgrupadaDto> porMorosidadAgrupada(@Param("fechaLimite") Instant fechaLimite, @Param("anio") Integer anio, @Param("nivel") String nivel, @Param("grado") String grado);

    Optional<TCronogramaPagosEntity> findByIdentifier(String identifier);

    @Query("SELECT c FROM TCronogramaPagosEntity c " +
           "WHERE c.estadoDeuda = 10 " +
           "AND c.matriculasEntity.aniosAcademicosEntity.identifier = :anioId")
    List<TCronogramaPagosEntity> findAllPendientesByAnio(@Param("anioId") String anioId);
    
    // --- MÉTODOS PARA DASHBOARD ---

    // KPI 3: Total de Deuda Pendiente (Global)
    @Query("SELECT c FROM TCronogramaPagosEntity c WHERE c.estadoDeuda = 10")
    List<TCronogramaPagosEntity> findAllPendientesGlobal();

    // KPI 4: Total de Alumnos Morosos (Global)
    @Query("SELECT COUNT(DISTINCT c.matriculasEntity.estudiantesEntity.id) FROM TCronogramaPagosEntity c " +
           "WHERE c.estadoDeuda = 10 AND c.fechaVencimiento < :fechaLimite")
    long countAlumnosMorososGlobal(@Param("fechaLimite") Instant fechaLimite);
}
