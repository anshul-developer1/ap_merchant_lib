package com.aeropay_merchant.Model

class AeropayModelManager {

    companion object modelManager {
        var objModelManager: AeropayModelManager? = null
    }

    var merchantLocationsModel: FetchMerchantLocationModel = FetchMerchantLocationModel()
    var merchantDevicesModel: FetchMerchantDevicesList = FetchMerchantDevicesList()
    var merchantProfileModel: FetchMerchantProfileModel = FetchMerchantProfileModel()
    var registerMerchantDevices: RegisterMerchantDeviceResponse = RegisterMerchantDeviceResponse()
    var createSyncPayload: SubscriptionPayload = SubscriptionPayload()
    var subscriptionPayloadForList: SubscriptionPayloadDataForList = SubscriptionPayloadDataForList()

    constructor() {}

    fun getInstance(): AeropayModelManager {

        if (objModelManager != null) {
            return objModelManager as AeropayModelManager

        } else {
            objModelManager = AeropayModelManager()
            return objModelManager as AeropayModelManager
        }
    }

}