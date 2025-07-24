package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TConceptosPagoEntity;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface ConceptosPagoRepository extends JpaRepository<TConceptosPagoEntity, Long> {

    Optional<TConceptosPagoEntity> findByIdentifier(String identifier);

    Optional<TConceptosPagoEntity> findByCodigo(String codigo);
    Optional<TConceptosPagoEntity> findByDescripcion(String descripcion);

    @Query("SELECT r FROM TConceptosPagoEntity r " +
            "WHERE (:codigo IS NULL OR r.codigo LIKE %:codigo%) " +
            "AND (:descripcion IS NULL OR r.descripcion LIKE %:descripcion%) " +
            "AND (:estado is NULL OR r.estado = :estado) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaCreacion >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaCreacion <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TConceptosPagoEntity> searchConceptos(String codigo, String descripcion, Integer estado, Instant fechaDesde, Instant fechaHasta, Pageable pageable);
}