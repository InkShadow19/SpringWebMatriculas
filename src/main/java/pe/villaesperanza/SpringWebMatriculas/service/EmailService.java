package pe.villaesperanza.SpringWebMatriculas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetNotification(String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        // Cambia este correo por el del administrador que recibirá la notificación
        message.setTo("ivantexsebastian@gmail.com"); 
        message.setSubject("Solicitud de Restablecimiento de Contraseña");
        message.setText("El usuario '" + username + "' ha solicitado un restablecimiento de su contraseña.\n\n"
                      + "Por favor, contacta al usuario y asígnale una nueva contraseña desde el módulo de gestión de usuarios.");
        
        // El correo desde el que se envía (debe ser el mismo que configuraste en application.properties)
        message.setFrom("ivantexsilva@gmail.com");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Manejar la excepción (ej. loggear el error)
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}