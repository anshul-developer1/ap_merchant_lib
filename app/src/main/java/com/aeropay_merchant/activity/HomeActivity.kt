package com.aeropay_merchant.activity

import AP.model.MerchantLocationDevices
import AP.model.ProcessTransaction
import AP.model.RegisterMerchantDevice
import android.Manifest
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aeropay_merchant.Model.AeropayModelManager
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
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.aeropay_merchant.Utilities.*
import com.aeropay_merchant.ViewModel.HomeViewModel
import com.aeropay_merchant.adapter.HomeCardRecyclerView
import com.aeropay_merchant.view.CustomTextView
import com.amazonaws.amplify.generated.graphql.OnCreateMerchantConsumerSyncStageSubscription
import com.amazonaws.amplify.generated.graphql.OnCreateMerchantSyncStageSubscription
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall
import com.androidadvance.topsnackbar.TSnackbar
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.earthling.atminput.ATMEditText
import com.earthling.atminput.Currency
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import kotlinx.android.synthetic.main.activity_validate_pin.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : BaseActivity(){

    lateinit var menuButton : ImageView
    lateinit var listViewRecycler : RecyclerView
    lateinit var cardViewRecycler : RecyclerView
    lateinit var readyToPay : TextView
    lateinit var aeropayTransparent : ImageView
    lateinit var headerLayout : RelativeLayout
    lateinit var beaconTransmitter: BeaconTransmitter
    lateinit var cardAdapter: HomeCardRecyclerView
    lateinit var bottomFragment: BottomSheetDialog
    var bleAdapter: BluetoothAdapter? = null

    var isBleSupported = false
    lateinit var subscriptionChannelWatcher: AppSyncSubscriptionCall<OnCreateMerchantConsumerSyncStageSubscription.Data>
    lateinit var subscriptionWatcher: AppSyncSubscriptionCall<OnCreateMerchantSyncStageSubscription.Data>
    val TAG = SignInScreenActivity::class.java!!.getSimpleName()
    lateinit var homeViewModel : HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = HomeViewModel()
        setContentView(com.aeropay_merchant.R.layout.activity_home)
        initialiseControls()

        saveMerchantId()
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

    private fun saveMerchantId() {
        var objModelManager = AeropayModelManager().getInstance().merchantProfileModel
        var merchantId = objModelManager.merchant.merchantId

        PrefKeeper.merchantId = merchantId
    }

    //setting onClick Listeners on views
    private fun setListeners() {
        menuButton.setOnClickListener(View.OnClickListener {
            launchActivity(NavigationMenuActivity::class.java)
        })
    }

    override fun onResume() {
        super.onResume()
        bleAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bleAdapter == null){
            var snackbar = TSnackbar.make(headerLayout, "Device not supported", TSnackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.BLACK);
            snackbar.setIconRight(R.drawable.ic_menu_close_clear_cancel, 36F); //Resize to bigger dp
            snackbar.setIconPadding(8)
            snackbar.setMaxWidth(3000)
            var snackbarView = snackbar.getView()
            snackbarView.setBackgroundColor(Color.parseColor("#34c1d7"))
            var textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
            textView.setTextColor(Color.BLACK)
            textView.textSize = 18F
            snackbar.show()
           /* var enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(enableBtIntent)*/
        }
        else{
            var result = BeaconTransmitter.checkTransmissionSupported(this)
            if(result == 0){
                var isBleEnabled = bleAdapter?.isEnabled
                if(isBleEnabled!!){
                    isBleSupported = true
                    if(PrefKeeper.minorId == -1){
                        createHitForUUID()
                    }
                    else{
                        startSharedAdvertisingBeaconWithString(PrefKeeper.deviceUuid!!,PrefKeeper.majorId,PrefKeeper.minorId, "AP Stores")
                    }
                }
            }
            else if(result == 5){
                cardViewRecycler.visibility = View.GONE
                aeropayTransparent.visibility = View.VISIBLE
                var snackbar = TSnackbar.make(headerLayout, "Bluetooth off. Cannot detect Consumers. Please enable your Bluetooth.", TSnackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.BLACK);
                snackbar.setIconRight(R.drawable.ic_menu_close_clear_cancel, 36F); //Resize to bigger dp
                snackbar.setIconPadding(8)
                snackbar.setMaxWidth(3000)
                var snackbarView = snackbar.getView()
                snackbarView.setBackgroundColor(Color.parseColor("#34c1d7"))
                var textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
                textView.setTextColor(Color.BLACK)
                textView.textSize = 18F
                snackbar.show()
            }
            else{
                showMsgToast("BLE is not supported in your device.")
            }
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        var consumerResponseArray = homeViewModel.getValues()
        cardAdapter = HomeCardRecyclerView(consumerResponseArray,this@HomeActivity)
        cardViewRecycler.adapter = cardAdapter

        cardAdapter.onItemClick = { pos, view ->
            onItemClick(pos,view)
        }
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
        headerLayout = findViewById(com.aeropay_merchant.R.id.bodyLayout)

        var text = "<font color=#06dab3>"+ homeViewModel.numberOfConsumers.toString() +"</font> <font color=#232323>ready to pay</font>"
        readyToPay.setText(Html.fromHtml(text))

        cardViewRecycler.visibility = View.GONE
    }

    // to check the login count of this user on this device
    private fun maintainUserLoginCount() {
        var initialLoginCount = PrefKeeper.logInCount
        var finalCount = initialLoginCount + 1
        PrefKeeper.logInCount = finalCount
    }

    fun creatBeaconTransmission(){
        var registerMerchantDevice = AeropayModelManager().getInstance().registerMerchantDevices

        PrefKeeper.deviceUuid = registerMerchantDevice.uuid as String
        PrefKeeper.majorId = registerMerchantDevice.majorID.toInt()
        PrefKeeper.minorId = registerMerchantDevice.minorID.toInt()

        startSharedAdvertisingBeaconWithString(registerMerchantDevice.uuid as String, registerMerchantDevice.majorID.toInt() , registerMerchantDevice.minorID.toInt(), "AP Stores")
    }

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
        val beaconParser = BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        beaconTransmitter = BeaconTransmitter(this, beaconParser)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        }
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

                    homeViewModel.setValues(response)

                    var consumerResponseArray = homeViewModel.getValues()

                    cardAdapter = HomeCardRecyclerView(consumerResponseArray,this@HomeActivity)
                    cardViewRecycler.adapter = cardAdapter

                    cardAdapter.onItemClick = { pos, view ->
                        onItemClick(pos,view)
                    }

                    homeViewModel.numberOfConsumers = homeViewModel.numberOfConsumers!! + 1

                    var text = "<font color=#06dab3>"+ homeViewModel.numberOfConsumers.toString() +"</font> <font color=#232323>ready to pay</font>"
                    readyToPay.setText(Html.fromHtml(text))
                }
            }

            override fun onFailure(e: ApolloException) {
                Log.e(TAG, "Subscription failure", e)
            }

            override fun onCompleted() {
                Log.d(TAG, "Subscription completed")
            }

        }
        val subscription = OnCreateMerchantSyncStageSubscription.builder().merchant_id(PrefKeeper.merchantId.toString()).build()
        subscriptionWatcher = ClientFactory.getInstance(this.applicationContext).subscribe(subscription)
        subscriptionWatcher.execute(subscriptionCallback)
    }

     fun onItemClick(position : Int,view: View) {
         homeViewModel.userEntered = ""
         var view = (this as FragmentActivity).layoutInflater.inflate(com.aeropay_merchant.R.layout.authorize_payment_landscape, null)
         bottomFragment = BottomSheetDialog(this)
         setValuesInDialog(view)
         bottomFragment.setContentView(view)
         bottomFragment.show()
    }

    private fun setValuesInDialog(view : View) {
        var etInput = view.findViewById(com.aeropay_merchant.R.id.amountEdit) as ATMEditText

        etInput.Currency   = Currency.USA
        etInput.setText("0")

        val pinButtonHandler = View.OnClickListener { v ->
            val pressedButton = v as CustomTextView
            homeViewModel.userEntered = homeViewModel.userEntered + pressedButton.text
            etInput.setText(homeViewModel.userEntered)
            etInput.setTextColor(Color.BLACK)
            etInput.setTypeface(Typeface.DEFAULT_BOLD)
        }

        var button0 = view.findViewById<View>(com.aeropay_merchant.R.id.button0) as CustomTextView
        button0!!.setOnClickListener(pinButtonHandler)

        var button1 = view.findViewById<View>(com.aeropay_merchant.R.id.button1) as CustomTextView
        button1!!.setOnClickListener(pinButtonHandler)

        var button2 = view.findViewById<View>(com.aeropay_merchant.R.id.button2) as CustomTextView
        button2!!.setOnClickListener(pinButtonHandler)

        var button3 = view.findViewById<View>(com.aeropay_merchant.R.id.button3) as CustomTextView
        button3!!.setOnClickListener(pinButtonHandler)

        var button4 = view.findViewById<View>(com.aeropay_merchant.R.id.button4) as CustomTextView
        button4!!.setOnClickListener(pinButtonHandler)

        var button5 = view.findViewById<View>(com.aeropay_merchant.R.id.button5) as CustomTextView
        button5!!.setOnClickListener(pinButtonHandler)

        var button6 = view.findViewById<View>(com.aeropay_merchant.R.id.button6) as CustomTextView
        button6!!.setOnClickListener(pinButtonHandler)

        var button7 = view.findViewById<View>(com.aeropay_merchant.R.id.button7) as CustomTextView
        button7!!.setOnClickListener(pinButtonHandler)

        var button8 = view.findViewById<View>(com.aeropay_merchant.R.id.button8) as CustomTextView
        button8!!.setOnClickListener(pinButtonHandler)

        var button9 = view.findViewById<View>(com.aeropay_merchant.R.id.button9) as CustomTextView
        button9!!.setOnClickListener(pinButtonHandler)

        var dropArrow = view.findViewById<View>(com.aeropay_merchant.R.id.downArrow) as ImageView
        dropArrow!!.setOnClickListener({
            bottomFragment.cancel()
        })

        var buttonDelete = view.findViewById<View>(com.aeropay_merchant.R.id.buttonDeleteBack) as CustomTextView
        buttonDelete!!.setOnClickListener(View.OnClickListener {
            var userEnteredLength = homeViewModel.userEntered!!.length
            if(userEnteredLength == 1){
                homeViewModel.userEntered = homeViewModel.userEntered!!.substring(0, homeViewModel.userEntered!!.length - 1)
                etInput.setText("0")
                etInput.setTypeface(Typeface.DEFAULT_BOLD)
                etInput.setTextColor(Color.LTGRAY)
            }
            else if (userEnteredLength > 1){
                homeViewModel.userEntered = homeViewModel.userEntered!!.substring(0, homeViewModel.userEntered!!.length - 1)
                etInput.setText(homeViewModel.userEntered)
            }
        }
        )

        var authorizeButton = view.findViewById<View>(com.aeropay_merchant.R.id.authoriseButton) as Button
        authorizeButton.setOnClickListener {
            var processTransaction = ProcessTransaction()
            processTransaction.type = "debit"
            processTransaction.fromMerchant = "1".toBigDecimal()
            processTransaction.merchantLocationId = PrefKeeper.merchantLocationId!!.toBigDecimal()
            processTransaction.amount = homeViewModel.userEntered!!.toBigDecimal()
            processTransaction.transactionDescription = "Aeropay Transaction"

            processTransaction.transactionId = "123456"
            processTransaction.debug = "1".toBigDecimal()

            var awsConnectionManager = AWSConnectionManager(this)
            awsConnectionManager.hitServer(DefineID().FETCH_MERCHANT_PROCESS_TRANSACTION,this,processTransaction)
        }
    }

    override fun onStop() {
        stopSharedAdvertisingBeacon()
        super.onStop()
    }

    fun sendProcessTransaction() {
        bottomFragment.cancel()
    }
}
