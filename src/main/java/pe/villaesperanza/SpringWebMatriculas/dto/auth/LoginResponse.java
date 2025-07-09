package pe.villaesperanza.SpringWebMatriculas.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private Object userInfo; // Para enviar datos del usuario si lo necesitas
}