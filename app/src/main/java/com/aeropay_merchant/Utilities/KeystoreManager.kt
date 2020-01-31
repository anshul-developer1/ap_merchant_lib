package com.aeropay_merchant.Utilities

import android.util.Base64
import android.util.Log
import java.io.IOException
import java.security.*
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

object KeystoreManager {
    private const val tag = "KeyStoreManager"

    const val TRANSFORMATION = "AES/GCM/NoPadding"
    const val ANDROID_KEY_STORE = "AndroidKeyStore"

    object ALIAS {
        const val USERNAME = "username"
        const val PWD = "password"
    }

    private var encryptor: KeystoreEncryptor = KeystoreEncryptor()
    private var decryptor: KeystoreDecryptor = KeystoreDecryptor()

    fun decryptText(value: String,  ivValue : String, alias: String) : String? {
        try {
            val a:ByteArray = Base64.decode(ivValue, Base64.DEFAULT)
            val b:ByteArray = Base64.decode(value, Base64.DEFAULT)

            return decryptor.decryptData(alias, b, a)


        } catch (e: UnrecoverableEntryException) {
            Log.e(tag, "onClick() called with: " +  e.message)
        } catch (e: NoSuchAlgorithmException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: NoSuchProviderException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: KeyStoreException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: IOException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: NoSuchPaddingException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: InvalidKeyException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: SignatureException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }
        return null
    }

    fun encryptText(str: String, alias: String): ByteArray? {
        try {
            return encryptor.encryptText(alias, str)
        } catch (e: UnrecoverableEntryException) {
            Log.e(tag, "onClick() called with: " +  e.message)
        } catch (e: NoSuchAlgorithmException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: NoSuchProviderException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: KeyStoreException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: IOException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: NoSuchPaddingException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: InvalidKeyException) {
            Log.e(tag, "onClick() called with: " + e.message)
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: SignatureException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }
        return null
    }

}