package org.o7planning.Ecorridor_DriverDNA;

import static org.o7planning.Ecorridor_DriverDNA.activity.HelperImageClass.SetImageToLoad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.o7planning.Ecorridor_DriverDNA.activity.CallGETPollingVehicle;
import org.o7planning.Ecorridor_DriverDNA.activity.HelperImageClass;
import org.o7planning.Ecorridor_DriverDNA.activity.RetrieveTokenVehicle;
import org.o7planning.Ecorridor_DriverDNA.activity.callCreateDPOVehicleIDS;
import org.o7planning.Ecorridor_DriverDNA.activity.callPOSTAnalyticVehicle;
import org.o7planning.Ecorridor_DriverDNA.config.idsNumber;
import org.o7planning.Ecorridor_DriverDNA.config.resultAnalyticIDS;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IDS extends AppCompatActivity {

    private static final String TAG = IDS.class.getName();

    private Button buttonmenuaskids;
    private Button buttonmenuaskids2;
    private ImageButton imageButtonBackarrow;
    public String ticket;
    /////////////////////////////////////////////////////////
    HelperImageClass HelperImageClass;
    /////////
    private static final int PERMISSION_REQUEST_CODE = 100;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_ids);

        this.imageButtonBackarrow = (ImageButton) this.findViewById(R.id.backarrowmenu);
        this.buttonmenuaskids = (Button) this.findViewById(R.id.buttonmenuaskids);
        this.buttonmenuaskids2 = (Button) this.findViewById(R.id.buttonmenuaskids2);

        // When user click "Backarrow" button.
        this.imageButtonBackarrow.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(IDS.this, Home.class);

                // Start.
                IDS.this.startActivity(myIntent);
            }
        });

        // When user click "senddata" button.
        this.buttonmenuaskids.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendData();
            }
        });

        // When user click "senddata" button.
        this.buttonmenuaskids2.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                callAnalyticandResultVehicle();
            }
        });

    }

    @SuppressLint("LongLogTag")
    private void sendData() {
        Log.d(TAG, "Stopping live data..");

        String token = null;

        Toast.makeText(this, "Live data uploaded", Toast.LENGTH_LONG).show();


        //////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        //TO ACCESS EXTERNAL STORAGE FOR TEST

        String state = Environment.getExternalStorageState();
        mExternalStorageAvailable = mExternalStorageWriteable = true;
        //Toast.makeText(getApplicationContext(), "We Can Read And Write ", Toast.LENGTH_LONG).show();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    try {

                    } catch (RuntimeException e) {
                        Log.e(TAG, "Can't enable logging to file.", e);
                    }

                } else {
                    requestPermission(); // Code for permission
                }
            }
        }


        try {
            token = new RetrieveTokenVehicle().execute("http://146.48.62.101/auth/realms/ecorridor/protocol/openid-connect/token").get();
            //token = new RetrieveTokenVehicle().execute("https://ecorridor2.iit.cnr.it/auth/realms/ecorridor/protocol/openid-connect/token").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.e("TOKEN:", token);

        // added for test
        //File fileFileWriter2 = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "pippoTest.json"));
        File fileFileWriter2 = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator  + "test_analyzer_27032023.txt"));
        //File fileFileWriter2 = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator  + "cnr.h5"));

        ////////////////////////////////////// CALL DPO VEHICLE !!!!!!!!
        callCreateDPOVehicleIDS task = new callCreateDPOVehicleIDS();
        String callresult = String.valueOf(task.execute(fileFileWriter2, token));

        Log.e(TAG, "CALLDPO EXECUTED");
        Log.e(TAG, "Button callDPO" + callresult);

        //Toast.makeText(getApplicationContext(),  callresult, Toast.LENGTH_LONG).show();

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(IDS.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(IDS.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(IDS.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(IDS.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void callAnalyticandResultVehicle() {

        Toast.makeText(this, "IDS Started", Toast.LENGTH_LONG).show();

        Log.d(TAG, "callAnalyticandResultVehicle..");

        String token = null;

        // RETRIEVE TOKEN AND START ANALYTIC
        try {
            //token = new RetrieveTokenVehicle().execute("https://ecorridor2.iit.cnr.it/auth/realms/ecorridor/protocol/openid-connect/token").get();
            token = new RetrieveTokenVehicle().execute("http://146.48.62.101/auth/realms/ecorridor/protocol/openid-connect/token").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Log.e("TOKEN:", token);
        //////////////////////////////////////////
        IDS.callAnalyticVehicleService1 task = new IDS.callAnalyticVehicleService1();
        String callresult = String.valueOf(task.execute(token));
        Log.e(TAG, "CALL ANALYTIC");
    }

    public class callAnalyticVehicleService1 extends AsyncTask<Object, Void, String> {

        String stringToken = "";

        @Override
        protected String doInBackground(Object... params) {

            stringToken = (String) params[0];
            String response = "";

            int ids_number = idsNumber.idsNumber;
            String ids_string_value = Integer.toString(ids_number);

            try {
                JSONObject jsonmetadata = new JSONObject();
                JSONObject metadata = new JSONObject();
                JSONObject searchCriteria = new JSONObject();
                JSONArray criteria = new JSONArray();
                JSONObject criteriaInnerValue = new JSONObject();
                JSONObject criteriaInnerValue2 = new JSONObject();
                JSONObject serviceParams = new JSONObject();
                JSONObject accessPurpose = new JSONObject();

                try {
                    // ADDITIONAL ATTRIBUTE
                    jsonmetadata.put("additionalAttribute", accessPurpose);
                    accessPurpose.put("accessPurpose", "Cyber Threat Monitoring");

                    // METADATA
                    long mils = System.currentTimeMillis();
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    String timestring = "T";
                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss.SSSSSS");
                    String sdtime = sdf1.format(new Date(mils)).toString() + timestring + sdf2.format(new Date(mils)).toString();
                    jsonmetadata.put("metadata", metadata);

                    // SEARCH CRITERIA
                    searchCriteria.put("combining_rule", "or");
                    criteriaInnerValue.put("attribute", "event_type");
                    criteriaInnerValue.put("operator", "eq");
                    criteriaInnerValue.put("value", "IDS_fraun_model");
                    criteriaInnerValue2.put("attribute", "event_type");
                    criteriaInnerValue2.put("operator", "eq");
                    criteriaInnerValue2.put("value", ids_string_value);
                    criteria.put(criteriaInnerValue);
                    criteria.put(criteriaInnerValue2);

                    searchCriteria.put("criteria", criteria);
                    jsonmetadata.put("searchCriteria", searchCriteria);

                    // SERVICE NAME
                    jsonmetadata.put("serviceName", "automotiveids");

                    // SERVICE PARAMS
                    serviceParams.put ("ids_task","classify");
                    serviceParams.put ("ids_model", "cnr.h5");
                    serviceParams.put ("adapter", "CANHackerAdapter");
                    jsonmetadata.put ("serviceParams", serviceParams);

                    // SUBJECT ID
                    jsonmetadata.put("subjectID", "user");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ///////////////////////////////////////////////
                // write file JSON to check
                Log.e(TAG, "JsonAnalytic: " + String.valueOf(jsonmetadata));
                System.out.println(jsonmetadata.toString(4));
                ////////////////////////////////////////////////////

                String jsonmetaString = jsonmetadata.toString();
                String charset = "UTF-8";
                //String requestURL = "https://ecorridor2.iit.cnr.it/iai-api/v1/runAnalytics";
                String requestURL = "http://146.48.62.101/iai-api/v1/runAnalytics";
                callPOSTAnalyticVehicle connection = new callPOSTAnalyticVehicle(requestURL, stringToken, charset, jsonmetadata);

                response = connection.finish();
                Log.e(TAG, "TICKET:" + response);

            } catch (Exception e) {
                e.printStackTrace();
            }

            /////////////////////////////////
            // convert response from string to JSON

            String ticketString = "";

            JSONObject responseJSON = new JSONObject();
            try {
                responseJSON = new JSONObject(response);
                //System.out.println(responseJSON.toString(4));
                ticketString = (responseJSON.getString("value"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return ticketString;
        }

        @Override
        protected void onPostExecute(String ticketString) {
            super.onPostExecute(ticketString);
            IDS.callPollingVehicle taskPolling = new IDS.callPollingVehicle();
            String callresult = String.valueOf(taskPolling.execute(ticketString, stringToken));
        }
    }

    public class callPollingVehicle extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String result = "";
            ticket = (String) params[0];
            String token = (String) params[1];
            JSONObject ticketjson = null;
            JSONObject resultjson = null;
            String resultString = "";
            String response = "";

            Log.e(TAG, "TicketPolling Ticket:::" + ticket);
            Log.e(TAG, "TicketPolling Token:::" + token);

            try {
                String charset = "UTF-8";
                //String requestURL = "https://ecorridor.iit.cnr.it/iai-api/v1/getResponse/{" + ticket + "}";
                //String requestURL = "https://ecorridor.iit.cnr.it/iai-api/v1/getResponse/30dfcdc9-2554-49b1-a05b-68a182ab63ed/";
                //String requestURL = "https://ecorridor2.iit.cnr.it/iai-api/v1/getResponse/" + ticket + "/";
                String requestURL = "http://146.48.62.101/iai-api/v1/getResponse/" + ticket + "/";
                //if (requestURL != "https://ecorridor2.iit.cnr.it/iai-api/v1/getResponse//") {
                if (requestURL != "http://146.48.62.101.iit.cnr.it/iai-api/v1/getResponse//") {
                    Log.e(TAG, "STRING REQUEST URL " + requestURL);
                    CallGETPollingVehicle connection = new CallGETPollingVehicle(requestURL, token, charset);

                    response = connection.finish();
                    Log.e(TAG, "RESULT ANALYTIC:" + response);

                    resultAnalyticIDS.resultGlobalIDS = response;
                } else {
                    Toast.makeText(IDS.this, "Error: Ticket not received", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Ticket not received");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response)
        {
            super.onPostExecute(response);

            /////////////////////////
            // EXTRACT DIGITS FROM THE RESULT

            String s = resultAnalyticIDS.resultGlobalIDS;
            //String s = "{\"result_files\":[\"1680013732080-aa0e6269-fb33-46e4-9a80-0af8edba2aa6.dpo_stix_0.json\"],\"result_message\":\"0\",\"finished\":true,\"status\":\"FINISH_OK\"}";
            String resultReceived = null;
            double resultReceivedDouble = 0;
            float flt = 0;
            String finishedValue = "testError"; //to test if the analytic is finished

            Pattern patternTest = Pattern.compile(".*\"finished\":(true),.*");
            Matcher matcherTest = patternTest.matcher(s);
            if (matcherTest.matches()) {
                finishedValue = matcherTest.group(1);
                finishedValue ="1";
            } else {
                finishedValue = "0";
            }

            if (finishedValue == "1") {
                //Creating a pattern to identify floats
                Pattern pat = Pattern.compile("(?<=\"result_message\":\")\\d+(?=\")");
                //matching the string with the pattern
                Matcher m = pat.matcher(s);
                //extracting and storing the float values
                while (m.find()) {
                    resultReceived = m.group();
                    System.out.println(m.group());
                    System.out.println(m.group().getClass());
                    //double d=Double.parseDouble(m.group());
                }

                // convert string regex to number changing coma with point
                NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                Number number = null;
                try {
                    number = format.parse(resultReceived);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                resultReceivedDouble = number.doubleValue();

                //printing the float values
                System.out.println("The received IDS value is:");
                System.out.println(resultReceivedDouble);
            }
            else {
                System.out.println("Error in the received response");
                Toast.makeText(IDS.this, "Error: Ticket not received. Please try again", Toast.LENGTH_LONG).show();
            }
            //////////////////////////////////
            // CHANGE COLOR OF STOPLIGHT AT RUNTIME USING HELPERIMAGECLASS AS SUPPORT
            //resultReceivedDouble = 70.0;
            SetImageToLoad(resultReceivedDouble);
            ImageView stopLight = (ImageView)findViewById(R.id.greyB);
            stopLight.setImageResource(HelperImageClass.GetResourceIDOfImageToLoad());
        }

    }
}