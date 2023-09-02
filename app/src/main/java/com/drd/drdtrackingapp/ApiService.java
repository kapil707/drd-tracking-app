package com.drd.drdtrackingapp;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    //@FormUrlEncoded
    /*@POST("web_api/api01/medicines_last_order_api")
    Call<ResponseBody> getUserInformation(@Field("user_type") String user_type,@Field("user_altercode") String user_altercode,@Field("user_password") String user_password);
*/
    @FormUrlEncoded
    @POST("drdtrackingapp_api/api01/update_api")
    Call<ResponseBody> update_api(@Field("user") String user,@Field("getLatitude") String getLatitude,@Field("getLongitude") String getLongitude);

    @POST("drdtrackingapp_api/api01/insert_getfcmtoken")
    Call<ResponseBody> postRequest2(@Body RequestBody requestBody);

    @POST("drdtrackingapp_api/api01/login")
    Call<Login_model> post_login(@Body Login_model Loginmodel);

    //Call<ResponseBody> postData(@Body RequestBody requestBody);
}
