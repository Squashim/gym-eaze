package com.example.gymeaze.util

import android.util.Patterns

object Validation {
    private val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#\$%^&*()_+./])[A-Za-z\\d!@#\$%^&*()_+./]{14,}\$")

    fun validateUsername(username: String): String?{
        if(username.isBlank()) return "Nazwa użytkownika jest wymagana."
        if(username.length < 6 || username.length > 16) return "Nazwa użytkownika musi mieć od 6 do 16 znaków."
        return null
    }

    fun validateEmail(email: String): String?{
        if(email.isBlank()) return "Email jest wymagany."
        if(email.length < 2 || email.length > 320) return "Email musi mieć od 2 do 320 znaków."
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Nieprawidłowy format adresu email."
        return null
    }

    fun validatePassword(password: String): String?{
        if (password.isBlank()) return "Hasło jest wymagane."
        if (password.any { it.isWhitespace() }) return "Hasło nie może zawierać spacji ani innych znaków białych."
        if (!passwordRegex.containsMatchIn(password)) {
            return "Hasło musi mieć min 14 znaków, 1 dużą literę, 1 małą literę, 1 cyfrę i 1 znak specjalny."
        }
        return null
    }

    fun validateConfirmPassword(password: String, confirm: String): String?{
        if(confirm.isBlank()) return "Hasło jest wymagane."
        if(password != confirm) return "Hasła nie są identyczne."
        return null
    }

    fun validateUsernameOrEmail(value: String): String?{
        if(value.isBlank()) return "Login jest wymagany."
        if(value.contains("@")){
            if(!Patterns.EMAIL_ADDRESS.matcher(value).matches()) return "Nieprawidłowy email."
        } else {
            if(value.length < 6 || value.length > 16) return "Nazwa użytkownika musi mieć od 6 do 16 znaków."
        }
        return null
    }
}
