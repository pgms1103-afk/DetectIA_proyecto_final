package co.edu.unbosque.detectia.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import co.edu.unbosque.detectia.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;


@Component
public class JwtUtil {

	 /** Tiempo de validez del token JWT en milisegundos (24 horas). */
	  private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000; // 24 horas, 

    /**
     * Clave secreta utilizada para firmar los tokens JWT. Se puede configurar en las propiedades de
     * la aplicación.
     */
    @Value("${jwt.secret:defaultSecretKeyWhichShouldBeAtLeast32CharactersLong}") //Asi se trae propiedades el .properties aca
    private String secret; // El valor de ese string es lo que esta arriba, eso del properties.


    private Key getSigningKey() { //Convierte esa clave en bytes
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
      }

    public String extractUsername(String token) { //Busca el nombre de usuario
        return extractClaim(token, Claims::getSubject);
      }
    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param token Token JWT del cual extraer la fecha de expiración
     * @return Fecha de expiración del token
     */
    public Date extractExpiration(String token) { //Verificar la fecha de expiracion
      return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae el rol del usuario del token JWT.
     *
     * @param token Token JWT del cual extraer el rol
     * @return Rol del usuario contenido en el token
     */
    public String extractRole(String token) { //Extrae el ROL
      return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Método genérico para extraer cualquier reclamación (claim) del token JWT.
     *
     * @param token Token JWT del cual extraer la reclamación
     * @param claimsResolver Función para resolver la reclamación específica
     * @return Valor de la reclamación extraída
     * @param <T> Tipo de dato de la reclamación a extraer
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { //Trae los claims
      final Claims claims = extractAllClaims(token);
      return claimsResolver.apply(claims);
    }
    
    /**
     * Extrae todas las reclamaciones (claims) del token JWT.
     *
     * @param token Token JWT del cual extraer todas las reclamaciones
     * @return Objeto Claims que contiene todas las reclamaciones del token
     */
    private Claims extractAllClaims(String token) {
      return Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
    }

    private Boolean isTokenExpired(String token) { //Ver si el token ya esta expirado
        return extractExpiration(token).before(new Date());
      }


    /**
     * Genera un token JWT para un usuario.
     *
     * @param userDetails Detalles del usuario para el cual generar el token
     * @return Token JWT generado
     */
    public String generateToken(UserDetails userDetails) { 
      Map<String, Object> claims = new HashMap<>();
      claims.put("authorities", userDetails.getAuthorities());

      // Añadir rol a las reclamaciones si userDetails es de nuestra clase User
      if (userDetails instanceof Usuario) {
    	  Usuario user = (Usuario) userDetails;
        claims.put("role", user.getRole().name());
      }

      return createToken(claims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()//Se genera un JWT con esos claims:
            .setClaims(claims)//Persona
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))//A quien pertenece
            .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))//Fecha
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)//Q cosa extra tendra
            .compact();
      }
    
    /**
     * Valida un token JWT para un usuario específico.
     *
     * @param token Token JWT a validar
     * @param userDetails Detalles del usuario contra los cuales validar el token
     * @return true si el token es válido para el usuario, false en caso contrario
     */
    public Boolean validateToken(String token, UserDetails userDetails) { //SI el de arriba lo crea, aqui se valida
      final String username = extractUsername(token);//Primero va y trae el nombre user
      return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); //Si el username existe y no esta expirado hasta el momento es un token valido(hasta el momento pq falta verificar los otros claims)
    }
}
