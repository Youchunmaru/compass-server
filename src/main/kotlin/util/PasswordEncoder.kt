package com.youchunmaru.util

import org.mindrot.jbcrypt.BCrypt


class PasswordEncoder {
    companion object {
        fun encode(password: String): String {
            return BCrypt.hashpw(password, BCrypt.gensalt())
        }

        fun verifyPassword(password: String, hashedPassword: String): Boolean {
            return BCrypt.checkpw(password, hashedPassword)
        }
    }

}