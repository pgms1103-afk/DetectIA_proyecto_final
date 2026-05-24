package co.edu.unbosque.detectia.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autenticación JWT que intercepta cada petición HTTP para validar
 * el token Bearer presente en el encabezado {@code Authorization}.
 * <p>
 * Si el token es válido, extrae el nombre de usuario, carga los detalles del
 * usuario mediante {@link UserDetailsService} y establece la autenticación en
 * el {@link org.springframework.security.core.context.SecurityContextHolder}
 * para que el resto del pipeline de seguridad lo reconozca.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see JwtUtil
 * @see UserDetailsServiceImpl
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	 /** Utilidad para operaciones con tokens JWT. */
	  private final JwtUtil jwtUtil;

	  /** Servicio para cargar los detalles del usuario. */
	  private final UserDetailsService userDetailsService;

	  public JwtAuthenticationFilter(JwtUtil jwtUtil, 
			    @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
		    this.jwtUtil = jwtUtil;
		    this.userDetailsService = userDetailsService;
		  }
	  
	  /**
	   * Método principal del filtro que se ejecuta para cada solicitud HTTP. Extrae y valida el token
	   * JWT del encabezado de autorización. Si el token es válido, establece la autenticación en el
	   * contexto de seguridad.
	   *
	   * @param request Solicitud HTTP entrante
	   * @param response Respuesta HTTP saliente
	   * @param filterChain Cadena de filtros para continuar el procesamiento
	   * @throws ServletException Si ocurre un error durante el procesamiento del servlet
	   * @throws IOException Si ocurre un error de entrada/salida
	 * @throws java.io.IOException 
	   */
	  @Override
	  protected void doFilterInternal(
	      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	      throws ServletException, IOException, java.io.IOException {

	    final String authorizationHeader = request.getHeader("Authorization");

	    String username = null;
	    String jwt = null;

	    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	      jwt = authorizationHeader.substring(7);//verifica si es real
	      try {
	        username = jwtUtil.extractUsername(jwt);//Extraiga el username, verifica que sea valido y lo guarda
	      } catch (Exception e) {
	        logger.error("Error extracting username from token", e);
	      }
	    }

	    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { //Traiga todos los detalles de esa persona
	      UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

	      if (jwtUtil.validateToken(jwt, userDetails)) {//Coje todo wl JWT y verifica contraseña, rol, y userName.
	        UsernamePasswordAuthenticationToken authenticationToken =
	            new UsernamePasswordAuthenticationToken(
	                userDetails, null, userDetails.getAuthorities());//autorities verifica que puede hacer el usuario verificado.
	        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	      }
	    }

	    filterChain.doFilter(request, response);
	  }
	}
