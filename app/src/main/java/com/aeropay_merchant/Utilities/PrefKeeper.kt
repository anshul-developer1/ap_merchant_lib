package com.aeropay_merchant.Utilities

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object PrefKeeper {

    private var prefs: SharedPreferences? = null

    var logInCount: Int
        get() = prefs!!.getInt(ConstantsStrings().loginCount, 0)
        set(loginCount) = prefs!!.edit().putInt(ConstantsStrings().loginCount, loginCount).apply()

    var minorId: Int
        get() = prefs!!.getInt(ConstantsStrings().minorId, -1)
        set(minorId) = prefs!!.edit().putInt(ConstantsStrings().minorId, minorId).apply()

    var majorId: Int
        get() = prefs!!.getInt(ConstantsStrings().majorId, -1)
        set(majorId) = prefs!!.edit().putInt(ConstantsStrings().majorId, majorId).apply()

    var deviceUuid: String?
        get() = prefs!!.getString(ConstantsStrings().deviceUUID, ConstantsStrings().noValue)
        set(deviceUuid) = prefs!!.edit().putString(ConstantsStrings().deviceUUID, deviceUuid).apply()

    var merchantId: Int
        get() = prefs!!.getInt(ConstantsStrings().merchantId, 0)
        set(merchantId) = prefs!!.edit().putInt(ConstantsStrings().merchantId, merchantId).apply()

    var pinValue: String?
        get() = prefs!!.getString(ConstantsStrings().pinValue,ConstantsStrings().noValue)
        set(pinValue) = prefs!!.edit().putString(ConstantsStrings().pinValue, pinValue).apply()

    var deviceToken: String?
        get() = prefs!!.getString(ConstantsStrings().deviceToken,ConstantsStrings().noValue)
        set(deviceToken) = prefs!!.edit().putString(ConstantsStrings().deviceToken, deviceToken).apply()

    var isPinEnabled: Boolean
        get() = prefs!!.getBoolean(ConstantsStrings().pinEnabled, false)
        set(pinEnabled) = prefs!!.edit().putBoolean(ConstantsStrings().pinEnabled, pinEnabled).apply()

    var isLoggedIn: Boolean
        get() = prefs!!.getBoolean(ConstantsStrings().isLoggedin, false)
        set(isLogin) = prefs!!.edit().putBoolean(ConstantsStrings().isLoggedin, isLogin).apply()

    var storeName: String?
        get() = prefs?.getString(ConstantsStrings().storeName, ConstantsStrings().noValue)
        set(storeName) = prefs!!.edit().putString(ConstantsStrings().storeName, storeName).apply()

    var deviceName: String?
        get() = prefs?.getString(ConstantsStrings().deviceName, ConstantsStrings().noValue)
        set(deviceName) = prefs!!.edit().putString(ConstantsStrings().deviceName, deviceName).apply()

    var merchantDeviceId : Int?
        get() = prefs?.getInt(ConstantsStrings().merchantDeviceId, 0)
        set(devicePosition) = prefs!!.edit().putInt(ConstantsStrings().merchantDeviceId, devicePosition!!).apply()

    var merchantLocationId : Int?
        get() = prefs?.getInt(ConstantsStrings().merchantLocationId, 0)
        set(devicePosition) = prefs!!.edit().putInt(ConstantsStrings().merchantLocationId, devicePosition!!).apply()

    var merchantLocationDeviceId : Int?
        get() = prefs?.getInt(ConstantsStrings().merchantLocationDeviceId, 0)
        set(devicePosition) = prefs!!.edit().putInt(ConstantsStrings().merchantLocationDeviceId, devicePosition!!).apply()

    var username: String?
        get() = prefs?.getString(ConstantsStrings().username, ConstantsStrings().noValue)
        set(username) = prefs!!.edit().putString(ConstantsStrings().username, username).apply()

    var password: String?
        get() = prefs?.getString(ConstantsStrings().password, null)
        set(token) = prefs!!.edit().putString(ConstantsStrings().password, token).apply()

    var usernameIV: String?
        get() = prefs?.getString(ConstantsStrings().usernameIv, null)
        set(usernameIv) = prefs!!.edit().putString(ConstantsStrings().usernameIv, usernameIv).apply()

    var passwordIV: String?
        get() = prefs?.getString(ConstantsStrings().passwordIv, null)
        set(passwordIv) = prefs!!.edit().putString(ConstantsStrings().passwordIv, passwordIv).apply()

    fun init(context: Context) {
        if (prefs == null)
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun clear() = prefs?.edit()?.clear()?.apply()

}