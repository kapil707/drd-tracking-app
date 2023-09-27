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
    @POST("drdtrackingapp_api/api01/update_user_location_api")
    Call<ResponseBody> update_user_location(@Field("user_code") String user_code,@Field("firebase_token") String firebase_token,@Field("getLatitude") String getLatitude,@Field("getLongitude") String getLongitude,@Field("getdate") String getdate,@Field("gettime") String gettime);

    @POST("drdtrackingapp_api/api01/insert_firebase_token_api")
    Call<ResponseBody> insert_firebase_token(@Body RequestBody requestBody);


    @FormUrlEncoded
    @POST("drdtrackingapp_api/api01/slider_api")
    Call<ResponseBody> slider_api(@Field("submit") String submit);

    @FormUrlEncoded
    @POST("drdtrackingapp_api/api01/login_api")
    Call<ResponseBody> loginUser(@Field("submit") String submit,@Field("user_name") String user_name,@Field("password") String password,@Field("firebase_token") String firebase_token);



    @FormUrlEncoded
    @POST("drdtrackingapp_api/api01/test")
    Call<ResponseBody> testing(@Field("test") String test);

    //Call<ResponseBody> postData(@Body RequestBody requestBody);
}
