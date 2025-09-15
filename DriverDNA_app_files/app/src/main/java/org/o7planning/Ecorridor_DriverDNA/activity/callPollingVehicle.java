package org.o7planning.Ecorridor_DriverDNA.activity;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.o7planning.Ecorridor_DriverDNA.OBDMain;
import org.o7planning.Ecorridor_DriverDNA.config.resultAnalytic;


public class callPollingVehicle extends AsyncTask<String, Void, String> {

    private static final String TAG = OBDMain.class.getName();

    @Override
    protected String doInBackground(String...params) {
        String result = "" ;
        String ticket = (String) params[0];
        String token= (String) params[1];
        JSONObject ticketjson = null;
        JSONObject resultjson = null;
        String resultString = "";
        String response= "";

        Log.e(TAG, "TicketPolling Ticket:::"+ ticket);
        Log.e(TAG, "TicketPolling Token:::"+ token);

        try {

            String charset = "UTF-8";
            String requestURL = "https://ecorridor.iit.cnr.it/iai-api/v1/getResponse/{" + ticket + "}";
            //String requestURL = "https://ecorridor.iit.cnr.it/iai-api/v1/getResponse/30dfcdc9-2554-49b1-a05b-68a182ab63ed/";
            CallGETPollingVehicle connection = new CallGETPollingVehicle (requestURL, token, charset);

            response = connection.finish();
            Log.e(TAG, "RESULT ANALYTIC:" + response);

            resultAnalytic.resultGlobal = response;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

}