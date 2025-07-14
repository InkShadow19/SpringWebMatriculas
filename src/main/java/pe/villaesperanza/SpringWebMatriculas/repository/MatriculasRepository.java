package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TMatriculasEntity;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface MatriculasRepository extends JpaRepository<TMatriculasEntity, Long> {

    Optional<TMatriculasEntity> findByIdentifier(String identifier);

    @Query("SELECT r FROM TMatriculasEntity r " +
            "WHERE (:codigo IS NULL OR r.codigo LIKE %:codigo%) " +
            "AND (:procedencia IS NULL OR r.procedencia LIKE %:procedencia%) " +
            "AND (:estado is NULL OR r.estado = :estado) " +
            "AND (:situacion is NULL OR r.situacion = :situacion) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaMatricula >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaMatricula <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TMatriculasEntity> searchMatriculas(String codigo, String procedencia, Integer estado, Integer situacion, Instant fechaDesde, Instant fechaHasta, Pageable pageable);
}
