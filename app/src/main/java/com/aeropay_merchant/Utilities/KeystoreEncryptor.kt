package com.aeropay_merchant.Utilities

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.IOException
import java.security.*
import javax.crypto.*

class KeystoreEncryptor {

    lateinit var encryption: ByteArray
        private set

    @Throws(UnrecoverableEntryException::class, NoSuchAlgorithmException::class, KeyStoreException::class, NoSuchProviderException::class, NoSuchPaddingException::class, InvalidKeyException::class, IOException::class, InvalidAlgorithmParameterException::class, SignatureException::class, BadPaddingException::class, IllegalBlockSizeException::class)
    fun encryptText(alias: String, textToEncrypt: String): ByteArray {

        val cipher = Cipher.getInstance(KeystoreManager.TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias))

        if(alias.equals(KeystoreManager.ALIAS.USERNAME)){
            PrefKeeper.usernameIV = Base64.encodeToString(cipher.iv, Base64.DEFAULT);
        }
        else if(alias.equals(KeystoreManager.ALIAS.PWD)){
            PrefKeeper.passwordIV = Base64.encodeToString(cipher.iv, Base64.DEFAULT);
        }
        encryption = cipher.doFinal(textToEncrypt.toByteArray(charset("UTF-8")))

        return encryption
    }


    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class, InvalidAlgorithmParameterException::class)
    fun getSecretKey(alias: String): SecretKey {

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KeystoreManager.ANDROID_KEY_STORE)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyGenerator.init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }
        else{
           //keyGenerator.init()
        }
        return keyGenerator.generateKey()
    }
}