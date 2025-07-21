package pe.villaesperanza.SpringWebMatriculas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.villaesperanza.SpringWebMatriculas.entity.TUsuariosEntity;

//import java.time.Instant;
import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<TUsuariosEntity, Long> {

    // Método crucial para buscar un usuario por su nombre de usuario.
    Optional<TUsuariosEntity> findByUsuario(String usuario);
    Optional<TUsuariosEntity> findByIdentifier(String identifier);

    @Query("SELECT u FROM TUsuariosEntity u " +
           "WHERE (:descripcion IS NULL " +
           "OR u.dni LIKE %:descripcion% " +
           "OR u.nombres LIKE %:descripcion% " +
           "OR u.apellidos LIKE %:descripcion% " +
           "OR u.usuario LIKE %:descripcion%) " +
           "AND (:estado IS NULL OR u.estado = :estado) " +
           "ORDER BY u.fechaCreacion DESC")
    Page<TUsuariosEntity> searchUsuarios(String descripcion, Integer estado, Pageable pageable);
}