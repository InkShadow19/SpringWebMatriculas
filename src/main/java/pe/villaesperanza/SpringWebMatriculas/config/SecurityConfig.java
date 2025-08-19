package pe.villaesperanza.SpringWebMatriculas.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pe.villaesperanza.SpringWebMatriculas.config.jwt.JwtAuthFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;  // Inyectamos nuestro filtro JWT

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/debug/**").permitAll()

                // REGLA 1 (LA MÁS ESPECÍFICA): Permisos de LECTURA (GET) para Secretaria y Admin.
                // Esto debe ir primero para que tenga prioridad.
                .requestMatchers(HttpMethod.GET,
                    "/usuarios/**",
                    "/roles/**",
                    "/anios/academicos/**", 
                    "/niveles/**", 
                    "/grados/**", 
                    "/conceptos/pago/**",
                    "/bancos/**"
                ).hasAnyAuthority("Administrador", "Secretaria")

                // REGLA 2: Permisos completos para los módulos operativos que ambos roles gestionan.
                .requestMatchers(
                    "/dashboard/**",
                    "/estudiantes/**", 
                    "/apoderados/**", 
                    "/matriculas/**", 
                    "/pagos/**",
                    "/report/**"
                ).hasAnyAuthority("Administrador", "Secretaria")

                // REGLA 3 (LA MÁS GENERAL): Permisos exclusivos para el Administrador.
                // Como la regla GET ya fue evaluada, esta regla aplicará para POST, PATCH, DELETE, etc.
                .requestMatchers(
                    "/usuarios/**", 
                    "/roles/**",
                    "/anios/academicos/**", 
                    "/niveles/**", 
                    "/grados/**", 
                    "/conceptos/pago/**", 
                    "/bancos/**"
                ).hasAuthority("Administrador")
                
                // REGLA 4: Cualquier otra petición requiere autenticación.
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean para configurar CORS, esencial para conectar con Angular.
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite que tu frontend de Angular se conecte
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "company"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}