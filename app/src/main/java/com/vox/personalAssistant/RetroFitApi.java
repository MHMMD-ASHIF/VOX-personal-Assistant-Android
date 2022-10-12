package com.vox.personalAssistant;




import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetroFitApi {
    @GET
    Call<MsgModel> getMessage(@Url String url);
}
