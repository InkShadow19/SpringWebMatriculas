package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TUsuariosEntity;
import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<TUsuariosEntity, Long> {

    // Método crucial para buscar un usuario por su nombre de usuario.
    Optional<TUsuariosEntity> findByUsuario(String usuario);
}