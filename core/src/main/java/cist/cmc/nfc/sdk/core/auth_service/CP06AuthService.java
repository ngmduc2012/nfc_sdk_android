package cist.cmc.nfc.sdk.core.auth_service;

import java.util.List;

import cist.cmc.nfc.sdk.core.models.cp06_auth.CP06Request;
import cist.cmc.nfc.sdk.core.models.cp06_auth.CP06Response;
import cist.cmc.nfc.sdk.core.models.cp06_auth.Result;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CP06AuthService {
    @Headers("Content-Type: application/json")
    @POST("/restservice/")
    Call<List<CP06Response>> cp06Auth(@Body CP06Request cp06Request);
}