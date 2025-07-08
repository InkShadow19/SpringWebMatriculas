package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TNivelesEntity;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface NivelesRepository extends JpaRepository<TNivelesEntity, Long> {

    Optional<TNivelesEntity> findByIdentifier(String identifier);

    @Query("SELECT r FROM TNivelesEntity r " +
            "WHERE (:descripcion IS NULL OR r.description LIKE %:descripcion%) " +
            "AND (:estado is NULL OR r.estado = :estado) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaCreacion >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaCreacion <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TNivelesEntity> searchNiveles(String descripcion, Integer estado, Instant fechaDesde, Instant fechaHasta, Pageable pageable);

}
