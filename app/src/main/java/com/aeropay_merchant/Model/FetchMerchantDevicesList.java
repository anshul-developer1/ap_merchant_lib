
package com.aeropay_merchant.Model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class FetchMerchantDevicesList {

    @SerializedName("devices")
    private List<Device> mDevices;
    @SerializedName("success")
    private Object mSuccess;

    public List<Device> getDevices() {
        return mDevices;
    }

    public void setDevices(List<Device> devices) {
        mDevices = devices;
    }

    public Object getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Object success) {
        mSuccess = success;
    }

}
