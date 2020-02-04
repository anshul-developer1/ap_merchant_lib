package com.aeropay_merchant.communication;


import AP.model.AllMerchantLocations;
import AP.model.BankAccountID;
import AP.model.BankAccountsResponse;
import AP.model.CreateTransaction;
import AP.model.Empty;
import AP.model.FundingSourcesResponse;
import AP.model.IAVTokenResponse;
import AP.model.LinkAccountFromPlaid;
import AP.model.MerchantLocationDevices;
import AP.model.MerchantLocationDevicesResponse;
import AP.model.MerchantLocationTransactions;
import AP.model.MerchantLocationsResponse;
import AP.model.MerchantResponse;
import AP.model.MerchantTransactionsResponse;
import AP.model.ProcessTransaction;
import AP.model.RegisterBankAccount;
import AP.model.RegisterBankAccountResponse;
import AP.model.RegisterMerchant;
import AP.model.RegisterMerchantDevice;
import AP.model.RegisterMerchantDeviceResponse;
import AP.model.RegisterUser;
import AP.model.RegisterUserDevice;
import AP.model.RewardsHistoryResponse;
import AP.model.RewardsResponse;
import AP.model.StandardResponse;
import AP.model.TransactionID;
import AP.model.TransactionResponse;
import AP.model.TransactionsHistoryResponse;
import AP.model.UserResponse;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.mobileconnectors.apigateway.annotation.Operation;
import com.amazonaws.mobileconnectors.apigateway.annotation.Service;

@Service(
        endpoint = "https://mrz8t2xq6g.execute-api.us-east-1.amazonaws.com/dev"
)
interface AeropayProdClient {
    ApiResponse execute(ApiRequest var1);

    @Operation(
            path = "/createTransaction",
            method = "POST"
    )
    TransactionResponse createTransactionPost(CreateTransaction var1);

    @Operation(
            path = "/currentRewards",
            method = "POST"
    )
    RewardsResponse currentRewardsPost();

    @Operation(
            path = "/deleteBankAccount",
            method = "POST"
    )
    StandardResponse deleteBankAccountPost(BankAccountID var1);

    @Operation(
            path = "/fetchAllMerchantLocations",
            method = "POST"
    )
    MerchantLocationsResponse fetchAllMerchantLocationsPost(AllMerchantLocations var1);

    @Operation(
            path = "/fetchBankAccounts",
            method = "POST"
    )
    BankAccountsResponse fetchBankAccountsPost();

    @Operation(
            path = "/fetchConfig",
            method = "POST"
    )
    Empty fetchConfigPost();

    @Operation(
            path = "/fetchConfigMerchant",
            method = "POST"
    )
    Empty fetchConfigMerchantPost();

    @Operation(
            path = "/fetchDevicesRegistration",
            method = "POST"
    )
    Empty fetchDevicesRegistrationPost();

    @Operation(
            path = "/fetchDevicesRegistrationStatus",
            method = "POST"
    )
    Empty fetchDevicesRegistrationStatusPost();

    @Operation(
            path = "/fetchMerchant",
            method = "POST"
    )
    MerchantResponse fetchMerchantPost();

    @Operation(
            path = "/fetchMerchantLocationDevices",
            method = "POST"
    )
    MerchantLocationDevicesResponse fetchMerchantLocationDevicesPost(MerchantLocationDevices var1);

    @Operation(
            path = "/fetchMerchantLocationTransactions",
            method = "POST"
    )
    MerchantTransactionsResponse fetchMerchantLocationTransactionsPost(MerchantLocationTransactions var1);

    @Operation(
            path = "/fetchMerchantLocations",
            method = "POST"
    )
    MerchantLocationsResponse fetchMerchantLocationsPost();

    @Operation(
            path = "/fetchUser",
            method = "POST"
    )
    UserResponse fetchUserPost();

    @Operation(
            path = "/fetchUser",
            method = "OPTIONS"
    )
    Empty fetchUserOptions();

    @Operation(
            path = "/iavTokenForUser",
            method = "POST"
    )
    IAVTokenResponse iavTokenForUserPost();

    @Operation(
            path = "/iavTokenForUser",
            method = "OPTIONS"
    )
    Empty iavTokenForUserOptions();

    @Operation(
            path = "/linkAccountFromPlaid",
            method = "POST"
    )
    RegisterBankAccountResponse linkAccountFromPlaidPost(LinkAccountFromPlaid var1);

    @Operation(
            path = "/merchantForDevice",
            method = "POST"
    )
    Empty merchantForDevicePost();

    @Operation(
            path = "/merchantProcessTransaction",
            method = "POST"
    )
    StandardResponse merchantProcessTransactionPost(ProcessTransaction var1);

    @Operation(
            path = "/registerBankAccount",
            method = "POST"
    )
    RegisterBankAccountResponse registerBankAccountPost(RegisterBankAccount var1);

    @Operation(
            path = "/registerBankAccount",
            method = "OPTIONS"
    )
    Empty registerBankAccountOptions();

    @Operation(
            path = "/registerMerchant",
            method = "POST"
    )
    MerchantResponse registerMerchantPost(RegisterMerchant var1);

    @Operation(
            path = "/registerMerchantLocationDevice",
            method = "POST"
    )
    RegisterMerchantDeviceResponse registerMerchantLocationDevicePost(RegisterMerchantDevice var1);

    @Operation(
            path = "/registerUser",
            method = "POST"
    )
    UserResponse registerUserPost(RegisterUser var1);

    @Operation(
            path = "/registerUserDevice",
            method = "POST"
    )
    StandardResponse registerUserDevicePost(RegisterUserDevice var1);

    @Operation(
            path = "/removeTransaction",
            method = "POST"
    )
    StandardResponse removeTransactionPost(TransactionID var1);

    @Operation(
            path = "/rewardsHistory",
            method = "POST"
    )
    RewardsHistoryResponse rewardsHistoryPost();

    @Operation(
            path = "/selectBankAccount",
            method = "POST"
    )
    StandardResponse selectBankAccountPost(BankAccountID var1);

    @Operation(
            path = "/sendBillTransaction",
            method = "POST"
    )
    StandardResponse sendBillTransactionPost(ProcessTransaction var1);

    @Operation(
            path = "/unRegisterMerchantLocationDevice",
            method = "POST"
    )
    Empty unRegisterMerchantLocationDevicePost();

    @Operation(
            path = "/updateProfile",
            method = "POST"
    )
    Empty updateProfilePost();

    @Operation(
            path = "/userFundingSources",
            method = "POST"
    )
    FundingSourcesResponse userFundingSourcesPost();

    @Operation(
            path = "/userProcessTransaction",
            method = "POST"
    )
    StandardResponse userProcessTransactionPost(ProcessTransaction var1);

    @Operation(
            path = "/userTransactionHistory",
            method = "POST"
    )
    TransactionsHistoryResponse userTransactionHistoryPost();

    @Operation(
            path = "/webhooks",
            method = "POST"
    )
    Empty webhooksPost();
}
