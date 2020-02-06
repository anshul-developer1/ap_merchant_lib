package com.aeropay_merchant.activity


import AP.model.ProcessTransaction
import AP.model.RegisterMerchantDevice
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aeropay_merchant.Model.AeropayModelManager
import com.aeropay_merchant.adapter.HomeListRecyclerView
import com.aeropay_merchant.communication.AWSConnectionManager
import com.aeropay_merchant.communication.DefineID
import org.altbeacon.beacon.*
import android.os.Build
import org.altbeacon.beacon.BeaconParser
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.*
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.aeropay_merchant.Model.CreateSyncPayload
import com.aeropay_merchant.Utilities.*
import com.aeropay_merchant.ViewModel.HomeViewModel
import com.aeropay_merchant.adapter.HomeCardRecyclerView
import com.aeropay_merchant.view.CustomTextView
import com.amazonaws.amplify.generated.graphql.OnCreateMerchantSyncSubscription
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.bumptech.glide.Glide
import com.earthling.atminput.ATMEditText
import com.earthling.atminput.Currency
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson


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
    lateinit var mReceiver: BroadcastReceiver
    lateinit var subscriptionWatcher: AppSyncSubscriptionCall<OnCreateMerchantSyncSubscription.Data>
    lateinit var homeViewModel : HomeViewModel
    lateinit var txnID : String
    var bleAdapter: BluetoothAdapter? = null
    var isBleSupported = false
    var isBillSend = true
    var selectedPosition : Int? = -1
    val TAG = SignInScreenActivity::class.java!!.getSimpleName()
    var objModelManager = AeropayModelManager().getInstance()
   /* var count : Int? = 0
    var listCount : Int? = 0*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = HomeViewModel()
        setContentView(com.aeropay_merchant.R.layout.activity_home)
        initialiseControls()

        objModelManager.subscriptionPayloadForList.payloadList = mutableListOf()
        objModelManager.createSyncPayload.payloadList = mutableListOf()
        saveMerchantId()
        GlobalMethods().getDeviceToken(applicationContext)
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
            it.isClickable = false
            it.isEnabled = false
            launchActivity(NavigationMenuActivity::class.java)
        })
    }

    override fun onResume() {
        super.onResume()
        menuButton.isClickable = true
        menuButton.isEnabled = true
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                var action = p1!!.action
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    if(p1.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF){
                        setUIWithBT()
                    }
                    else if(p1.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON){
                        setUIWithBT()
                    }
                }
            }
        }
        registerReceiver(mReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        setUIWithBT()
    }

    fun setUIWithBT(){
        bleAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bleAdapter == null){
            GlobalMethods().createSnackBar(headerLayout,"Device not supported")
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
                else{
                    cardViewRecycler.visibility = View.GONE
                    aeropayTransparent.visibility = View.VISIBLE
                    GlobalMethods().createSnackBar(headerLayout,"Bluetooth off. Cannot detect Consumers. Please enable your Bluetooth.")
                }
            }
            else if(result == 5){
                cardViewRecycler.visibility = View.GONE
                aeropayTransparent.visibility = View.VISIBLE
                GlobalMethods().createSnackBar(headerLayout,"Bluetooth off. Cannot detect Consumers. Please enable your Bluetooth.")
            }
            else{
                GlobalMethods().createSnackBar(headerLayout,"BLE is not supported in your device.")
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
        cardAdapter = HomeCardRecyclerView(objModelManager.createSyncPayload.payloadList,this@HomeActivity)
        cardViewRecycler.adapter = cardAdapter
        listViewRecycler.adapter = HomeListRecyclerView(objModelManager.subscriptionPayloadForList.payloadList,this)
    }

    //setting up hardcoded Recycler Adapter
    private fun setupView() {
        listViewRecycler.adapter = HomeListRecyclerView(objModelManager.subscriptionPayloadForList.payloadList,this)
    }

    //inflating UI controls
    private fun initialiseControls() {
        menuButton = findViewById(com.aeropay_merchant.R.id.back_button)
        listViewRecycler = findViewById(com.aeropay_merchant.R.id.recyclerListView)
        cardViewRecycler = findViewById(com.aeropay_merchant.R.id.cardRecyclerView)
        readyToPay = findViewById(com.aeropay_merchant.R.id.readyToPayText)
        aeropayTransparent = findViewById(com.aeropay_merchant.R.id.aeropayTranparentLogo)
        headerLayout = findViewById(com.aeropay_merchant.R.id.bodyLayout)

        listViewRecycler.layoutManager = LinearLayoutManager(this)



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
                    GlobalMethods().createSnackBar(headerLayout,"BLE connection error")
                }

                override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                    GlobalMethods().createSnackBar(headerLayout,"BLE connection successful")
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
                    this.subscriptionWatcher.cancel()
                } catch (ex: Exception) {
                }
            }
        }
    }

    private fun startSubscription() {
        val subscriptionCallback = object : AppSyncSubscriptionCall.Callback<OnCreateMerchantSyncSubscription.Data> {
            override fun onResponse(response: Response<OnCreateMerchantSyncSubscription.Data>) {
                runOnUiThread {
                    isBillSend = true
                    var payload = response.data()!!.onCreateMerchantSync()!!.payload().toString()
                    var stringOutput = Gson().toJson(payload)

                    var apStatusIndex = stringOutput.indexOf("APStatus")
                    var transactionIdIndex = stringOutput.indexOf("transactionId")

                    var apStatus = stringOutput.substring(apStatusIndex+22,transactionIdIndex-14)

                    if(apStatus.equals("initiated")){
                        cardViewRecycler.visibility = View.VISIBLE
                        aeropayTransparent.visibility = View.GONE

                        cardViewRecycler.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL, false)

                        homeViewModel.setValues(response)
                        homeViewModel.setPayload(response)

                        var transactionIdIndex =stringOutput.indexOf("transactionId")
                        var profileImageIndex =stringOutput.indexOf("profileImage")

                        txnID = stringOutput.substring(transactionIdIndex+27,profileImageIndex-14)

                        var userNameIndex =stringOutput.indexOf("userName")
                        var expirationTimeIndex =stringOutput.indexOf("expirationTime")
                        var apStatusIndex = stringOutput.indexOf("APStatus")

                        var userName = stringOutput.substring(userNameIndex+23,apStatusIndex-14)
                        var profileImageUrl = stringOutput.substring(profileImageIndex+27,expirationTimeIndex-14)

                        var createSyncPayload = CreateSyncPayload()

                        createSyncPayload.userName = userName
                        createSyncPayload.status = apStatus
                        createSyncPayload.profileImage = profileImageUrl
                        createSyncPayload.transactionId = txnID
                        createSyncPayload.expirationTime = ""

                        objModelManager.createSyncPayload.payloadList.add(createSyncPayload)

                        cardAdapter = HomeCardRecyclerView(objModelManager.createSyncPayload.payloadList,this@HomeActivity)
                        cardViewRecycler.adapter = cardAdapter

                        cardAdapter.onItemClick = { pos, view ->
                            onItemClick(pos,view)
                        }

                        homeViewModel.numberOfConsumers = homeViewModel.numberOfConsumers!! + 1

                        var text = "<font color=#06dab3>"+ homeViewModel.numberOfConsumers.toString() +"</font> <font color=#232323>ready to pay</font>"
                        readyToPay.setText(Html.fromHtml(text))
                    }
                    else if(apStatus.equals("processed")){
                        var listSize = objModelManager.subscriptionPayloadForList.payloadList.size
                        var profileImageIndex =stringOutput.indexOf("profileImage")
                        txnID = stringOutput.substring(transactionIdIndex+27,profileImageIndex-14)
                        for(i in 0..listSize - 1){
                            if(txnID.equals(objModelManager.subscriptionPayloadForList.payloadList[i].transactionId)){
                                objModelManager.subscriptionPayloadForList.payloadList[i].status = apStatus
                            }
                        }
                        setupView()
                    }
                    else if(apStatus.equals("cancelled")){

                        var listSize = objModelManager.subscriptionPayloadForList.payloadList.size
                        var profileImageIndex =stringOutput.indexOf("profileImage")
                        txnID = stringOutput.substring(transactionIdIndex+27,profileImageIndex-14)

                        for(i in 0..listSize - 1){
                            if(txnID.equals(objModelManager.subscriptionPayloadForList.payloadList[i].transactionId)){
                                isBillSend = false
                                objModelManager.subscriptionPayloadForList.payloadList[i].status = apStatus
                            }
                        }

                        if((isBillSend)){
                            var listSizeCard = objModelManager.createSyncPayload.payloadList.size

                            for(i in 0..listSizeCard - 1){
                                if(txnID.equals(objModelManager.createSyncPayload.payloadList[i].transactionId)){
                                    isBillSend = false
                                    objModelManager.createSyncPayload.payloadList[i].status = apStatus
                                    objModelManager.subscriptionPayloadForList.payloadList.add(objModelManager.createSyncPayload.payloadList[i])

                                    objModelManager.createSyncPayload.payloadList.removeAt(i)
                                    cardAdapter.setValues(objModelManager.createSyncPayload.payloadList)

                                    homeViewModel.numberOfConsumers = homeViewModel.numberOfConsumers!! - 1
                                    var text = "<font color=#06dab3>"+ homeViewModel.numberOfConsumers.toString() +"</font> <font color=#232323>ready to pay</font>"
                                    readyToPay.setText(Html.fromHtml(text))
                                }
                            }
                        }
                        setupView()
                    }
                }
            }

            override fun onFailure(e: ApolloException) {
                Log.e(TAG, "Subscription failure", e)
            }

            override fun onCompleted() {
                Log.d(TAG, "Subscription completed")
            }

        }
        val subscription = OnCreateMerchantSyncSubscription.builder().merchant_id(PrefKeeper.merchantLocationDeviceId.toString()).build()
        subscriptionWatcher = ClientFactory.getInstance(this.applicationContext).subscribe(subscription)
        subscriptionWatcher.execute(subscriptionCallback)
    }

     fun onItemClick(position : Int,view: View) {
         homeViewModel.userEntered = ""
         var view = (this as FragmentActivity).layoutInflater.inflate(com.aeropay_merchant.R.layout.authorize_payment, null)
         bottomFragment = BottomSheetDialog(this)
         setValuesInDialog(view,position)
         bottomFragment.setContentView(view)
         bottomFragment.show()
    }

    private fun setValuesInDialog(view: View, position: Int) {
        var etInput = view.findViewById(com.aeropay_merchant.R.id.amountEdit) as ATMEditText
        var userImage = view.findViewById(com.aeropay_merchant.R.id.userImage) as ImageView
        var userName = view.findViewById(com.aeropay_merchant.R.id.userName) as CustomTextView

        userName.setText(objModelManager.createSyncPayload.payloadList[position].userName)
        Glide.with(this).load(objModelManager.createSyncPayload.payloadList[position].profileImage).into(userImage)

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

            var amount = etInput.text.toString()
            processTransaction.transactionDescription = "Aeropay Transaction"
            processTransaction.amount = amount.replace("$","").toBigDecimal()
            processTransaction.debug = "0".toBigDecimal()
            processTransaction.transactionId = txnID

            homeViewModel.userEntered = amount

            selectedPosition = position

            var awsConnectionManager = AWSConnectionManager(this)
            awsConnectionManager.hitServer(DefineID().FETCH_MERCHANT_PROCESS_TRANSACTION,this,processTransaction)
        }
    }

    override fun onStop() {
        unregisterReceiver(mReceiver)
        stopSharedAdvertisingBeacon()
        super.onStop()
    }

    fun sendProcessTransaction() {
        var inProgressUserDetail = objModelManager.createSyncPayload.payloadList[selectedPosition!!]

        objModelManager.createSyncPayload.payloadList.removeAt(selectedPosition!!)
        cardAdapter.setValues(objModelManager.createSyncPayload.payloadList)

        homeViewModel.numberOfConsumers = homeViewModel.numberOfConsumers!! - 1
        var text = "<font color=#06dab3>"+ homeViewModel.numberOfConsumers.toString() +"</font> <font color=#232323>ready to pay</font>"
        readyToPay.setText(Html.fromHtml(text))

        var createSyncPayload = CreateSyncPayload()

        createSyncPayload.userName = inProgressUserDetail.userName
        createSyncPayload.status = "in-progress"
        createSyncPayload.profileImage = inProgressUserDetail.profileImage
        createSyncPayload.transactionId = inProgressUserDetail.transactionId
        createSyncPayload.expirationTime = homeViewModel.userEntered

        objModelManager.subscriptionPayloadForList.payloadList.add(createSyncPayload)
        setupView()
        bottomFragment.cancel()
    }

}
