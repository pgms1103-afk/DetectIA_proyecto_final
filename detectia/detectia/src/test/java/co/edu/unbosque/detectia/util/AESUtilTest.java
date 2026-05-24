package co.edu.unbosque.detectia.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase utilitaria {@link AESUtil}.
 * <p>
 * Verifica el correcto funcionamiento de las operaciones de cifrado AES/GCM,
 * descifrado y generación de hashes criptográficos (MD5, SHA-1, SHA-256,
 * SHA-384, SHA-512). Garantiza la coherencia del ciclo encrypt→decrypt y la
 * consistencia de los hashes para la misma entrada.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see AESUtil
 */
class AESUtilTest {

    // ─── ENCRYPT / DECRYPT ─────────────────────────────────────────────────────

    /**
     * Verifica que el ciclo completo encrypt → decrypt recupera el texto
     * original para una cadena de texto común.
     */
    @Test
    void testEncryptDecrypt_TextoComun() {
        String original = "usuario@correo.com";

        String encriptado  = AESUtil.encrypt(original);
        String desencriptado = AESUtil.decrypt(encriptado);

        assertNotNull(encriptado);
        assertNotEquals(original, encriptado);          // El texto cifrado no debe ser igual al original
        assertEquals(original, desencriptado);          // El descifrado debe recuperar el original
    }

    /**
     * Verifica que dos llamadas a {@code encrypt} con el mismo texto
     * producen el mismo resultado cifrado (cifrado determinístico con IV fijo).
     */
    @Test
    void testEncrypt_Determinista() {
        String original = "test@email.com";

        String enc1 = AESUtil.encrypt(original);
        String enc2 = AESUtil.encrypt(original);

        assertEquals(enc1, enc2);
    }

    /**
     * Verifica que dos cadenas de entrada diferentes producen cifrados
     * distintos (garantía de unicidad del cifrado).
     */
    @Test
    void testEncrypt_DiferentesEntradas_DiferentesSalidas() {
        String enc1 = AESUtil.encrypt("usuario1@correo.com");
        String enc2 = AESUtil.encrypt("usuario2@correo.com");

        assertNotEquals(enc1, enc2);
    }

    /**
     * Verifica que {@code decrypt} recupera correctamente el texto
     * para un correo electrónico cifrado.
     */
    @Test
    void testDecrypt_CorreoElectronico() {
        String correo = "admin@detectia.com";
        String encriptado = AESUtil.encrypt(correo);

        assertEquals(correo, AESUtil.decrypt(encriptado));
    }

    // ─── HASHING ───────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code hashingToMD5} retorna un hash MD5 de 32 caracteres
     * hexadecimales para una entrada válida.
     */
    @Test
    void testHashingToMD5_LongitudCorrecta() {
        String hash = AESUtil.hashingToMD5("detectia");

        assertNotNull(hash);
        assertEquals(32, hash.length());
    }

    /**
     * Verifica que {@code hashingToSHA1} retorna un hash SHA-1 de 40
     * caracteres hexadecimales para una entrada válida.
     */
    @Test
    void testHashingToSHA1_LongitudCorrecta() {
        String hash = AESUtil.hashingToSHA1("detectia");

        assertNotNull(hash);
        assertEquals(40, hash.length());
    }

    /**
     * Verifica que {@code hashingToSHA256} retorna un hash SHA-256 de 64
     * caracteres hexadecimales para una entrada válida.
     */
    @Test
    void testHashingToSHA256_LongitudCorrecta() {
        String hash = AESUtil.hashingToSHA256("detectia");

        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    /**
     * Verifica que {@code hashingToSHA384} retorna un hash SHA-384 de 96
     * caracteres hexadecimales para una entrada válida.
     */
    @Test
    void testHashingToSHA384_LongitudCorrecta() {
        String hash = AESUtil.hashingToSHA384("detectia");

        assertNotNull(hash);
        assertEquals(96, hash.length());
    }

    /**
     * Verifica que {@code hashingToSHA512} retorna un hash SHA-512 de 128
     * caracteres hexadecimales para una entrada válida.
     */
    @Test
    void testHashingToSHA512_LongitudCorrecta() {
        String hash = AESUtil.hashingToSHA512("detectia");

        assertNotNull(hash);
        assertEquals(128, hash.length());
    }

    /**
     * Verifica que los hashes son consistentes: la misma entrada siempre
     * produce la misma salida (propiedad determinística del hash).
     */
    @Test
    void testHashing_Consistencia() {
        String entrada = "contraseña123";

        assertEquals(AESUtil.hashingToMD5(entrada),    AESUtil.hashingToMD5(entrada));
        assertEquals(AESUtil.hashingToSHA256(entrada), AESUtil.hashingToSHA256(entrada));
        assertEquals(AESUtil.hashingToSHA512(entrada), AESUtil.hashingToSHA512(entrada));
    }

    /**
     * Verifica que entradas diferentes producen hashes MD5 distintos
     * (propiedad de distribución del hash).
     */
    @Test
    void testHashing_EntadasDiferentes_HashDiferente() {
        String hash1 = AESUtil.hashingToSHA256("usuario1");
        String hash2 = AESUtil.hashingToSHA256("usuario2");

        assertNotEquals(hash1, hash2);
    }
}
