package org.o7planning.Ecorridor_DriverDNA.activity;

import android.os.AsyncTask;
import android.util.Log;

import org.conscrypt.Conscrypt;
import org.json.JSONException;
import org.json.JSONObject;
import org.o7planning.Ecorridor_DriverDNA.DNA.Service1Vehicle;

import java.io.IOException;
import java.security.Security;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RetrieveTokenVehicle extends AsyncTask<String, Void, String> {

    private static final String TAG = Service1Vehicle.class.getName();

    protected String doInBackground(String... urls) {

        String responseString;
        String token = null;

        ///////////////////////////////

        // Use Conscrypt as the main provider
        Security.insertProviderAt(Conscrypt.newProvider(), 1);

        OkHttpClient client = new OkHttpClient();

        //// SET TIMEOUT TO NOT STOP CONNECTION
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(1500, TimeUnit.SECONDS);
        builder.readTimeout(1500, TimeUnit.SECONDS);
        builder.writeTimeout(1500, TimeUnit.SECONDS);
        client = builder.build();
        /////////

        RequestBody requestBody = new FormBody.Builder()
                .add("client_id", "api-services")
                .add("client_secret", "cb93d797-f378-4bb4-a31d-32860921f7c6")
                .add("username", "marco.devincenzi")
                //.add("username", "giuseppe.crincoli")
                .add("password", "eeV6eiV5")
                //.add("password", "chu9Isoh")
                .add("grant_type", "password")
                .add("scope", "openid profile ecorridor email")
                .build();

        String url = "https://ecorridor-s2c.iit.cnr.it/auth/realms/ecorridor/protocol/openid-connect/token";
        //String url = "http://146.48.62.101/auth/realms/ecorridor/protocol/openid-connect/token";
        //String url = "http://fbetbdaeso.iit.cnr.it/auth/realms/ecorridor/protocol/openid-connect/token";
        //String url = "https://ecorridor2.iit.cnr.it/auth/realms/ecorridor/protocol/openid-connect/token";

        Request request = new Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            responseString = response.body().string();

            // convert response to json
            JSONObject jsonResponse = null;
            try {
                jsonResponse = new JSONObject(responseString.toString());
                token = jsonResponse.getString("access_token");
                Log.e(TAG, "TOKEN:" + token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            token = "TOKEN NULL OR NOT RECEIVED";
            return token;
        }

        return token;
    }

}

