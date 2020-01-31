package com.aeropay_merchant.Utilities

class ConstantsStrings {

    //// AWS Cognito Login Keys

    val aws_client_secret_id = "v07gs7famtp16d93kt8e898dkh29brkikf63mp6d59djsvqglf"
    val aws_userpool_id = "us-east-1_ndcMY47H8"
    val aws_identitypool_id = "us-east-1:de663d17-7b05-4949-a08a-ff2d1bb9b387"
    val aws_client_id = "4uolkfn4mu3ii8f0scgqpgjiep"
    val userPoolLoginType = "cognito-idp.us-east-1.amazonaws.com/us-east-1_ndcMY47H8"

    //// SharePreferences Key
    val usernameIv = "usernameIv"
    val passwordIv = "passwordIv"
    val username = "username"
    val password = "password"
    val loginCount = "loginCount"
    val pinEnabled = "pinEnabled"
    val isLoggedin = "isLogin"
    val pinValue = "pinValue"
    val storeName = "storeName"
    val deviceName = "deviceName"
    val deviceToken = "deviceToken"
    val merchantDeviceId = "merchantDeviceId"
    val merchantLocationId = "merchantLocationId"
    val majorId = "majorId"
    val minorId = "minorId"
    val deviceUUID = "deviceUUID"


    var isPinActivityName = "isPinActivityName"
    var noValue = "NO VALUE"

    val isValidatePinActivity = "isValidatePinActivity"
    val isSplashActivity = "isSplashActivity"
    val isSignInActivity = "isSignInActivity"

}