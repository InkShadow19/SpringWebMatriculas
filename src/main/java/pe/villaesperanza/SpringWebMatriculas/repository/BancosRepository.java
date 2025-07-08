package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TBancosEntity;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface BancosRepository extends JpaRepository<TBancosEntity, Long> {

    Optional<TBancosEntity> findByIdentifier(String identifier);

    @Query("SELECT r FROM TBancosEntity r " +
            "WHERE (:codigo IS NULL OR r.codigo LIKE %:codigo%) " +
            "WHERE (:descripcion IS NULL OR r.description LIKE %:descripcion%) " +
            "AND (:estado is NULL OR r.estado = :estado) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaCreacion >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaCreacion <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TBancosEntity> searchBancos(String codigo, String descripcion, Integer estado, Instant fechaDesde, Instant fechaHasta, Pageable pageable);

}
