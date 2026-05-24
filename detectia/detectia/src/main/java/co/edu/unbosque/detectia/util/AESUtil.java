package co.edu.unbosque.detectia.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad criptográfica para cifrado AES/GCM y generación de hashes.
 * <p>
 * Proporciona métodos estáticos para cifrar y descifrar cadenas de texto
 * utilizando el algoritmo AES en modo GCM sin padding, con clave e IV fijos
 * de 16 bytes. Incluye además generadores de hash criptográfico mediante
 * Apache Commons Codec: MD5, SHA-1, SHA-256, SHA-384 y SHA-512.
 * </p>
 * <p>
 * <strong>Nota de seguridad:</strong> el uso de IV fijo hace que el cifrado
 * sea determinístico y no sea seguro para todos los casos de uso. Se emplea
 * exclusivamente para ofuscar datos en tránsito dentro de la plataforma.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 */
public class AESUtil {

	private final static String ALGORITMO = "AES";

	private final static String TIPOCIFRADO = "AES/GCM/NoPadding";

	/**
	 * Cifra un texto plano con AES/GCM/NoPadding usando la clave e IV indicados.
	 *
	 * @param llave clave AES de exactamente 16 caracteres
	 * @param iv    vector de inicialización de exactamente 16 caracteres
	 * @param texto texto plano a cifrar
	 * @return representación Base64 del texto cifrado
	 */
	public static String encrypt(String llave, String iv, String texto) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(TIPOCIFRADO);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}

		SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		byte[] encrypted = null;
		try {
			encrypted = cipher.doFinal(texto.getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return new String(encodeBase64(encrypted));
	}

	/**
	 * Descifra un texto cifrado en Base64 con AES/GCM/NoPadding usando la clave e
	 * IV indicados.
	 *
	 * @param llave     clave AES de exactamente 16 caracteres
	 * @param iv        vector de inicialización de exactamente 16 caracteres
	 * @param encrypted texto cifrado codificado en Base64
	 * @return texto plano descifrado, o cadena vacía si ocurre algún error
	 */
	public static String decrypt(String llave, String iv, String encrypted) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(TIPOCIFRADO);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}

		SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {

			e.printStackTrace();
		}

		byte[] enc = decodeBase64(encrypted);
		byte[] decrypted = null;
		try {
			decrypted = cipher.doFinal(enc);
			return new String(decrypted);
		} catch (IllegalBlockSizeException | BadPaddingException e) {

		}
		return "";

	}

	/**
	 * Descifra un texto cifrado en Base64 usando la clave e IV internos de la
	 * plataforma ({@code "llavede16carater"} / {@code "programacioncomp"}).
	 *
	 * @param encrypted texto cifrado codificado en Base64
	 * @return texto plano descifrado, o cadena vacía si ocurre algún error
	 */
	public static String decrypt(String encrypted) {
		String iv = "programacioncomp";
		String key = "llavede16carater";
		return decrypt(key, iv, encrypted);
	}

	/**
	 * Cifra un texto plano usando la clave e IV internos de la plataforma
	 * ({@code "llavede16carater"} / {@code "programacioncomp"}).
	 *
	 * @param plainText texto plano a cifrar
	 * @return representación Base64 del texto cifrado
	 */
	public static String encrypt(String plainText) {
		String iv = "programacioncomp";
		String key = "llavede16carater";
		return encrypt(key, iv, plainText);
	}

	/**
	 * Genera el hash MD5 (128 bits, 32 caracteres hexadecimales) del contenido.
	 *
	 * @param content cadena de texto a hashear
	 * @return hash MD5 en formato hexadecimal en minúsculas
	 */
	public static String hashingToMD5(String content) {
		return  DigestUtils.md5Hex(content);
	}

	/**
	 * Genera el hash SHA-1 (160 bits, 40 caracteres hexadecimales) del contenido.
	 *
	 * @param content cadena de texto a hashear
	 * @return hash SHA-1 en formato hexadecimal en minúsculas
	 */
	public static String hashingToSHA1(String content) {
		return DigestUtils.sha1Hex(content);
	}

	/**
	 * Genera el hash SHA-256 (256 bits, 64 caracteres hexadecimales) del contenido.
	 *
	 * @param content cadena de texto a hashear
	 * @return hash SHA-256 en formato hexadecimal en minúsculas
	 */
	public static String hashingToSHA256(String content) {
		return DigestUtils.sha256Hex(content);
	}

	/**
	 * Genera el hash SHA-384 (384 bits, 96 caracteres hexadecimales) del contenido.
	 *
	 * @param content cadena de texto a hashear
	 * @return hash SHA-384 en formato hexadecimal en minúsculas
	 */
	public static String hashingToSHA384(String content) {
		return DigestUtils.sha384Hex(content);
	}

	/**
	 * Genera el hash SHA-512 (512 bits, 128 caracteres hexadecimales) del contenido.
	 *
	 * @param content cadena de texto a hashear
	 * @return hash SHA-512 en formato hexadecimal en minúsculas
	 */
	public static String hashingToSHA512(String content) {
		return DigestUtils.sha512Hex(content);
	}
	
	

//	public static void main(String[] args) {
//		String text = "zambrano lo robaron hace meses";
//		System.out.println(text);
//		
//		String encoded = encrypt(text);
//		System.out.println(encoded);
//
//		String decoded = decrypt(encoded);
//		System.out.println(decoded);
//		
//		String contrasena="soyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseñasoyunacontraseña";
//		System.out.println(hashingToMD5(contrasena));
//		System.out.println(hashingToSHA1(contrasena));
//		System.out.println(hashingToSHA256(contrasena));
//		System.out.println(hashingToSHA384(contrasena));
//		System.out.println(hashingToSHA512(contrasena));
//	}

}
