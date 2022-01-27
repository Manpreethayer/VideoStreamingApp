package com.example.hp.teacher.Camera.network;


import com.example.hp.teacher.Camera.AppConfig;
import com.example.hp.teacher.Camera.model.CategoryRespModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ItemService {
    @GET("/api/v3/" + AppConfig.API_KEY)
    Call<CategoryRespModel> requestItem(@QueryMap Map<String, String> params);


}
