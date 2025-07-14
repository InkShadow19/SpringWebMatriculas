package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.dto.report.PorMorosidadDto;
import pe.villaesperanza.SpringWebMatriculas.entity.TCronogramaPagosEntity;

import java.util.List;

@Repository
public interface CronogramaRepository extends JpaRepository<TCronogramaPagosEntity, Long> {

    @Query("SELECT r FROM TCronogramaPagosEntity r " +
            "WHERE r.matriculasEntity.estudiantesEntity.identifier = :estudiante " +
            "ORDER BY r.fechaCreacion DESC")
    List<TCronogramaPagosEntity> estadoCuenta(String estudiante);

    @Query("SELECT new pe.villaesperanza.SpringWebMatriculas.dto.report.PorMorosidadDto(" +
            "CONCAT(r.matriculasEntity.estudiantesEntity.nombre, ' ', r.matriculasEntity.estudiantesEntity.apellidoPaterno, ' ', r.matriculasEntity.estudiantesEntity.apellidoMaterno), " +
            "CONCAT(r.matriculasEntity.apoderadosEntity.nombre, ' ', r.matriculasEntity.apoderadosEntity.apellidoPaterno, ' ', r.matriculasEntity.apoderadosEntity.apellidoMaterno), " +
            "r.matriculasEntity.apoderadosEntity.telefono, r.descripcion, r.fechaVencimiento, r.montoAPagar) " +
            "FROM TCronogramaPagosEntity r " +
            "WHERE (:nivel IS NULL OR r.matriculasEntity.nivelesEntity.identifier = :nivel) " +
            "AND (:grado IS NULL OR r.matriculasEntity.gradosEntity.identifier = :grado)")
    List<PorMorosidadDto> porMorosidad(String nivel, String grado);
}
