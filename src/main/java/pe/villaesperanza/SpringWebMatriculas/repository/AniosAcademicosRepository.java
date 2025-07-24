package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TAniosAcademicosEntity;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface AniosAcademicosRepository extends JpaRepository<TAniosAcademicosEntity, Long> {

    Optional<TAniosAcademicosEntity> findByIdentifier(String identifier);
    
    Optional<TAniosAcademicosEntity> findByAnio(Integer anio);

    // --- MÉTODO AÑADIDO PARA BUSCAR POR ESTADO ---
    Optional<TAniosAcademicosEntity> findByEstadoAcademico(Integer estado);

    @Query("SELECT r FROM TAniosAcademicosEntity r " +
            "WHERE (:anio IS NULL OR r.anio = :anio) " +
            "AND (:estadoA is NULL OR r.estadoAcademico = :estadoA) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaCreacion >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaCreacion <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TAniosAcademicosEntity> searchAcademicos(Integer anio, Integer estadoA, Instant fechaDesde, Instant fechaHasta, Pageable pageable);
}
