package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.villaesperanza.SpringWebMatriculas.dto.UsuariosDto;
import pe.villaesperanza.SpringWebMatriculas.entity.TRolesEntity;
import pe.villaesperanza.SpringWebMatriculas.entity.TUsuariosEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.RolesRepository;
import pe.villaesperanza.SpringWebMatriculas.repository.UsuariosRepository;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

@Service
@RequiredArgsConstructor
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder; // Inyectado para hashear

    public UsuariosDto registrarUsuario(UsuariosDto usuariosDto) {
        // Busca el rol para asignarlo. Asegúrate de que el rol exista.
        TRolesEntity rol = rolesRepository.findByIdentifier(usuariosDto.getRol())
                .orElseThrow(() -> new AppException("El rol especificado no existe"));

        TUsuariosEntity nuevoUsuario = new TUsuariosEntity();
        nuevoUsuario.setIdentifier(java.util.UUID.randomUUID().toString());
        nuevoUsuario.setUsuario(usuariosDto.getUsuario());
        nuevoUsuario.setNombres(usuariosDto.getNombres());
        nuevoUsuario.setApellidos(usuariosDto.getApellidos());
        nuevoUsuario.setDni(usuariosDto.getDni());
        nuevoUsuario.setFechaNacimiento(java.time.Instant.parse(usuariosDto.getFechaNacimiento()));
        nuevoUsuario.setRolesEntity(rol);

        // ¡PUNTO CLAVE! Hashear la contraseña antes de guardarla.
        nuevoUsuario.setContraseña(passwordEncoder.encode(usuariosDto.getContraseña()));

        TUsuariosEntity usuarioGuardado = usuariosRepository.save(nuevoUsuario);
        return usuarioGuardado.toDto();
    }
}