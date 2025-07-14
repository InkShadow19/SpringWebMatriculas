package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TCronogramaPagosEntity;

import java.util.List;

@Repository
public interface CronogramaRepository extends JpaRepository<TCronogramaPagosEntity, Long> {

    @Query("SELECT r FROM TCronogramaPagosEntity r " +
            "WHERE r.matriculasEntity.estudiantesEntity.identifier = :estudiante " +
            "ORDER BY r.fechaCreacion DESC")
    List<TCronogramaPagosEntity> estadoCuenta(String estudiante);
}
