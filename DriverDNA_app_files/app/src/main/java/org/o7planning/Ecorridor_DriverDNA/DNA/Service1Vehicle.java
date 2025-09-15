package org.o7planning.Ecorridor_DriverDNA.DNA;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.o7planning.Ecorridor_DriverDNA.R;
import org.o7planning.Ecorridor_DriverDNA.activity.CallGETPollingVehicle;
import org.o7planning.Ecorridor_DriverDNA.activity.RetrieveTokenVehicle;
import org.o7planning.Ecorridor_DriverDNA.activity.callPOSTAnalyticVehicle;
import org.o7planning.Ecorridor_DriverDNA.config.resultAnalytic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Service1Vehicle extends AppCompatActivity {

    private static final String TAG = Service1Vehicle.class.getName();

    private ImageButton imageButtonBackarrowaskservice;
    private String token;
    public ProgressBar spinner;
    public TextView result;
    private int quartile = 100;
    public String ticket;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    private static final int PERMISSION_REQUEST_CODE = 100;

    //////////////////////////////////////
    // RADAR
    //define max and min for radar chart
    public static final float MAX = 5, MIN = 1;
    //nb qualities radar chart
    public static final int NB_QUALITIES = 4;
    public RadarChart chart;
    /////////////////////////////////// END RADAR


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service1);
        spinner=(CircularProgressIndicator)findViewById(R.id.progressCircle);
        result =(TextView) findViewById(R.id.textViewService1);

        //////////////////////////////////////
        ////////////////////////////////////
        // RADAR
        chart = (RadarChart) this.findViewById(R.id.chart);

        // we configure the radar chart
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.getDescription().setEnabled(false);

        // hide legend and initial message
        chart.setNoDataText("");
        chart.getLegend().setEnabled(true);

        //useful to export graph
        chart.setWebLineWidth(1f);
        chart.setWebColor(Color.BLACK);
        chart.setWebColorInner(Color.BLACK);
        chart.setWebAlpha(100);


        // animate chart
        chart.animateXY(1400, 1400, Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad);
        //define axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(15f);
        xAxis.setYOffset(0);
        xAxis.setXOffset(0);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] qualities = new String[]{"Speeding", "Breaking", "Turning", "Rpm"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return qualities[(int) value % qualities.length];
            }
        });

        xAxis.setTextColor(Color.BLACK);

        //Y axis
        YAxis yAxis = chart.getYAxis();
        yAxis.setLabelCount(NB_QUALITIES, true);
        yAxis.setTextSize(15f);
        yAxis.setAxisMinimum(MIN);
        yAxis.setAxisMaximum(MAX);
        yAxis.setDrawLabels(true);


        Legend l = chart.getLegend();
        l.setTextSize(15f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.BLACK);
        ////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////
        // FINISH RADAR
        ////////////////////////////// END RADAR


        // Find Button by its ID
        this.imageButtonBackarrowaskservice = (ImageButton) this.findViewById(R.id.backarrowservice1);

        // When user click "Backarrow" button.
        this.imageButtonBackarrowaskservice.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Back to previous Activity.

                if (quartile == 100) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Service1Vehicle.this);

                    builder.setTitle("Confirm");
                    builder.setMessage("Still computing results, are you sure you want ot exit?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            Service1Vehicle.this.finish();
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                }
                else{
                    Service1Vehicle.this.finish();
                }
            }
        });

        /////////////////////////////
        // CALL ANALYTIC

        callAnalyticandResultVehicle();
    }


    private void callAnalyticandResultVehicle() {

        // RETRIEVE TOKEN AND START ANALYTIC
        try {
            //token = new RetrieveTokenVehicle().execute("https://ecorridor2.iit.cnr.it/auth/realms/ecorridor/protocol/openid-connect/token").get();
            token = new RetrieveTokenVehicle().execute("https://ecorridor-s2c.iit.cnr.it/auth/realms/ecorridor/protocol/openid-connect/token").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Log.e("TOKEN:", token);
        //////////////////////////////////////////
        callAnalyticVehicleService1 task = new callAnalyticVehicleService1();
        String callresult = String.valueOf(task.execute(token));
        Log.e(TAG, "CALL ANALYTIC");
    }

    public class callAnalyticVehicleService1 extends AsyncTask<Object, Void, String> {

        String stringToken = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Do something like display a progress bar
            spinner.setVisibility(View.VISIBLE);

        }

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
                    jsonmetadata.put ("additionalAttribute", accessPurpose);
                    accessPurpose.put ("accessPurpose", "Cyber Threat Monitoring");

                    // METADATA
                    long mils = System.currentTimeMillis();
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    String timestring = "T";
                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss.SSSSSS");
                    String sdtime = sdf1.format(new Date(mils)).toString() + timestring + sdf2.format(new Date(mils)).toString();
                    jsonmetadata.put ("metadata", metadata);
                    //metadata.put("dsa_id", "DSA-fcb4f370-07f5-469b-b1f8-16d9d3f5ce12");
                    //metadata.put("dsaId", "DSA-1f6eb2c4-8e18-418f-a7f5-8849179cd119");
                    //metadata.put("end_time", "2022-05-05T15:42:46Z");
                    //metadata.put("event_type", "driver_dna");
                    //metadata.put("file:extension", "json");
                    //metadata.put("id", "1651754608668-2a832fa6-5e11-487a-a7a2-58b4e1a41236");
                    //metadata.put("organization", "CNR");
                    //metadata.put("start_time", "2022-05-05T14:42:46Z");
                    //metadata.put("stixed", "false");

                    // SEARCH CRITERIA
                    criteriaInnerValue.put("attribute","event_type");
                    criteriaInnerValue.put("operator","eq");
                    criteriaInnerValue.put("value","20230418");
                    criteria.put (criteriaInnerValue);
                    searchCriteria.put ("combining_rule", "or");
                    searchCriteria.put ("criteria", criteria);
                    jsonmetadata.put ("searchCriteria", searchCriteria);

                    // SERVICE NAME
                    jsonmetadata.put ("serviceName", "driverdna");

                    // SERVICE PARAMS
                    //serviceParams.put ("additionalProp1","string");
                    //jsonmetadata.put ("serviceParams", "");

                    // SUBJECT ID
                    jsonmetadata.put ("subjectID", "user");


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
                String requestURL = "https://ecorridor-s2c.iit.cnr.it/iai-api/v1/runAnalytics";
                //String requestURL = "https://ecorridor2.iit.cnr.it/iai-api/v1/runAnalytics";
                callPOSTAnalyticVehicle connection = new callPOSTAnalyticVehicle(requestURL, stringToken, charset, jsonmetadata);

                response = connection.finish();
                Log.e(TAG, "TICKET:" + response);

            } catch (Exception e) {
                e.printStackTrace();
            }

            /////////////////////////////////
            // convert response from string to JSON

            String ticketString= "";

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
        protected void onPostExecute(String ticketString)
        {
            super.onPostExecute(ticketString);
            callPollingVehicle taskPolling = new callPollingVehicle();
            String callresult = String.valueOf(taskPolling.execute(ticketString, stringToken));
        }
    }

    public class callPollingVehicle extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String...params) {
            String result = "" ;
            ticket = (String) params[0];
            String token= (String) params[1];
            JSONObject ticketjson = null;
            JSONObject resultjson = null;
            String resultString = "";
            String response= "";

            Log.e(TAG, "TicketPolling Ticket:::"+ ticket);
            Log.e(TAG, "TicketPolling Token:::"+ token);

            try {
                    String charset = "UTF-8";
                    //String requestURL = "https://ecorridor.iit.cnr.it/iai-api/v1/getResponse/{" + ticket + "}";
                    //String requestURL = "https://ecorridor.iit.cnr.it/iai-api/v1/getResponse/30dfcdc9-2554-49b1-a05b-68a182ab63ed/";
                    //String requestURL = "https://ecorridor2.iit.cnr.it/iai-api/v1/getResponse/" + ticket + "/";
                    String requestURL = "https://ecorridor-s2c.iit.cnr.it/iai-api/v1/getResponse/" + ticket + "/";
                //if (requestURL != "https://ecorridor2.iit.cnr.it/iai-api/v1/getResponse//") {
                if (requestURL != "https://ecorridor-s2c.iit.cnr.it/iai-api/v1/getResponse//") {
                    Log.e(TAG, "STRING REQUEST URL " + requestURL);
                    CallGETPollingVehicle connection = new CallGETPollingVehicle(requestURL, token, charset);

                    response = connection.finish();
                    Log.e(TAG, "RESULT ANALYTIC:" + response);

                    resultAnalytic.resultGlobal = response;
                }
                else {
                    Toast.makeText(Service1Vehicle.this, "Error: Ticket not received", Toast.LENGTH_LONG).show();
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
            //String numberOnly = resultAnalytic.resultGlobal.replaceAll("[^0-9]", "");
           // System.out.println(numberOnly);

            String s = resultAnalytic.resultGlobal;
            String resultReceived = null;
            double resultReceivedDouble;
            float flt = 0;
            
            //Creating a pattern to identify floats
            Pattern pat = Pattern.compile("[-]?[0-9]*,?[0-9]+,?[0-9]+,?[0-9]+");
            //matching the string with the pattern
            Matcher m = pat.matcher(s);
            //extracting and storing the float values
            while(m.find()) {
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
            System.out.println("The float value from the string is:");
            System.out.println(resultReceivedDouble);

            //////////////////////////////////
            // FIND QUARTILE

            String priceString = "";
            String price ="";

            if (isBetween(resultReceivedDouble, 0,1300)) {
                System.out.println("<1300");
                priceString = "Great deal! Your price is 29€/day.\n" +
                        "Keep it up! :)";
                quartile=1;
                price = "29€-day";
            }
            if (isBetween(resultReceivedDouble, 1301,1450)) {
                System.out.println(">1301 & <1450");
                priceString = "Great deal! Your price is 39€/day.\n" +
                        "Keep it up! :)";
                quartile=2;
                price = "39€-day";
            }
            if (isBetween(resultReceivedDouble, 1451,1600)) {
                System.out.println(">1451 & <1600");
                priceString = "Good deal! Your price is 49€/day.\n" +
                        "Slower speeds: create more liveability! :)";
                quartile=3;
                price = "49€-day";
            }
            if (isBetween(resultReceivedDouble, 1601,1750)) {
                System.out.println(">1601 & <1750");
                priceString = "Good deal! Your price is 59€/day.\n" +
                        "Slower speeds: create more liveability! :) ";
                quartile=4;
                price = "59€-day";
            }
            if (isBetween(resultReceivedDouble, 1751,5000)) {
                System.out.println(">1751");
                priceString = "Your price is 79€/day.\n" +
                        "Consider to reduce your speed! :)";
                quartile=5;
                price = "79€-day";
            }

            ////////////////////////////
            // PRINT RESULT

            spinner.setVisibility(View.GONE);
            result.setText(priceString);


            //////////////
            // TO SET AND SHOW RADAR
            setData();

            ///////////////////////////////
            // TO WRITE DATA INTO JSON FILE

            long mils = System.currentTimeMillis();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
            String sdtime = sdf1.format(new Date(mils)).toString() + " " + sdf2.format(new Date(mils)).toString();


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Datetime", sdtime);
                jsonObject.put("Service", "Car Sharing");
                jsonObject.put("Offer", price);
                jsonObject.put("Quartile", quartile);
                jsonObject.put("Ticket", ticket);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Convert JsonObject to String Format
            String userString = jsonObject.toString();

            String state = Environment.getExternalStorageState();
            mExternalStorageAvailable = mExternalStorageWriteable = true;

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


            // Define the File Path and its Name
            //File file = new File(Service1.this.getFilesDir(),"Service1DB.json"); // file accessible on by the application
            File file = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Service1DB.json"));
            BufferedWriter bufferedWriter = null;


            // NO WAY!, WE HAVE TO OPEN THE OLD FILE, READ, ADD THE NEW STRING AND RECREATE THE FILE
            // THIS WAY WE HAVE ORDERED FROM THE LAST TO THE FIRST
            if (!file.exists()) {
                Log.e("App","file not exist");
                try {
                    file.createNewFile();
                    FileWriter fileWriter = null;
                    try {
                        fileWriter = new FileWriter(file, true);
                        bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(userString);
                        bufferedWriter.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else {
                String Service1DBString = getData();

                String sumDB = userString + "\n"+ Service1DBString;

                ArrayList<String> JSONobjectList = new ArrayList<>();
                JSONobjectList.add(sumDB);

                File fileFileWriter = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Service1DB.json"));
                try {
                    //FileWriter fileWriter = new FileWriter(fileFileWriter, true);
                    FileWriter fileWriter = new FileWriter(fileFileWriter); // we have not to append becuse we want the last registration as first row
                    BufferedWriter bw = new BufferedWriter(fileWriter);
                    for (int i = 0; i < JSONobjectList.size(); i++) {
                        bw.write(JSONobjectList.get(i));
                    }
                        bw.flush();
                        bw.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
            }
        }
    }


        // to read data from JSON file
    public static String getData() {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Service1DB.json");
        //check whether file exists
        FileInputStream is = null;
        byte[] buffer = null;
        try {
            is = new FileInputStream(f);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buffer);
    }


    /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
    // RADAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.radar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refreshValues:
                setData();
                chart.invalidate();
                break;
            case R.id.toogleValues:
                for (IDataSet<?> set : chart.getData().getDataSets()) {
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }

                chart.invalidate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setData() {

        ArrayList<RadarEntry> employee1 = new ArrayList<>();
        ArrayList<RadarEntry> employee2 = new ArrayList<>();

        // generate random values in some cases.
        for (int i=0; i < NB_QUALITIES; i++) {
            if (i==3){
                employee1.add(new RadarEntry(quartile));

                //float val2 = (int) (Math.random() * MAX) + MIN;
                //employee2.add(new RadarEntry(val2));
                employee2.add(new RadarEntry(2.0F));
            }

            else {
                float val1 = (int) (Math.random() * MAX) + MIN;
                employee1.add(new RadarEntry(val1));

                float val2 = (int) (Math.random() * MAX) + MIN;
                employee2.add(new RadarEntry(val2));
            }
        }

        // we create two radar datasets
        RadarDataSet set1 = new RadarDataSet(employee1, "Current");
        set1.setColor(Color.BLUE);
        set1.setFillColor(Color.BLUE);
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightIndicators(false);
        set1.setDrawHighlightCircleEnabled(true);


        // we create two radar datasets
        RadarDataSet set2 = new RadarDataSet(employee2, "General");
        set2.setColor(Color.GREEN);
        set2.setFillColor(Color.GREEN);
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightIndicators(false);
        set2.setDrawHighlightCircleEnabled(true);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(10f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);
        chart.invalidate();

    }

    ////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    // end RADAR

    public static boolean isBetween(double value, int min, int max)
    {
        return((value > min) && (value < max));
    }

    ////////////////////////////////////////////////////////////////////////////////////

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Service1Vehicle.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Service1Vehicle.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Service1Vehicle.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Service1Vehicle.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

}