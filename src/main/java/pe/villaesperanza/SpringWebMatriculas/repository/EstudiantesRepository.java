package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TEstudiantesEntity;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EstudiantesRepository extends JpaRepository<TEstudiantesEntity, Long> {

    Optional<TEstudiantesEntity> findByIdentifier(String identifier);

    @Query("SELECT r FROM TEstudiantesEntity r " +
            "WHERE (:descripcion IS NULL OR r.name LIKE %:descripcion% OR r.dni LIKE %:descripcion%) " +
            "AND (:genero is NULL OR r.genero = :genero) " +
            "AND (:estado is NULL OR r.estado = :estado) " +
            "AND (:estadoAcademico is NULL OR r.estadoAcademico = :estadoAcademico) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaNacimiento >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaNacimiento <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TEstudiantesEntity> searchEstudiantes(String descripcion, Integer genero, Integer estado, Integer estadoAcademico, Instant fechaDesde, Instant fechaHasta, Pageable pageable);

}