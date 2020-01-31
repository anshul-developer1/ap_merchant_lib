
package com.aeropay_merchant.Model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class FetchMerchantLocationModel {

    @SerializedName("locations")
    private List<Location> mLocations;
    @SerializedName("success")
    private Object mSuccess;

    public List<Location> getLocations() {
        return mLocations;
    }

    public void setLocations(List<Location> locations) {
        mLocations = locations;
    }

    public Object getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Object success) {
        mSuccess = success;
    }

}
