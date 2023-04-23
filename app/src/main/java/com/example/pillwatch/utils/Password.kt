package com.example.pillwatch.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.util.UUID

fun hashPassword(input: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val messageDigest = md.digest(input.toByteArray())
    val no = BigInteger(1, messageDigest)
    var hashText = no.toString(16)
    while (hashText.length < 32) {
        hashText = "0$hashText"
    }
    return hashText
}

fun checkPassword(inputPassword: String, storedHashedPassword: String): Boolean {
    val inputHashedPassword = hashPassword(inputPassword)
    return inputHashedPassword == storedHashedPassword
}

fun randomPassword(): String {
    return UUID.randomUUID().toString()
}