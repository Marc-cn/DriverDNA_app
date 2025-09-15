package org.o7planning.Ecorridor_DriverDNA.activity;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.o7planning.Ecorridor_DriverDNA.OBDMain;

import java.text.SimpleDateFormat;
import java.util.Date;


public class callAnalyticVehicle extends AsyncTask<Object, Void, String> {

    private static final String TAG = OBDMain.class.getName();
    String stringToken = "";

    @Override
    protected String doInBackground(Object... params) {

        stringToken = (String) params[0];
        String response= "";

        try {
            JSONObject jsonmetadata = new JSONObject();
            JSONObject metadata = new JSONObject();
            JSONObject searchCriteria = new JSONObject();
            JSONArray criteria= new JSONArray();
            JSONObject criteriaInnerValue = new JSONObject();
            JSONObject serviceParams = new JSONObject();
            JSONObject accessPurpose = new JSONObject();

            try {
                // ADDITIONAL ATTRIBUTE
                //jsonmetadata.put ("additionalAttribute", accessPurpose);
                //accessPurpose.put ("accessPurpose", "Cyber Threat Monitoring");

                // METADATA
                long mils = System.currentTimeMillis();
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                String timestring = "T";
                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss.SSSSSS");
                String sdtime = sdf1.format(new Date(mils)).toString() + timestring + sdf2.format(new Date(mils)).toString();
                //jsonmetadata.put ("metadata", metadata);
                //metadata.put("dsa_id", "DSA-fcb4f370-07f5-469b-b1f8-16d9d3f5ce12");
                //metadata.put("dsa_id", "DSA-1f6eb2c4-8e18-418f-a7f5-8849179cd119");
                //metadata.put("end_time", "2022-05-05T15:42:46Z");
                //metadata.put("event_type", "driver_dna");
                //metadata.put("file:extension", "json");
                //metadata.put("id", "1651754608668-2a832fa6-5e11-487a-a7a2-58b4e1a41236");
                //metadata.put("organization", "CNR");
                //metadata.put("start_time", "2022-05-05T14:42:46Z");
                //metadata.put("stixed", "false");

                // SEARCH CRITERIA
                criteriaInnerValue.put("attribute","driver_dna");
                criteriaInnerValue.put("operator","eq");
                criteriaInnerValue.put("value","20230418");
                criteria.put (criteriaInnerValue);
                searchCriteria.put ("combining_rule", "or");
                searchCriteria.put ("criteria", criteria);
                jsonmetadata.put ("searchCriteria", searchCriteria);

                // SERVICE NAME
                //jsonmetadata.put ("serviceName", "driverdna");

                // SERVICE PARAMS
                //serviceParams.put ("additionalProp1","string");
                //jsonmetadata.put ("serviceParams", "");

                // SUBJECT ID
                //jsonmetadata.put ("subjectID", "user");


            }  catch (JSONException e) {
                e.printStackTrace();
            }

            ///////////////////////////////////////////////
            // write file JSON to check
            Log.e(TAG, "JsonAnalytic: "+String.valueOf(jsonmetadata));
            System.out.println(jsonmetadata.toString(4));
            ////////////////////////////////////////////////////

            String jsonmetaString = jsonmetadata.toString();
            String charset = "UTF-8";
            //String requestURL = "https://ecorridor2.iit.cnr.it/iai-api/v1/runAnalytics";
            String requestURL = "https://ecorridor-s2c.iit.cnr.it/iai-api/v1/runAnalytics";
            callPOSTAnalyticVehicle connection = new callPOSTAnalyticVehicle(requestURL, stringToken, charset, jsonmetadata);

            response = connection.finish();
            Log.e(TAG, "TICKET:" + response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


    protected void onPostExecute(String response, String token)
    {
        callPollingVehicle task = new callPollingVehicle ();
        String callresult = String.valueOf(task.execute(response, stringToken));
    }
}