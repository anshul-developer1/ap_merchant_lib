package com.aeropay_merchant.activity

import AP.model.MerchantLocationDevices
import AP.model.RegisterMerchantDevice
import android.Manifest
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aeropay_merchant.Model.AeropayModelManager
import com.aeropay_merchant.Utilities.GlobalMethods
import com.aeropay_merchant.Utilities.PrefKeeper
import com.aeropay_merchant.adapter.HomeListRecyclerView
import com.aeropay_merchant.communication.AWSConnectionManager
import com.aeropay_merchant.communication.DefineID
import android.os.RemoteException
import org.altbeacon.beacon.*
import android.Manifest.permission
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconManager
import android.R
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import com.aeropay_merchant.Utilities.ClientFactory
import com.aeropay_merchant.adapter.HomeCardRecyclerView
import com.amazonaws.amplify.generated.graphql.OnCreateMerchantConsumerSyncStageSubscription
import com.amazonaws.amplify.generated.graphql.OnCreateMerchantSyncStageSubscription
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : BaseActivity(){

    lateinit var menuButton : ImageView
    lateinit var listViewRecycler : RecyclerView
    lateinit var cardViewRecycler : RecyclerView
    lateinit var readyToPay : TextView
    lateinit var aeropayTransparent : ImageView
    lateinit var beaconTransmitter: BeaconTransmitter
    var isBleSupported = false
    lateinit var subscriptionChannelWatcher: AppSyncSubscriptionCall<OnCreateMerchantConsumerSyncStageSubscription.Data>
    lateinit var subscriptionWatcher: AppSyncSubscriptionCall<OnCreateMerchantSyncStageSubscription.Data>
    val TAG = SignInScreenActivity::class.java!!.getSimpleName()
    val userName: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.aeropay_merchant.R.layout.activity_home)
        initialiseControls()

        GlobalMethods().getDeviceToken(applicationContext)
        setupView()
        setListeners()

        maintainUserLoginCount()

        var loginCount = PrefKeeper.logInCount
        if(loginCount< 4){
            var isPin = PrefKeeper.isPinEnabled
            var isLogin = PrefKeeper.isLoggedIn
            if(!isPin && !isLogin)
                GlobalMethods().showDialog(this)
        }
    }

    //setting onClick Listeners on views
    private fun setListeners() {
        menuButton.setOnClickListener(View.OnClickListener {
            launchActivity(NavigationMenuActivity::class.java)
        })
    }

    override fun onResume() {
        super.onResume()
        var result = BeaconTransmitter.checkTransmissionSupported(this)
        if(result == 0){
            isBleSupported = true
            createHitForUUID()
        }
        else{
            showMsgToast("Beacon is not supported in your device.")
        }
    }

    fun createHitForUUID(){
        var registerMerchant = RegisterMerchantDevice()
        var deviceIntValue = PrefKeeper.merchantDeviceId
        var deviceIdValue = deviceIntValue!!.toBigDecimal()

        registerMerchant.deviceId =  deviceIdValue
        registerMerchant.token = PrefKeeper.deviceToken

        var awsConnectionManager = AWSConnectionManager(this)
        awsConnectionManager.hitServer(DefineID().REGISTER_MERCHANT_LOCATION_DEVICE,this,registerMerchant)
    }

    //setting up hardcoded Recycler Adapter
    private fun setupView() {
        listViewRecycler.layoutManager = LinearLayoutManager(this)
        val payerName: ArrayList<String> = ArrayList()
        payerName.add("Daniel")
        payerName.add("Adam")
        listViewRecycler.adapter = HomeListRecyclerView(payerName,this)
    }

    //inflating UI controls
    private fun initialiseControls() {
        menuButton = findViewById(com.aeropay_merchant.R.id.back_button)
        listViewRecycler = findViewById(com.aeropay_merchant.R.id.recyclerListView)
        cardViewRecycler = findViewById(com.aeropay_merchant.R.id.cardRecyclerView)
        readyToPay = findViewById(com.aeropay_merchant.R.id.readyToPayText)
        aeropayTransparent = findViewById(com.aeropay_merchant.R.id.aeropayTranparentLogo)

        var text = "<font color=#06dab3>0</font> <font color=#232323>ready to pay</font>";
        readyToPay.setText(Html.fromHtml(text));

        cardViewRecycler.visibility = View.GONE
    }

    // to check the login count of this user on this device
    private fun maintainUserLoginCount() {
        var initialLoginCount = PrefKeeper.logInCount
        var finalCount = initialLoginCount + 1
        PrefKeeper.logInCount = finalCount
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun creatBeaconTransmission(){
        var registerMerchantDevice = AeropayModelManager().getInstance().registerMerchantDevices
        startSharedAdvertisingBeaconWithString(registerMerchantDevice.uuid as String, registerMerchantDevice.majorID.toInt() , registerMerchantDevice.minorID.toInt(), "AP Stores")
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun startSharedAdvertisingBeaconWithString(uuid: String, major: Int, minor: Int, identifier: String) {
        val manufacturer = 0x4C
        val beacon = Beacon.Builder()
            .setId1(uuid)
            .setId2(major.toString())
            .setId3(minor.toString())
            .setManufacturer(manufacturer)
            .setBluetoothName(identifier)
            .setTxPower(-59)
            .build()
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        beaconTransmitter = BeaconTransmitter(this, beaconParser)
        beaconTransmitter!!.startAdvertising(beacon, object : AdvertiseCallback() {

            override fun onStartFailure(errorCode: Int) {
                showMsgToast("onStartFailure")
                Log.d("ReactNative", "Error from start advertising $errorCode")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                showMsgToast("onStartSuccess")
                Log.d("ReactNative", "Success start advertising")
            }
        })
        startSubscription()
    }


    fun stopSharedAdvertisingBeacon() {
        if(isBleSupported){
            if (this.beaconTransmitter != null) {
                try {
                    this.beaconTransmitter!!.stopAdvertising()
                } catch (ex: Exception) {
                }
            }
        }
    }

    private fun startSubscription() {
        val subscriptionCallback = object : AppSyncSubscriptionCall.Callback<OnCreateMerchantSyncStageSubscription.Data> {
            override fun onResponse(response: Response<OnCreateMerchantSyncStageSubscription.Data>) {
                runOnUiThread {
                    cardViewRecycler.visibility = View.VISIBLE
                    aeropayTransparent.visibility = View.GONE

                    cardViewRecycler.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL, false)
                    userName.add(response.data()!!.onCreateMerchantSyncStage()!!.__typename())

                    cardViewRecycler.adapter = HomeCardRecyclerView(userName,this@HomeActivity)
                    Log.e(TAG, "Subscription response: " + response.data()!!.toString())
                }
            }

            override fun onFailure(e: ApolloException) {
                Log.e(TAG, "Subscription failure", e)
            }

            override fun onCompleted() {
                Log.d(TAG, "Subscription completed")
            }

        }
        val subscription = OnCreateMerchantSyncStageSubscription.builder().merchant_id("198").build()
        subscriptionWatcher = ClientFactory.getInstance(this.applicationContext).subscribe(subscription)
        subscriptionWatcher.execute(subscriptionCallback)
    }

    override fun onStop() {
        stopSharedAdvertisingBeacon()
        super.onStop()
    }
}
