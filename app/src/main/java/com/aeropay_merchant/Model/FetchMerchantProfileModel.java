
package com.aeropay_merchant.Model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class FetchMerchantProfileModel {

    @SerializedName("merchant")
    private Merchant mMerchant;
    @SerializedName("success")
    private Object mSuccess;

    public Merchant getMerchant() {
        return mMerchant;
    }

    public void setMerchant(Merchant merchant) {
        mMerchant = merchant;
    }

    public Object getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Object success) {
        mSuccess = success;
    }

}
