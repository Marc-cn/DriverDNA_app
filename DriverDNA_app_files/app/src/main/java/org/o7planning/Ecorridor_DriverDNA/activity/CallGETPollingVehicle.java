package org.o7planning.Ecorridor_DriverDNA.activity;

import org.conscrypt.Conscrypt;

import java.io.IOException;
import java.net.URL;
import java.security.Security;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CallGETPollingVehicle {
    private static final String TAG = CallGETPollingVehicle.class.getName();
    private String charset;
    private String responseString;

    /**
     * @param requestURL
     * @param token
     * @param charset
     * @throws IOException
     */
    public CallGETPollingVehicle(String requestURL, String token, String charset)
            throws IOException {

        this.charset = charset;

        // HTTP CONNECTION and GET CALL

        URL url = new URL(requestURL);


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

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + token)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            responseString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String finish() throws IOException {

        return (responseString);
    }

}