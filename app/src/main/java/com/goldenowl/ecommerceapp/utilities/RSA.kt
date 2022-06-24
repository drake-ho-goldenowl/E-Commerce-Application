package com.goldenowl.ecommerceapp.utilities

import java.io.IOException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class RSA {
    private var masterKey: KeyPair? = null
    private val cipher: Cipher = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC)

    init {
        if (masterKey == null) {
            createKeyPair()
        }
    }

    private fun createKeyPair() {
        val privateKey = stringToPrivate()
        val publicKey = privateKey?.let { generatePublicKey(it) }
        masterKey = KeyPair(publicKey, privateKey)
    }

    private fun getKeyPair(): KeyPair? {
        if (masterKey == null) {
            createKeyPair()
        }
        return masterKey
    }


    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun stringToPrivate(): PrivateKey? {
        try {
            // Base64 decode the result
            val decodedImg: ByteArray =
                Base64.getMimeDecoder().decode(PRIVATE_KEY.toByteArray(StandardCharsets.UTF_16))
            // extract the private key
            val keySpec =
                PKCS8EncodedKeySpec(decodedImg)
            val kf = KeyFactory.getInstance(KEY_ALGORITHM_RSA)
            return kf.generatePrivate(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun generatePublicKey(privateKey: PrivateKey): PublicKey? {
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")

        val privateKeySpec: RSAPrivateKeySpec =
            keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec::class.java)

        val keySpec = RSAPublicKeySpec(privateKeySpec.modulus, BigInteger.valueOf(65537))

        return keyFactory.generatePublic(keySpec)
    }

    fun encrypt(data: String): String {
        getKeyPair()
        cipher.init(Cipher.ENCRYPT_MODE, masterKey?.public)
        val bytes = cipher.doFinal(data.toByteArray())
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
    }

    fun decrypt(data: String): String {
        getKeyPair()
        cipher.init(Cipher.DECRYPT_MODE, masterKey?.private)
        val encryptedData = android.util.Base64.decode(data, android.util.Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }

    companion object {
        const val KEY_ALGORITHM_RSA = "RSA"
        const val TRANSFORMATION_ASYMMETRIC = "RSA/ECB/PKCS1Padding"
    }
}
