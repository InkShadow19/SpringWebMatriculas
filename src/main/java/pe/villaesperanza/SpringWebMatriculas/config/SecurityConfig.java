package pe.villaesperanza.SpringWebMatriculas.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilitar CORS
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // ENDPOINT PÚBLICO: Solo el login es público.
                .requestMatchers("/auth/login",
                                             "/apoderados/**", 
                                             "/estudiantes/**",
                                             "/anios/academicos/**",
                                             "/bancos/**",
                                             "/conceptos/pago**",
                                             "/grados/**",
                                             "/niveles/**",
                                             "/roles/**").permitAll()

                // RUTAS SOLO PARA ADMINISTRADOR:
                .requestMatchers("/usuarios/**", "/roles/**", "/anios/academicos/**", "/niveles/**", "/grados/**", "/conceptos/pago/**", "/bancos/**").hasAuthority("Administrador")

                // CUALQUIER OTRA RUTA: Requiere estar autenticado (sirve para ambos roles).
                .anyRequest().authenticated()
            )
            // Endpoint para el logout, Spring lo gestiona.
            .logout(logout -> logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessHandler((request, response, authentication) -> response.setStatus(200))
            )
            .authenticationProvider(authenticationProvider);

        return http.build();
    }

    // Bean para configurar CORS, esencial para conectar con Angular.
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite que tu frontend de Angular se conecte
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true); // ¡Para las sesiones!
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}