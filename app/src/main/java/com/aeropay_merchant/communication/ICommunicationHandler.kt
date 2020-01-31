package com.aeropay_merchant.communication

interface ICommunicationHandler {

    fun onSuccess(outputParms: Int)

    fun onFailure(outputParms: Int)

}