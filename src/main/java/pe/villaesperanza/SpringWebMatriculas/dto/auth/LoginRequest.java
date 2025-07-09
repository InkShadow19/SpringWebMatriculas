package pe.villaesperanza.SpringWebMatriculas.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String usuario;
    private String contraseña;
}