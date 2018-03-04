package com.fromscratch.mine.bookclub.Classes;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  initialize Retrofit Library
 */

public class APIClient {

    private final static String BASE_URL="https://capstoneprojectnotification.000webhostapp.com/";
    private static Retrofit retrofit=null;

    public  static Retrofit getClient(){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

