package com.aeropay_merchant.activity

import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.aeropay_merchant.R
import com.aeropay_merchant.Utilities.*
import com.aeropay_merchant.communication.AWSConnectionManager
import com.aeropay_merchant.communication.DefineID
import com.aeropay_merchant.view.CustomEditText
import android.widget.ImageView


class SignInCredentialActivity : BaseActivity(){

    lateinit var userNameEdit : CustomEditText
    lateinit var passwordEdit : CustomEditText
    lateinit var signInButton : ImageView
    lateinit var userName : String
    lateinit var password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_credential)

        initialiseControls()
    }

    private fun initialiseControls() {
        userNameEdit = findViewById(R.id.userEmail)
        passwordEdit = findViewById(R.id.userPassword)
        signInButton = findViewById(R.id.signInButton)

        var userNameValue = PrefKeeper.username
        if(!(userNameValue.equals(ConstantsStrings().noValue))){
            var usernameEncryptValue = PrefKeeper.username

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var usernameIvEncryptValue = PrefKeeper.usernameIV
                var username = KeystoreManager.decryptText(usernameEncryptValue!!,usernameIvEncryptValue!!, KeystoreManager.ALIAS.USERNAME)
                userNameEdit.setText(username)
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                var usernameEncryptValue = PrefKeeper.username
                var username = KeyStoreHelper.decrypt(KeystoreManager.ALIAS.USERNAME,usernameEncryptValue)
                userNameEdit.setText(username)
            }
        }
    }

    // check for email and password validations for Login
    fun createUserValidation(view: View) {
         userName = userNameEdit.text.toString()
         password = passwordEdit.text.toString()
         userName = "daniel.muller@aeropayments.com"
         password = "Password*12345"

        if(userName.trim().isNullOrEmpty() || password!!.trim().isNullOrEmpty()){
            Toast.makeText(this,"Please enter Email and password.",Toast.LENGTH_SHORT).show()
        }
        else if(userName.trim().isNullOrEmpty()){
            Toast.makeText(this,"Please enter your Email.",Toast.LENGTH_SHORT).show()
        }
        else if(password!!.trim().isNullOrEmpty()){
            Toast.makeText(this,"Please enter your password.",Toast.LENGTH_SHORT).show()
        }
        else if(!(GlobalMethods().isValidEmailId(userName))){
            Toast.makeText(this,"Please enter a valid Email ID.",Toast.LENGTH_SHORT).show()
        }
        else
        {
        GlobalMethods().showLoader(this)
        view.isClickable = false
        view.isEnabled = false
        GlobalMethods().userCognitoLoginHandler(this@SignInCredentialActivity, view,userName,password)
        }
    }

    //callback for AWS Login success
    fun onCognitoSuccess(){
        savingCredentialInkeystore()
        var awsConnectionManager = AWSConnectionManager(this)
        awsConnectionManager.hitServer(DefineID().FETCH_MERCHANT_PROFILE,this,null)
    }

    //saving Login Credentials in Key Store
    private fun savingCredentialInkeystore() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var usernameArray = KeystoreEncryptor().encryptText(KeystoreManager.ALIAS.USERNAME, userName)
                PrefKeeper.username = Base64.encodeToString(usernameArray, Base64.DEFAULT)
                var passwordArray = KeystoreEncryptor().encryptText(KeystoreManager.ALIAS.PWD, password)
                PrefKeeper.password = Base64.encodeToString(passwordArray, Base64.DEFAULT)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                KeyStoreHelper.createKeys(this, KeystoreManager.ALIAS.USERNAME)
                KeyStoreHelper.createKeys(this, KeystoreManager.ALIAS.PWD)
                PrefKeeper.username = KeyStoreHelper.encrypt(KeystoreManager.ALIAS.USERNAME, userName)
                PrefKeeper.password = KeyStoreHelper.encrypt(KeystoreManager.ALIAS.PWD, password)
            }
        }

    //callback for AWS Login failure
    fun onCognitoFailure(){
        GlobalMethods().dismissLoader()
        Toast.makeText(this,"Invalid username or password", Toast.LENGTH_LONG).show()
    }
}
