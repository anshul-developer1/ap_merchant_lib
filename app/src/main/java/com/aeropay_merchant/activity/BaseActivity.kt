package com.aeropay_merchant.activity

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.aeropay_merchant.R
import com.aeropay_merchant.Utilities.ConstantsStrings
import com.aeropay_merchant.Utilities.PrefKeeper
import com.aeropay_merchant.communication.AWSConnectionManager
import com.aeropay_merchant.communication.DefineID
import com.aeropay_merchant.communication.ICommunicationHandler

var loader : Dialog? = null
lateinit var idToken : String

open class BaseActivity : AppCompatActivity() , ICommunicationHandler{

    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    //move from one activity to other
    fun launchActivity(activityClass: Class<out BaseActivity>, intent : Intent? = null) {
        if(intent != null){
            startActivity(intent)
            overridePendingTransition(R.anim.right_enter, R.anim.left_exit)
        }
        else {
            startActivity(Intent(this, activityClass))
            overridePendingTransition(R.anim.left_enter, R.anim.right_exit)
        }
    }

    fun showMsgToast(msg: String) {
        if (mToast != null) {
            mToast?.cancel()
            mToast = null
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        mToast?.show()
    }

    // Network callback for success
    override fun onSuccess(outputParms: Int) {
        if(outputParms.equals(DefineID().FETCH_MERCHANT_PROFILE)){
            if(PrefKeeper.storeName.equals(ConstantsStrings().noValue)|| PrefKeeper.deviceName.equals(ConstantsStrings().noValue)){
                var awsConnectionManager = AWSConnectionManager(this)
                awsConnectionManager.hitServer(DefineID().FETCH_MERCHANT_LOCATIONS,this,null)
            }
            else{
                launchActivity(HomeActivity::class.java)
            }
        }
        else if(outputParms.equals(DefineID().FETCH_MERCHANT_LOCATIONS)){
            launchActivity(SettingsScreenActivity::class.java)
        }
        else if(outputParms.equals(DefineID().FETCH_MERCHANT_DEVICES)){
            (this as SettingsScreenActivity).createDeviceSpinner()
        }
        else if(outputParms.equals(DefineID().FETCH_MERCHANT_PROCESS_TRANSACTION)){
            (this as HomeActivity).sendProcessTransaction()
        }
        else if(outputParms.equals(DefineID().REGISTER_MERCHANT_LOCATION_DEVICE)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                (this as HomeActivity).creatBeaconTransmission()
            }
        }
    }

    // Network callback for failure
    override fun onFailure(outputParms: Int) {
        if(outputParms.equals(DefineID().FETCH_MERCHANT_PROFILE)){
            showMsgToast("API Failure")
        }
        else if(outputParms.equals(DefineID().FETCH_MERCHANT_LOCATIONS)){
            showMsgToast("API Failure")
        }
        else if(outputParms.equals(DefineID().FETCH_MERCHANT_DEVICES)){
            showMsgToast("API Failure")
        }
        else if(outputParms.equals(DefineID().REGISTER_MERCHANT_LOCATION_DEVICE)){
            showMsgToast("API Failure")
        }
        else if(outputParms.equals(DefineID().FETCH_MERCHANT_PROCESS_TRANSACTION)){
            showMsgToast("API Failure")
        }
    }

    // removing the android native back press functionality
    override fun onBackPressed() {

    }
}
