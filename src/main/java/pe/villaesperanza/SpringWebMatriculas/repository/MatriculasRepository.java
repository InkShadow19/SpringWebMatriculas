package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.dto.report.AlumnoPorGradoDto;
import pe.villaesperanza.SpringWebMatriculas.entity.TMatriculasEntity;

//import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculasRepository extends JpaRepository<TMatriculasEntity, Long> {

    Optional<TMatriculasEntity> findByIdentifier(String identifier);

   /*@Query("SELECT r FROM TMatriculasEntity r " +
            "WHERE (:codigo IS NULL OR r.codigo LIKE %:codigo%) " +
            "AND (:procedencia IS NULL OR r.procedencia LIKE %:procedencia%) " +
            "AND (:estado is NULL OR r.estado = :estado) " +
            "AND (:situacion is NULL OR r.situacion = :situacion) " +
            "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaMatricula >= :fechaDesde) " +
            "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaMatricula <= :fechaHasta) " +
            "ORDER BY r.fechaCreacion DESC")
    Page<TMatriculasEntity> searchMatriculas(String codigo, String procedencia, Integer estado, Integer situacion, Instant fechaDesde, Instant fechaHasta, Pageable pageable);*/

    // --- CONSULTA DE BÚSQUEDA CORREGIDA Y COMPLETADA ---
    @Query("SELECT r FROM TMatriculasEntity r " +
           "WHERE (:descripcion IS NULL OR r.codigo LIKE %:descripcion% " +
           "OR r.estudiantesEntity.dni LIKE %:descripcion% " +
           "OR r.estudiantesEntity.nombre LIKE %:descripcion% " +
           "OR r.estudiantesEntity.apellidoPaterno LIKE %:descripcion% " +
           "OR r.estudiantesEntity.apellidoMaterno LIKE %:descripcion%) " +
           "AND (:estado IS NULL OR r.estado = :estado) " +
           "AND (:anioId IS NULL OR r.aniosAcademicosEntity.identifier = :anioId) " +
           "AND (:nivelId IS NULL OR r.nivelesEntity.identifier = :nivelId) " +
           "AND (:gradoId IS NULL OR r.gradosEntity.identifier = :gradoId) " +
           "ORDER BY r.fechaCreacion DESC")
    Page<TMatriculasEntity> searchMatriculas(
            @Param("descripcion") String descripcion,
            @Param("estado") Integer estado,
            @Param("anioId") String anioId,
            @Param("nivelId") String nivelId,
            @Param("gradoId") String gradoId,
            Pageable pageable);

    @Query("SELECT new pe.villaesperanza.SpringWebMatriculas.dto.report.AlumnoPorGradoDto(" +
            "r.estudiantesEntity.dni, " +
            "CONCAT(r.estudiantesEntity.nombre, ' ', r.estudiantesEntity.apellidoPaterno, ' ', r.estudiantesEntity.apellidoMaterno), " +
            "CONCAT(r.apoderadosEntity.nombre, ' ', r.apoderadosEntity.apellidoPaterno, ' ', r.apoderadosEntity.apellidoMaterno), " +
            "r.apoderadosEntity.telefono, r.fechaMatricula, r.situacion) " +
            "FROM TMatriculasEntity r WHERE r.aniosAcademicosEntity.anio = :anio " +
            "AND (:nivel IS NULL OR r.nivelesEntity.identifier = :nivel) " +
            "AND (:grado IS NULL OR r.gradosEntity.identifier = :grado)")
    List<AlumnoPorGradoDto> alumnoPorGrado(Integer anio, String nivel, String grado);

    // Busca todas las matrículas de un año académico específico y las ordena por el código de forma descendente
    List<TMatriculasEntity> findByAniosAcademicosEntity_AnioOrderByCodigoDesc(Integer anio);
}
