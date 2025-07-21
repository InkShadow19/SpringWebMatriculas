package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.villaesperanza.SpringWebMatriculas.dto.UsuariosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.entity.TRolesEntity;
import pe.villaesperanza.SpringWebMatriculas.entity.TUsuariosEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.RolesRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.UsuariosRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public UsuariosDto registrarUsuario(UsuariosDto usuariosDto) {
        TRolesEntity rol = rolesRepository.findByIdentifier(usuariosDto.getRol())
                .orElseThrow(() -> new AppException("El rol especificado no existe"));

        TUsuariosEntity nuevoUsuario = new TUsuariosEntity();
        nuevoUsuario.setIdentifier(UUID.randomUUID().toString());
        nuevoUsuario.setUsuario(usuariosDto.getUsuario());
        nuevoUsuario.setNombres(usuariosDto.getNombres());
        nuevoUsuario.setApellidos(usuariosDto.getApellidos());
        nuevoUsuario.setDni(usuariosDto.getDni());
        nuevoUsuario.setFechaNacimiento(Instant.parse(usuariosDto.getFechaNacimiento() + "T00:00:00Z"));
        nuevoUsuario.setRolesEntity(rol);
        
        // ¡PUNTO CLAVE! Hashear la contraseña antes de guardarla.
        // Se asume que la contraseña viene en el DTO al registrar.
        if (usuariosDto.getContrasena() == null || usuariosDto.getContrasena().isEmpty()) {
            throw new AppException("La contraseña es obligatoria para registrar un nuevo usuario.");
        }
        nuevoUsuario.setContrasena(passwordEncoder.encode(usuariosDto.getContrasena()));

        TUsuariosEntity usuarioGuardado = usuariosRepository.save(nuevoUsuario);
        return usuarioGuardado.toDto();
    }

    // --- NUEVOS MÉTODOS AÑADIDOS ---

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Page<UsuariosDto> getSearch(int page, int size, String descripcion, EstadoReference estado) {
        Pageable pageable = PageRequest.of(page, size);
        Integer estadoValue = (estado != null) ? estado.getValue() : null;
        Page<TUsuariosEntity> pageList = usuariosRepository.searchUsuarios(descripcion, estadoValue, pageable);
        return pageList.map(TUsuariosEntity::toDto);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<UsuariosDto> get(String identifier) {
        return usuariosRepository.findByIdentifier(identifier).map(TUsuariosEntity::toDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public UsuariosDto update(String identifier, UsuariosDto usuariosDto) {
        TUsuariosEntity entity = usuariosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este usuario no existe"));

        // Actualizar campos (la contraseña NO se actualiza aquí por seguridad)
        entity.setNombres(usuariosDto.getNombres());
        entity.setApellidos(usuariosDto.getApellidos());
        entity.setDni(usuariosDto.getDni());
        
        if (usuariosDto.getFechaNacimiento() != null) {
            String fecha = usuariosDto.getFechaNacimiento();
            if (!fecha.contains("T")) {
                entity.setFechaNacimiento(Instant.parse(fecha + "T00:00:00Z"));
            } else {
                entity.setFechaNacimiento(Instant.parse(fecha));
            }
        }
        
        if (usuariosDto.getEstado() != null) {
            entity.setEstado(usuariosDto.getEstado().getValue());
        }

        // Actualizar rol si se proporciona uno nuevo
        if (usuariosDto.getRol() != null && !usuariosDto.getRol().equals(entity.getRolesEntity().getIdentifier())) {
            TRolesEntity nuevoRol = rolesRepository.findByIdentifier(usuariosDto.getRol())
                    .orElseThrow(() -> new AppException("El nuevo rol especificado no existe"));
            entity.setRolesEntity(nuevoRol);
        }

        TUsuariosEntity result = usuariosRepository.save(entity);
        return result.toDto();
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String identifier, String newPassword) {
        TUsuariosEntity entity = usuariosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El usuario no fue encontrado para restablecer la contraseña."));

        // Hashear y guardar la nueva contraseña
        entity.setContrasena(passwordEncoder.encode(newPassword));
        usuariosRepository.save(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class, IOException.class })
    public void delete(String identifier) {
        TUsuariosEntity entity = usuariosRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new AppException("El identifier de este usuario no existe para eliminar"));
        usuariosRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public void handleForgotPassword(String username) {
        // Busca al usuario. Si no lo encuentra, lanza una excepción con un mensaje claro.
        TUsuariosEntity user = usuariosRepository.findByUsuario(username)
            .orElseThrow(() -> new AppException("El nombre de usuario ingresado no se encuentra registrado."));

        // Si el usuario sí existe, envía la notificación como antes.
        emailService.sendPasswordResetNotification(user.getUsername());
    }
}