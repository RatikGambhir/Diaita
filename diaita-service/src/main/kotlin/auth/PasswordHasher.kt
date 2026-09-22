package com.diaita.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PasswordHasher {
    private val random = SecureRandom()

    fun hash(password: String): PasswordDigest {
        val salt = ByteArray(SALT_BYTES).also(random::nextBytes)
        return PasswordDigest(
            hash = Base64.getEncoder().encodeToString(derive(password, salt)),
            salt = Base64.getEncoder().encodeToString(salt)
        )
    }

    fun verify(password: String, expectedHash: String, encodedSalt: String): Boolean {
        val salt = runCatching { Base64.getDecoder().decode(encodedSalt) }.getOrNull() ?: return false
        val expected = runCatching { Base64.getDecoder().decode(expectedHash) }.getOrNull() ?: return false
        return MessageDigest.isEqual(expected, derive(password, salt))
    }

    private fun derive(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    companion object {
        private const val ITERATIONS = 210_000
        private const val KEY_LENGTH_BITS = 256
        private const val SALT_BYTES = 16
    }
}

data class PasswordDigest(val hash: String, val salt: String)
