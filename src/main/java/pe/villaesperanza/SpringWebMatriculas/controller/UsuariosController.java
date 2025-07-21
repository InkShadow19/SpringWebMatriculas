package pe.villaesperanza.SpringWebMatriculas.controller;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.dto.UsuariosDto;
import pe.villaesperanza.SpringWebMatriculas.dto.reference.EstadoReference;
import pe.villaesperanza.SpringWebMatriculas.service.UsuariosService;
import pe.villaesperanza.SpringWebMatriculas.util.AppException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuariosService usuariosService;

    // Endpoint para registrar un nuevo usuario 
    /*@PostMapping("/registrar")
    public UsuariosDto registrarUsuario(@RequestBody UsuariosDto usuariosDto) {
        return usuariosService.registrarUsuario(usuariosDto);
    }*/
    
    // Endpoint para actualizar un usuario existente
    @PatchMapping("/{identifier}")
    public UsuariosDto update(@PathVariable(value = "identifier") String identifier, @RequestBody UsuariosDto usuariosDto) {
        return usuariosService.update(identifier, usuariosDto);
    }

    // Endpoint para restablecer la contraseña
    @PostMapping("/{identifier}/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(
            @PathVariable String identifier,
            @RequestBody Map<String, String> passwordMap) {
        String newPassword = passwordMap.get("newPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new AppException("La nueva contraseña no puede estar vacía.");
        }
        usuariosService.resetPassword(identifier, newPassword);
    }

    // Endpoint para obtener un usuario por su identifier
    @GetMapping("/{identifier}")
    public ResponseEntity<UsuariosDto> get(@PathVariable(value = "identifier") String identifier) {
        return usuariosService.get(identifier)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para buscar y paginar usuarios
    @GetMapping("/search")
    public Page<UsuariosDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) EstadoReference estado
    ) {
        return usuariosService.getSearch(page, size, descripcion, estado);
    }

    // Endpoint para eliminar un usuario
    @DeleteMapping("/{identifier}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "identifier") String identifier) {
        usuariosService.delete(identifier);
    }
}