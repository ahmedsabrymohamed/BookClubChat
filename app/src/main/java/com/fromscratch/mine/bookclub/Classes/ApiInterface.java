package com.fromscratch.mine.bookclub.Classes;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("send.php")
    Call<Response> send(@Query("title") String senderName
            , @Query("message") String message
            , @Query("chatID") String chatID
            , @Query("senderUid") String senderUid
            , @Query("chatName") String chatName);


}
