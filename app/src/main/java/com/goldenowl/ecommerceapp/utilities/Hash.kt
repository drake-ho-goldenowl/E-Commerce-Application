package com.goldenowl.ecommerceapp.utilities

import java.security.MessageDigest

class Hash{
    companion object{
        fun hashSHA256(string : String): String {
            val bytes = string.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }
    }
}