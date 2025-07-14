package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.dto.report.AlumnoPorGradoDto;
import pe.villaesperanza.SpringWebMatriculas.entity.TMatriculasEntity;

import java.time.Instant;
import java.util.List;
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

    @Query("SELECT new pe.villaesperanza.SpringWebMatriculas.dto.report.AlumnoPorGradoDto(" +
            "r.estudiantesEntity.dni, " +
            "CONCAT(r.estudiantesEntity.nombre, ' ', r.estudiantesEntity.apellidoPaterno, ' ', r.estudiantesEntity.apellidoMaterno), " +
            "CONCAT(r.apoderadosEntity.nombre, ' ', r.apoderadosEntity.apellidoPaterno, ' ', r.apoderadosEntity.apellidoMaterno), " +
            "r.apoderadosEntity.telefono, r.fechaMatricula, r.situacion) " +
            "FROM TMatriculasEntity r WHERE r.aniosAcademicosEntity.anio = :anio " +
            "AND (:nivel IS NULL OR r.nivelesEntity.identifier = :nivel) " +
            "AND (:grado IS NULL OR r.gradosEntity.identifier = :grado)")
    List<AlumnoPorGradoDto> alumnoPorGrado(Integer anio, String nivel, String grado);
}
