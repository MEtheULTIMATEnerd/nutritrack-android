package com.fit2081.fit2081assignment1

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Local-only password hashing for the demo Room database. */
object PasswordHasher {
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val SALT_BYTES = 16

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val digest = derive(password, salt, ITERATIONS)
        return "pbkdf2\$${ITERATIONS}\$${salt.toHex()}\$${digest.toHex()}"
    }

    fun verify(password: String, encoded: String): Boolean {
        val parts = encoded.split('$')
        if (parts.size != 4 || parts[0] != "pbkdf2") return false
        return runCatching {
            val iterations = parts[1].toInt()
            val expected = parts[3].hexToBytes()
            val actual = derive(password, parts[2].hexToBytes(), iterations)
            MessageDigest.isEqual(expected, actual)
        }.getOrDefault(false)
    }

    private fun derive(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH)
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    }

    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }
    private fun String.hexToBytes() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
