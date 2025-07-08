package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.villaesperanza.SpringWebMatriculas.entity.TApoderadosEntity;

import java.time.Instant;
import java.util.Optional;

public interface ApoderadosRepository extends JpaRepository<TApoderadosEntity, Long> {

    Optional<TApoderadosEntity> findByIdentifier(String identifier);

    @Query("SELECT r FROM TApoderadosEntity r " +
            "WHERE (:descripcion IS NULL OR r.name LIKE %:descripcion% OR r.dni LIKE %:descripcion%) " +
            "AND (:genero is NULL OR r.genero = :genero) " +
            "AND (:estado is NULL OR r.estado = :estado) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaNacimiento >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaNacimiento <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TApoderadosEntity> searchApoderados(String descripcion, Integer genero, Integer estado, Instant fechaDesde, Instant fechaHasta, Pageable pageable);

}