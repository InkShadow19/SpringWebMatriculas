package pe.villaesperanza.SpringWebMatriculas.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.villaesperanza.SpringWebMatriculas.repository.UsuariosRepository;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UsuariosRepository usuariosRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        // Le decimos a Spring que use nuestro repositorio para buscar usuarios.
        // Aquí personalizamos el mensaje si el usuario no se encuentra.
        return username -> usuariosRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Este usuario no existe en la base de datos."));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usamos BCrypt, el estándar para hashear contraseñas.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}