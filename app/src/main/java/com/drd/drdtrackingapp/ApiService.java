package com.drd.drdtrackingapp;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("drd_master_api/api01/test")
    Call<ResponseBody> testing(@Field("test") String test);

    @FormUrlEncoded
    @POST("drd_master_api/api01/get_login_api")
    Call<ResponseBody> get_login_api(@Field("api_key") String api_key,@Field("user_name") String user_name,@Field("password") String password,@Field("firebase_token") String firebase_token);

    @FormUrlEncoded
    @POST("drd_master_api/api01/get_slider_api")
    Call<ResponseBody> get_slider_api(@Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("drd_master_api/api01/update_firebase_token_api")
    Call<ResponseBody> update_firebase_token_api(@Field("api_key") String api_key,@Field("user_code") String user_code,@Field("user_altercode") String user_altercode,@Field("firebase_token") String firebase_token,@Field("latitude") String latitude,@Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("drd_master_api/api01/update_tracking_api")
    Call<ResponseBody> update_tracking_api(@Field("api_key") String api_key,@Field("user_code") String user_code,@Field("user_altercode") String user_altercode,@Field("firebase_token") String firebase_token,@Field("latitude") String getLatitude,@Field("longitude") String getLongitude,@Field("getdate") String getdate,@Field("gettime") String gettime);

    @FormUrlEncoded
    @POST("drd_master_api/api01/get_delivery_order_api")
    Call<ResponseBody> get_delivery_order_api(@Field("api_key") String api_key,@Field("user_altercode") String user_altercode);

    @FormUrlEncoded
    @POST("drd_master_api/api01/get_delivery_order_done_api")
    Call<ResponseBody> get_delivery_order_done_api(@Field("api_key") String api_key,@Field("user_altercode") String user_altercode);

    @FormUrlEncoded
    @POST("drd_master_api/api01/get_delivery_order_photo_api")
    Call<ResponseBody> get_delivery_order_photo_api(@Field("api_key") String api_key,@Field("user_altercode") String user_altercode,@Field("chemist_id") String chemist_id,@Field("gstvno") String gstvno);

    @FormUrlEncoded
    @POST("drd_master_api/api01/upload_delivery_order_completed_api")
    Call<ResponseBody> upload_delivery_order_completed_api(@Field("api_key") String api_key,@Field("user_altercode") String user_altercode,@Field("chemist_id") String chemist_id,@Field("gstvno") String gstvno,@Field("message") String message,@Field("getLatitude") String getLatitude,@Field("getLongitude") String getLongitude);

    @FormUrlEncoded
    @POST("drd_master_api/api01/upload_attendance_api")
    Call<ResponseBody> upload_attendance_api(@Field("api_key") String api_key,@Field("user_altercode") String user_altercode,@Field("latitude") String latitude,@Field("longitude") String longitude,@Field("date") String date,@Field("time") String time,@Field("token_key") String token_key);


    //Call<ResponseBody> postData(@Body RequestBody requestBody);
}
