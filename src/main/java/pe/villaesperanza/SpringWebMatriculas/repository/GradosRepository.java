package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TGradosEntity;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface GradosRepository extends JpaRepository<TGradosEntity, Long> {

    Optional<TGradosEntity> findByIdentifier(String identifier);

    @Query("SELECT r FROM TGradosEntity r " +
            "WHERE (:descripcion IS NULL OR r.descripcion LIKE %:descripcion%) " +
            "AND (:estado is NULL OR r.estado = :estado) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaCreacion >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaCreacion <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TGradosEntity> searchGrados(String descripcion, Integer estado, Instant fechaDesde, Instant fechaHasta, Pageable pageable);
}
