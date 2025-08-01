package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.villaesperanza.SpringWebMatriculas.config.jwt.JwtService;
import pe.villaesperanza.SpringWebMatriculas.dto.UsuariosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.auth.LoginRequest;
import pe.villaesperanza.SpringWebMatriculas.dto.auth.LoginResponse;
import pe.villaesperanza.SpringWebMatriculas.entity.TUsuariosEntity;
import pe.villaesperanza.SpringWebMatriculas.repository.UsuariosRepository;
import pe.villaesperanza.SpringWebMatriculas.service.UsuariosService;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UsuariosService usuariosService;
    private final JwtService jwtService;
    private final UsuariosRepository usuariosRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            TUsuariosEntity user = usuariosRepository.findByUsuario(loginRequest.getUsuario())
                .orElseThrow(() -> new AppException("Usuario o contraseña incorrectos."));

            if (!user.getUsername().equals(loginRequest.getUsuario())) {
                throw new AppException("Usuario o contraseña incorrectos.");
            }

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsuario(),
                    loginRequest.getContrasena()
                )
            );

            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRolesEntity().getDescripcion())
                .build());

        } catch (DisabledException e) {
            // Captura específicamente el error de cuenta deshabilitada
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Su usuario se encuentra inactivo o ha sido dado de baja.");
        } catch (Exception e) {
            // Captura todos los demás errores (contraseña incorrecta, etc.)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos.");
        }
    }

    @PostMapping("/registrar")
    public UsuariosDto registrarUsuario(@RequestBody UsuariosDto usuariosDto) {
        return usuariosService.registrarUsuario(usuariosDto);
    }

    // --- ENDPOINT MODIFICADO ---
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            if (username == null || username.trim().isEmpty()) {
                // Devolvemos un error 400 si no se envía el nombre de usuario
                return ResponseEntity.badRequest().body(Map.of("error", "El nombre de usuario es obligatorio."));
            }
            usuariosService.handleForgotPassword(username);
            // Si todo va bien, devolvemos una respuesta 200 OK con un mensaje de éxito
            return ResponseEntity.ok(Map.of("message", "Solicitud procesada exitosamente."));
        } catch (AppException e) {
            // Si el servicio lanza la excepción (usuario no encontrado), la capturamos
            // y devolvemos un error 404 con el mensaje de la excepción.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}