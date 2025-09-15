package org.o7planning.Ecorridor_DriverDNA.activity;

import android.os.AsyncTask;
import android.util.Log;

import org.conscrypt.Conscrypt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.o7planning.Ecorridor_DriverDNA.config.idsNumber;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


public class callCreateDPOVehicleIDS extends AsyncTask<Object, Void, String> {

    private static final String TAG = callCreateDPOVehicleIDS.class.getName();

    @Override
    protected String doInBackground(Object... params) {

        File fileJsonPath = (File) params[0];
        String stringToken = (String) params[1];

        String boundary;
        String responseString = null;

        Log.e(TAG, "STRING TOKEN:"+ stringToken);

        /////////////////////////////////////////////////////
        Random ran = new Random();
        int ids_number = ran.nextInt(6000000) + 50;
        idsNumber.idsNumber = ids_number;
        String ids_string_value = Integer.toString(ids_number);
        //////////////////////////////////////////////////////

        try {
            JSONObject jsonmetadata = new JSONObject();
            String subjectid = "driver";
            JSONObject request_json = new JSONObject();
            JSONArray attibuteIDs= new JSONArray();

            //////////////////////////
            // optimization test
            /*
            ArrayList<JSONObject> arrayListOfAttribute = new ArrayList<>();
            int NumberOfAttributes = 16;

            for (int i = 0; i < NumberOfAttributes; i++) {
                JSONObject attibuteID = new JSONObject();
                arrayListOfAttribute.add(attibuteID);
            }

            try {

                // METADATA
                long mils = System.currentTimeMillis();
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                String timestring = "T";
                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss.SSSSSS");
                String sdtime = sdf1.format(new Date(mils)).toString() + timestring + sdf2.format(new Date(mils)).toString();

                // ATTRIBUTE ID 1
                arrayListOfAttribute.get(1).put("AttributeId","urn:oasis:names:tc:xacml:1.0:resource:data-start-date");
                arrayListOfAttribute.get(1).put("Value","2022-12-13");
                arrayListOfAttribute.get(1).put("DataType","string");

                // ATTRIBUTE ID 2
                arrayListOfAttribute.get(2).put("AttributeId","urn:oasis:names:tc:xacml:1.0:resource:data-start-time");
                arrayListOfAttribute.get(2).put("Value","22:57:48");
                arrayListOfAttribute.get(2).put("DataType","string");

                // ATTRIBUTE ID 3
                arrayListOfAttribute.get(3).put("AttributeId","urn:oasis:names:tc:xacml:1.0:resource:data-end-date");
                arrayListOfAttribute.get(3).put("Value","2022-12-13");
                arrayListOfAttribute.get(3).put("DataType","string");

                // ATTRIBUTE ID 4
                arrayListOfAttribute.get(4).put("AttributeId","urn:oasis:names:tc:xacml:1.0:resource:data-end-time");
                arrayListOfAttribute.get(4).put("Value","23:57:48");
                arrayListOfAttribute.get(4).put("DataType","string");

                // ATTRIBUTE ID 5
                arrayListOfAttribute.get(5).put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:dsa-id");
                arrayListOfAttribute.get(5).put("Value","DSA-1f6eb2c4-8e18-418f-a7f5-8849179cd119");
                arrayListOfAttribute.get(5).put("DataType","string");

                // ATTRIBUTE ID 6
                arrayListOfAttribute.get(6).put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:resource-type");
                arrayListOfAttribute.get(6).put("Value","ewadwe");
                arrayListOfAttribute.get(6).put("DataType","string");

                // ATTRIBUTE ID 7
                arrayListOfAttribute.get(7).put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:producer");
                arrayListOfAttribute.get(7).put("Value","rewrwe");
                arrayListOfAttribute.get(7).put("DataType","string");

                // ATTRIBUTE ID 8
                arrayListOfAttribute.get(8).put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:producer-appliance");
                arrayListOfAttribute.get(8).put("Value","erwerwe");
                arrayListOfAttribute.get(8).put("DataType","string");

                // ATTRIBUTE ID 9
                arrayListOfAttribute.get(9).put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:producer-appliance-owner");
                arrayListOfAttribute.get(9).put("Value","erewrw");
                arrayListOfAttribute.get(9).put("DataType","string");

                // ATTRIBUTE ID 10
                arrayListOfAttribute.get(10).put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:production-position");
                arrayListOfAttribute.get(10).put("Value","erwerwe");
                arrayListOfAttribute.get(10).put("DataType","string");

                // ATTRIBUTE ID 11
                arrayListOfAttribute.get(11).put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:resource-owner");
                arrayListOfAttribute.get(11).put("Value","CNR");
                arrayListOfAttribute.get(11).put("DataType","string");

                // ATTRIBUTE ID 12
                arrayListOfAttribute.get(12).put("AttributeId","file:extension");
                arrayListOfAttribute.get(12).put("Value","json");
                arrayListOfAttribute.get(12).put("DataType","string");

                // ATTRIBUTE ID 13
                arrayListOfAttribute.get(13).put("AttributeId","urn:oasis:names:tc:xacml:1.0:subject:subject-id");
                arrayListOfAttribute.get(13).put("Value","marco.devincenzi");
                arrayListOfAttribute.get(13).put("DataType","string");

                // ATTRIBUTE ID 14
                arrayListOfAttribute.get(14).put("AttributeId","urn:oasis:names:tc:xacml:3.0:subject:subject-organisation");
                arrayListOfAttribute.get(14).put("Value","CNR");
                arrayListOfAttribute.get(14).put("DataType","string");

                // ATTRIBUTE ID 15
                arrayListOfAttribute.get(15).put("AttributeId","urn:oasis:names:tc:xacml:1.0:action:action-id");
                arrayListOfAttribute.get(15).put("Value","create");
                arrayListOfAttribute.get(15).put("DataType","string");

                // ATTRIBUTE ID 16
                arrayListOfAttribute.get(16).put("AttributeId","urn:oasis:names:tc:xacml:3.0:subject:access-purpose");
                arrayListOfAttribute.get(16).put("Value","Cyber Threat Monitoring");
                arrayListOfAttribute.get(16).put("DataType","string");

                for (int i = 0; i < NumberOfAttributes; i++) {
                    attibuteIDs.put (arrayListOfAttribute.get(i));
                }

                request_json.put ("Attribute", attibuteIDs);
                jsonmetadata.put ("Request", request_json);
            */

            JSONObject attibuteID1 = new JSONObject();
            JSONObject attibuteID2 = new JSONObject();
            JSONObject attibuteID3 = new JSONObject();
            JSONObject attibuteID4 = new JSONObject();
            JSONObject attibuteID5 = new JSONObject();
            JSONObject attibuteID6 = new JSONObject();
            JSONObject attibuteID7 = new JSONObject();
            JSONObject attibuteID8 = new JSONObject();
            JSONObject attibuteID9 = new JSONObject();
            JSONObject attibuteID10 = new JSONObject();
            JSONObject attibuteID11 = new JSONObject();
            JSONObject attibuteID12 = new JSONObject();
            JSONObject attibuteID13 = new JSONObject();
            JSONObject attibuteID14 = new JSONObject();
            JSONObject attibuteID15 = new JSONObject();
            JSONObject attibuteID16 = new JSONObject();
            JSONObject attibuteID17 = new JSONObject();
            JSONObject attibuteID18 = new JSONObject();

            try {

                // METADATA
                long mils = System.currentTimeMillis();
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                String timestring = "T";
                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss.SSSSSS");
                String sdtime = sdf1.format(new Date(mils)).toString() + timestring + sdf2.format(new Date(mils)).toString();

                // ATTRIBUTE ID 1
                attibuteID1.put("AttributeId","urn:oasis:names:tc:xacml:1.0:resource:data-start-date");
                attibuteID1.put("Value","2022-12-13");
                attibuteID1.put("DataType","string");

                // ATTRIBUTE ID 2
                attibuteID2.put("AttributeId","urn:oasis:names:tc:xacml:1.0:resource:data-start-time");
                attibuteID2.put("Value","22:57:48");
                attibuteID2.put("DataType","string");

                // ATTRIBUTE ID 3
                attibuteID3.put("AttributeId","urn:oasis:names:tc:xacml:1.0:resource:data-end-date");
                attibuteID3.put("Value","2022-12-13");
                attibuteID3.put("DataType","string");

                // ATTRIBUTE ID 4
                attibuteID4.put("AttributeId","urn:oasis:names:tc:xacml:1.0:resource:data-end-time");
                attibuteID4.put("Value","23:57:48");
                attibuteID4.put("DataType","string");

                // ATTRIBUTE ID 5
                attibuteID5.put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:dsa-id");
                attibuteID5.put("Value","DSA-68866e4b-741e-4a1d-a293-09f90604a7f8");
                attibuteID5.put("DataType","string");

                // ATTRIBUTE ID 6
                attibuteID6.put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:resource-type");
                attibuteID6.put("Value",ids_string_value);
                attibuteID6.put("DataType","string");

                // ATTRIBUTE ID 7
                attibuteID7.put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:producer");
                attibuteID7.put("Value","CNR");
                attibuteID7.put("DataType","string");

                // ATTRIBUTE ID 8
                attibuteID8.put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:producer-appliance");
                attibuteID8.put("Value","erwerweqw");
                attibuteID8.put("DataType","string");

                // ATTRIBUTE ID 9
                attibuteID9.put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:producer-appliance-owner");
                attibuteID9.put("Value","erew");
                attibuteID9.put("DataType","string");

                // ATTRIBUTE ID 10
                attibuteID10.put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:production-position");
                attibuteID10.put("Value","CNR");
                attibuteID10.put("DataType","string");

                // ATTRIBUTE ID 11
                attibuteID11.put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:resource-owner");
                attibuteID11.put("Value","CNR");
                attibuteID11.put("DataType","string");

                // ATTRIBUTE ID 12
                attibuteID12.put("AttributeId","file:extension");
                attibuteID12.put("Value","json");
                attibuteID12.put("DataType","string");

                // ATTRIBUTE ID 13
                attibuteID13.put("AttributeId","urn:oasis:names:tc:xacml:1.0:subject:subject-id");
                attibuteID13.put("Value","marco.devincenzi");
                //attibuteID13.put("Value","giuseppe.crincoli");
                attibuteID13.put("DataType","string");

                // ATTRIBUTE ID 14
                attibuteID14.put("AttributeId","urn:oasis:names:tc:xacml:3.0:subject:subject-organisation");
                attibuteID14.put("Value","CNR");
                attibuteID14.put("DataType","string");

                // ATTRIBUTE ID 15
                attibuteID15.put("AttributeId","urn:oasis:names:tc:xacml:1.0:action:action-id");
                attibuteID15.put("Value","create");
                attibuteID15.put("DataType","string");

                // ATTRIBUTE ID 16
                attibuteID16.put("AttributeId","urn:oasis:names:tc:xacml:3.0:subject:access-purpose");
                attibuteID16.put("Value","Cyber Threat Monitoring");
                attibuteID16.put("DataType","string");

                // ATTRIBUTE ID 17
                attibuteID17.put("AttributeId","urn:oasis:names:tc:xacml:1.0:resource:resource-id");
                attibuteID17.put("Value","json");
                attibuteID17.put("DataType","string");

                // ATTRIBUTE ID 18
                attibuteID18.put("AttributeId","urn:oasis:names:tc:xacml:3.0:resource:producer-appliance-id");
                attibuteID18.put("Value","appliance-id");
                attibuteID18.put("DataType","string");


                attibuteIDs.put (attibuteID1);
                attibuteIDs.put (attibuteID2);
                attibuteIDs.put (attibuteID3);
                attibuteIDs.put (attibuteID4);
                attibuteIDs.put (attibuteID5);
                attibuteIDs.put (attibuteID6);
                attibuteIDs.put (attibuteID7);
                attibuteIDs.put (attibuteID8);
                attibuteIDs.put (attibuteID9);
                attibuteIDs.put (attibuteID10);
                attibuteIDs.put (attibuteID11);
                attibuteIDs.put (attibuteID12);
                attibuteIDs.put (attibuteID13);
                attibuteIDs.put (attibuteID14);
                attibuteIDs.put (attibuteID15);
                attibuteIDs.put (attibuteID16);
                attibuteIDs.put (attibuteID17);
                attibuteIDs.put (attibuteID18);

                request_json.put ("Attribute", attibuteIDs);
                jsonmetadata.put ("Request", request_json);


            }  catch (JSONException e) {
                        e.printStackTrace();
            }

        ///////////////////////////////////////////////
        // write file JSON to check
        Log.e(TAG, "JsonAnalytic: "+String.valueOf(jsonmetadata));
        try {
            System.out.println(jsonmetadata.toString(4));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        ////////////////////////////////////////////////////

            String jsonmetaString = jsonmetadata.toString();
            boundary = "===" + System.currentTimeMillis() + "===";

            //String requestURL = "https://ecorridor2.iit.cnr.it/isi-api/v1/dpo";
            //String requestURL = "https://ecorridor2.iit.cnr.it/pilot-interface/v1/uploadFile";
            String requestURL = "https://ecorridor-s2c.iit.cnr.it/isi-api/v1/dpo";

            //////////////////////////////

            // Use Conscrypt as the main provider
            Security.insertProviderAt(Conscrypt.newProvider(), 1);

            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();

            //// SET TIMEOUT TO NOT STOP CONNECTION
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(1500, TimeUnit.SECONDS);
            builder.readTimeout(1500, TimeUnit.SECONDS);
            builder.writeTimeout(1500, TimeUnit.SECONDS);
            client = builder.build();
            /////////

            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("input_metadata", jsonmetaString)
                    .addFormDataPart("fileToSubmit", fileJsonPath.getName(), RequestBody.create(MediaType.parse("text/plain"), fileJsonPath))
                    .build();


            Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + stringToken)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .url(requestURL)
                    .post(body)
                    .build();

            try {
                Buffer bodyBuffer = new Buffer();
                request.body().writeTo( bodyBuffer );
                Log.d( TAG, "Request body: '" + bodyBuffer.readUtf8() + "': " );
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (Response response = client.newCall(request).execute()) {
                responseString = response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Response callDPO "+ responseString);

        return responseString;
    }
}