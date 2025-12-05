package com.example.habittracker.utils

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


fun hashPassword(password: String, salt: ByteArray = generateSalt()): Pair<String, String> {
    val iterations = 100_000
    val keyLength = 256

    val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val hash = factory.generateSecret(spec).encoded

    val saltBase64 = Base64.getEncoder().encodeToString(salt)
    val hashBase64 = Base64.getEncoder().encodeToString(hash)

    return Pair(saltBase64, hashBase64)
}


fun generateSalt(): ByteArray {
    val salt = ByteArray(16)
    SecureRandom().nextBytes(salt)
    return salt
}

fun verifyPassword(password: String, storedSalt: String, storedHash: String): Boolean {
    val salt = Base64.getDecoder().decode(storedSalt)
    val (_, newHash) = hashPassword(password, salt)
    return newHash == storedHash
}