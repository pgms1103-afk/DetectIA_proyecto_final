package co.edu.unbosque.detectia.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// --- NUEVAS IMPORTACIONES PARA CORS ---
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * Configuración central de Spring Security para la plataforma DetectIA.
 * <p>
 * Define la cadena de filtros de seguridad HTTP con política de sesión sin
 * estado (JWT stateless), las reglas de autorización por rol y ruta, y la
 * configuración CORS para permitir peticiones desde el frontend Angular.
 * Registra los beans de {@link PasswordEncoder} (BCrypt),
 * {@link AuthenticationProvider} (DAO) y {@link AuthenticationManager}.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see JwtAuthenticationFilter
 * @see JwtUtil
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/** Filtro de autenticación JWT que procesa los tokens en las solicitudes. */
	private final JwtAuthenticationFilter jwtAuthFilter;

	/** Servicio que carga los detalles del usuario para la autenticación. */
	private final UserDetailsService userDetailsService;

	/**
	 * Constructor que inicializa los componentes necesarios para la seguridad.
	 *
	 * @param jwtAuthFilter      Filtro para procesar tokens JWT
	 * @param userDetailsService Servicio para cargar detalles de usuarios
	 */
	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, @Qualifier("userDetailsServiceImpl")UserDetailsService userDetailsService) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Configura la cadena de filtros de seguridad HTTP. Define reglas de acceso,
	 * manejo de sesiones y filtros de autenticación.
	 *
	 * @param http Configuración de seguridad HTTP
	 * @return Cadena de filtros de seguridad configurada
	 * @throws Exception Si ocurre un error durante la configuración
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// 1. ACTIVAMOS CORS AQUÍ llamando al Bean que creamos más abajo
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> // Se autorizan las siguientes solicitudes
						auth.requestMatchers("/public/**") // Si la URL coincide con eso, se le permite a todos.
								.permitAll()
								.requestMatchers("/swagger-ui/**", "/v3/api-docs/**") // Si la URL coincide con eso, se le permite a todos.
								.permitAll()
								.requestMatchers("/private/archivo/**", "/private/resultadoporia/**", "/private/user/**") // Rutas privadas
								.hasAnyRole("USER", "ADMIN") // Si la persona tiene el rol de user o admin.
								.requestMatchers("/admin/**", "/admin/auditoria/**" )
								.hasRole("ADMIN")
								.anyRequest().authenticated()
				)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Verifica usuario y contra

		return http.build();
	}

	// --- 2. CREAMOS EL BEAN DE CONFIGURACIÓN DE CORS ---
	/**
	 * Configura las reglas de CORS para permitir peticiones desde el frontend (Angular).
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		
		// Permitir el origen donde corre Angular
		configuration.setAllowedOriginPatterns(Arrays.asList("*"));
		
		// Permitir los métodos HTTP que vas a usar
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		
		// Permitir las cabeceras que usa Angular, incluyendo el token y los archivos multipart
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		
		// Permitir el envío de credenciales (fundamental para que JWT funcione con CORS)
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// Aplicar estas reglas a todas las rutas del backend (/**)
		source.registerCorsConfiguration("/**", configuration);
		
		return source;
	}

	/**
	 * Configura el proveedor de autenticación.
	 *
	 * @return Proveedor de autenticación configurado
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	/**
	 * Configura el gestor de autenticación.
	 *
	 * @param config Configuración de autenticación
	 * @return Gestor de autenticación
	 * @throws Exception Si ocurre un error durante la configuración
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * Configura el codificador de contraseñas.
	 *
	 * @return Codificador de contraseñas BCrypt
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}