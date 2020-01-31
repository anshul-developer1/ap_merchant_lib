package com.aeropay_merchant.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.aeropay_merchant.R
import com.aeropay_merchant.Utilities.*
import com.aeropay_merchant.communication.AWSConnectionManager
import com.aeropay_merchant.communication.DefineID

class SplashActivity : BaseActivity() {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        PrefKeeper.init(this)
        navigateToScreen()
    }

    //method to check for auto login/pin login or normal login and redirection
    private fun navigateToScreen() {
        if(PrefKeeper.isLoggedIn){
            GlobalMethods().showLoader(this)
            var usernameEncryptValue = PrefKeeper.username
            var passwordEncryptValue = PrefKeeper.password

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var usernameIvEncryptValue = PrefKeeper.usernameIV
                var passwordIvEncryptValue = PrefKeeper.passwordIV
                var username = KeystoreManager.decryptText(usernameEncryptValue!!,usernameIvEncryptValue!!,KeystoreManager.ALIAS.USERNAME)
                var password = KeystoreManager.decryptText(passwordEncryptValue!!,passwordIvEncryptValue!!,KeystoreManager.ALIAS.PWD)

                GlobalMethods().autoLoginAction(this@SplashActivity,username!!,password!!,
                    ConstantsStrings().isSplashActivity)
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                var username = KeyStoreHelper.decrypt(KeystoreManager.ALIAS.USERNAME,usernameEncryptValue)
                var password = KeyStoreHelper.decrypt(KeystoreManager.ALIAS.PWD,passwordEncryptValue)
                GlobalMethods().autoLoginAction(this@SplashActivity,username!!,password!!,ConstantsStrings().isSplashActivity)
            }
        }
        else{
            mDelayHandler = Handler()
            mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        }

    }

    //to show Splash for 3 seconds
    internal val mRunnable: Runnable = Runnable {
         if(PrefKeeper.isPinEnabled) {
             var intent = Intent(this@SplashActivity,PinEnterActivity::class.java)
             intent.putExtra(ConstantsStrings().isPinActivityName,3)
             launchActivity(PinEnterActivity::class.java,intent)
         }
        else {
             launchActivity(SignInScreenActivity::class.java)
             finish()
         }
    }

    // to get callback for login success
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
}
