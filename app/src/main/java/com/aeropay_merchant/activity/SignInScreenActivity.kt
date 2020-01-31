package com.aeropay_merchant.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.aeropay_merchant.R
import com.aeropay_merchant.Utilities.*
import com.aeropay_merchant.communication.AWSConnectionManager
import com.aeropay_merchant.communication.DefineID
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException

class SignInScreenActivity : BaseActivity() {

    lateinit var signInButton : ImageView
    val TAG = SignInScreenActivity::class.java!!.getSimpleName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_screen)
        signInButton = findViewById(R.id.signInButton)
    }

    //to perform login operation
    fun signInButtonClick(view: View) {
        view.isEnabled = false
        view.isClickable = false

        if(PrefKeeper.isLoggedIn){
            var usernameEncryptValue = PrefKeeper.username
            var passwordEncryptValue = PrefKeeper.password

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var usernameIvEncryptValue = PrefKeeper.usernameIV
                var passwordIvEncryptValue = PrefKeeper.passwordIV
                var username = KeystoreManager.decryptText(usernameEncryptValue!!,usernameIvEncryptValue!!, KeystoreManager.ALIAS.USERNAME)
                var password = KeystoreManager.decryptText(passwordEncryptValue!!,passwordIvEncryptValue!!, KeystoreManager.ALIAS.PWD)

                GlobalMethods().autoLoginAction(this@SignInScreenActivity,username!!,password!!,ConstantsStrings().isSignInActivity)
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                var username = KeyStoreHelper.decrypt(KeystoreManager.ALIAS.USERNAME,usernameEncryptValue)
                var password = KeyStoreHelper.decrypt(KeystoreManager.ALIAS.PWD,passwordEncryptValue)
                GlobalMethods().autoLoginAction(this@SignInScreenActivity,username!!,password!!,ConstantsStrings().isSignInActivity)
            }
        }
        else if(PrefKeeper.isPinEnabled){
            var intent = Intent(this@SignInScreenActivity,PinEnterActivity::class.java)
            intent.putExtra(ConstantsStrings().isPinActivityName,3)
            launchActivity(PinEnterActivity::class.java,intent)
        }
        else{
            launchActivity(SignInCredentialActivity::class.java)
        }
    }

    // to get calback for login success
    fun onCognitoSuccess(){
        var awsConnectionManager = AWSConnectionManager(this)
        awsConnectionManager.hitServer(DefineID().FETCH_MERCHANT_PROFILE,this,null)
    }

    // to get callback for login failure
    fun onCognitoFailure(){
        GlobalMethods().dismissLoader()
        Toast.makeText(this,"Auto-Login was not successfull", Toast.LENGTH_LONG).show()
        launchActivity(SignInScreenActivity::class.java)
    }

    override fun onResume() {
        super.onResume()
        signInButton.isEnabled = true
        signInButton.isClickable = true
    }
}
