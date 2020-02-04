package com.aeropay_merchant.ViewModel

import androidx.lifecycle.ViewModel
import com.amazonaws.amplify.generated.graphql.OnCreateMerchantSyncStageSubscription
import com.apollographql.apollo.api.Response

class HomeViewModel : ViewModel() {

    val userName: ArrayList<String> = ArrayList()
    var numberOfConsumers : Int? = 0
    var userEntered: String? = ""

    fun setValues(response: Response<OnCreateMerchantSyncStageSubscription.Data>){
        userName.add(response.data()!!.onCreateMerchantSyncStage()!!.__typename())
    }

    fun getValues() : ArrayList<String>{
        return userName
    }
}