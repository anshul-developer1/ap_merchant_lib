package com.aeropay_merchant.Model;

import androidx.lifecycle.ViewModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubscriptionPayload extends ViewModel {

    @SerializedName("createSyncPayload")
    private List<CreateSyncPayload> payloadList;

    public List<CreateSyncPayload> getPayloadList() {
        return payloadList;
    }

    public void setPayloadList(List<CreateSyncPayload> payloadList) {
        this.payloadList = payloadList;
    }
}
