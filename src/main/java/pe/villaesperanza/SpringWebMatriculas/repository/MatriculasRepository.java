package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.dto.report.AlumnoPorGradoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.DistribucionGradoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.DistribucionNivelDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.SituacionAlumnoDto;
import pe.villaesperanza.SpringWebMatriculas.dto.report.TendenciaMatriculaProjection;
import pe.villaesperanza.SpringWebMatriculas.entity.TMatriculasEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculasRepository extends JpaRepository<TMatriculasEntity, Long> {

    Optional<TMatriculasEntity> findByIdentifier(String identifier);

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
           "AND (CAST(:fechaDesde AS TIMESTAMP) IS NULL OR r.fechaMatricula >= :fechaDesde) " +
           "AND (CAST(:fechaHasta AS TIMESTAMP) IS NULL OR r.fechaMatricula <= :fechaHasta) " +
           "ORDER BY r.id DESC")
    Page<TMatriculasEntity> searchMatriculas(
            @Param("descripcion") String descripcion,
            @Param("estado") Integer estado,
            @Param("anioId") String anioId,
            @Param("nivelId") String nivelId,
            @Param("gradoId") String gradoId,
            @Param("fechaDesde") Instant fechaDesde,
            @Param("fechaHasta") Instant fechaHasta,
            Pageable pageable);

    @Query("SELECT new pe.villaesperanza.SpringWebMatriculas.dto.report.AlumnoPorGradoDto(" +
            "r.estudiantesEntity.dni, " +
            "CONCAT(r.estudiantesEntity.nombre, ' ', r.estudiantesEntity.apellidoPaterno, ' ', r.estudiantesEntity.apellidoMaterno), " +
            "CONCAT(r.apoderadosEntity.nombre, ' ', r.apoderadosEntity.apellidoPaterno, ' ', r.apoderadosEntity.apellidoMaterno), " +
            "r.apoderadosEntity.telefono, r.fechaMatricula, r.situacion) " +
            "FROM TMatriculasEntity r " +
            "WHERE r.aniosAcademicosEntity.anio = :anio " +
            "AND r.estado IN (10, 30) " + 
            "AND (:nivel IS NULL OR r.nivelesEntity.identifier = :nivel) " +
            "AND (:grado IS NULL OR r.gradosEntity.identifier = :grado)")
    List<AlumnoPorGradoDto> alumnoPorGrado(Integer anio, String nivel, String grado);

    boolean existsByEstudiantesEntity_IdentifierAndAniosAcademicosEntity_IdentifierAndEstadoNotAndIdentifierNot(
        String estudianteId, String anioAcademicoId, Integer estado, String matriculaIdentifier
    );
    
    Optional<TMatriculasEntity> findTopByAniosAcademicosEntity_AnioOrderByIdDesc(Integer anio);

    // --- MÉTODOS PARA DASHBOARD ---
    
    // KPI 1: Total de Alumnos Matriculados en el Año ACTIVO
    @Query("SELECT COUNT(m) FROM TMatriculasEntity m JOIN m.aniosAcademicosEntity a WHERE m.estado IN (10, 30) AND a.estadoAcademico = 10")
    long countMatriculasActivasEnAnioActivo();

    // Gráfico 1 (Análisis Anual): Tendencia de Matrículas
    @Query(value = "SELECT MONTH(m.fecha_matricula) AS mes, COUNT(m.id) AS total " +
                   "FROM matriculas m JOIN anios_academicos a ON m.id_anios_academicos = a.id " +
                   "WHERE a.identifier = :anioId AND m.estado IN (10, 30) " +
                   "GROUP BY mes ORDER BY mes",
           nativeQuery = true)
    List<TendenciaMatriculaProjection> getTendenciaMatriculasPorAnioNativo(@Param("anioId") String anioId);

    // Gráfico 2 (Análisis Anual): Distribución por Nivel
    @Query("SELECT new pe.villaesperanza.SpringWebMatriculas.dto.report.DistribucionNivelDto(" +
           "n.descripcion, COUNT(m)) " +
           "FROM TMatriculasEntity m JOIN m.nivelesEntity n " +
           "WHERE m.aniosAcademicosEntity.identifier = :anioId AND m.estado IN (10, 30) " +
           "GROUP BY n.descripcion ORDER BY n.descripcion")
    List<DistribucionNivelDto> getDistribucionPorNivelPorAnio(@Param("anioId") String anioId);

    // Gráfico 3 (Análisis Anual): Distribución por Grado
    @Query("SELECT new pe.villaesperanza.SpringWebMatriculas.dto.report.DistribucionGradoDto(" +
           "g.descripcion, COUNT(m)) " +
           "FROM TMatriculasEntity m JOIN m.gradosEntity g " +
           "WHERE m.aniosAcademicosEntity.identifier = :anioId " +
           "AND m.estado IN (10, 30) " +
           "AND m.nivelesEntity.identifier = :nivelId " +
           "GROUP BY g.descripcion " +
           "ORDER BY g.descripcion ASC")
    List<DistribucionGradoDto> getDistribucionPorGradoPorAnioYNivel(@Param("anioId") String anioId, @Param("nivelId") String nivelId);

    // Gráfico 4 (Análisis Anual): Distribución por Situación del Alumno
    @Query("SELECT new pe.villaesperanza.SpringWebMatriculas.dto.report.SituacionAlumnoDto(" +
           "CASE " +
           "    WHEN m.situacion = 10 THEN 'Promovido' " +
           "    WHEN m.situacion = 20 THEN 'Ingresante' " +
           "    ELSE 'Repitente' " +
           "END, " +
           "COUNT(m)) " +
           "FROM TMatriculasEntity m " +
           "WHERE m.aniosAcademicosEntity.identifier = :anioId AND m.estado IN (10, 30) " +
           "GROUP BY m.situacion")
    List<SituacionAlumnoDto> getDistribucionSituacionPorAnio(@Param("anioId") String anioId);
}
