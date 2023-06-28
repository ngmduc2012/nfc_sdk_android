package cist.cmc.nfc.sdk.core;

import androidx.annotation.Keep;

import java.util.List;

import cist.cmc.nfc.sdk.core.auth_service.CP06AuthService;
import cist.cmc.nfc.sdk.core.models.cp06_auth.CP06Request;
import cist.cmc.nfc.sdk.core.models.cp06_auth.CP06Response;
import cist.cmc.nfc.sdk.core.models.cp06_auth.Result;
import retrofit2.Call;
import retrofit2.Retrofit;


@Keep
public class CP06Authenticate {
    private Retrofit client;
    private CP06Request cp06Request;

    public CP06Authenticate(Retrofit client, CP06Request cp06Request) {
        this.client = client;
        this.cp06Request = cp06Request;
    }

    public Call<List<CP06Response>> cp06Authenticate() {
        CP06AuthService cp06AuthService = client.create(CP06AuthService.class);
        Call<List<CP06Response>> call = cp06AuthService.cp06Auth(cp06Request);
        return call;
    }

}
