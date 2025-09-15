package org.o7planning.Ecorridor_DriverDNA.activity;

import android.util.Log;

import org.conscrypt.Conscrypt;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.security.Security;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class callPOSTAnalyticVehicle {
    private static final String TAG = callPOSTAnalyticVehicle.class.getName();
    private String charset;
    private  String responseString;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to application/json
     *
     * @param requestURL
     * @param token
     * @param charset
     * @param jsonmeta
     * @throws IOException
     */
    public callPOSTAnalyticVehicle(String requestURL, String token, String charset, JSONObject jsonmeta)
            throws IOException {

        this.charset = charset;
        URL url = new URL(requestURL);

        // HTTP CONNECTION and POST CALL

        //////////////////////////////

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

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, jsonmeta.toString());


        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            //responseString = response.networkResponse().toString();
            //responseString = response.message().toString();
            responseString = response.body().string();
            Log.e(TAG, responseString);
            Log.e(TAG, "HERE1");
        } catch (IOException e) {
            Log.e(TAG, "HERE2");
            e.printStackTrace();
        }

    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {

        return (responseString);
        }

}