package org.o7planning.Ecorridor_DriverDNA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.enums.AvailableCommandNames;
import com.google.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;
import org.o7planning.Ecorridor_DriverDNA.activity.ForegroundService;
import org.o7planning.Ecorridor_DriverDNA.activity.Preference;
import org.o7planning.Ecorridor_DriverDNA.activity.RetrieveTokenVehicle;
import org.o7planning.Ecorridor_DriverDNA.activity.callCreateDPOVehicle;
import org.o7planning.Ecorridor_DriverDNA.config.ObdConfig;
import org.o7planning.Ecorridor_DriverDNA.config.errorBL;
import org.o7planning.Ecorridor_DriverDNA.config.liveDataONOFF;
import org.o7planning.Ecorridor_DriverDNA.io.AbstractGatewayService;
import org.o7planning.Ecorridor_DriverDNA.io.MockObdGatewayService;
import org.o7planning.Ecorridor_DriverDNA.io.ObdCommandJob;
import org.o7planning.Ecorridor_DriverDNA.io.ObdGatewayService;
import org.o7planning.Ecorridor_DriverDNA.io.ObdProgressListener;
import org.o7planning.Ecorridor_DriverDNA.net.ObdReading;
import org.o7planning.Ecorridor_DriverDNA.trips.TripLog;
import org.o7planning.Ecorridor_DriverDNA.trips.TripRecord;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;



public class OBDMain extends RoboActivity implements ObdProgressListener, LocationListener, GpsStatus.Listener {

    private static final String TAG = OBDMain.class.getName();
    private static final int NO_BLUETOOTH_ID = 0;
    private static final int BLUETOOTH_DISABLED = 1;
    private static final int START_LIVE_DATA = 2;
    private static final int STOP_LIVE_DATA = 3;
    private static final int SETTINGS = 4;
    private static final int TABLE_ROW_MARGIN = 7;
    private static final int SAVE_TRIP_NOT_AVAILABLE = 11;
    private static final int REQUEST_ENABLE_BT = 1234;
    private static boolean bluetoothDefaultIsEnable = false;

    /////////
    private static final int PERMISSION_REQUEST_CODE = 100;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    //////////


    static {
        RoboGuice.setUseAnnotationDatabases(false);
    }

    public Map<String, String> commandResult = new HashMap<>();
    private TripLog triplog;
    private TripRecord currentTrip;
    private Button buttonStart;
    private Button buttonStop;
    private Button buttonSettings;
    private Button buttonPair;
    private ImageButton imageButtonBackarrowobdmain;
    public LocationManager locationManager;
    protected Context context;
    double lat;
    double lon;
    double bea;
    public boolean gps_enabled = false;


    @SuppressLint("NonConstantResourceId")
    @InjectView(R.id.BT_STATUS)
    private TextView btStatusTextView;
    @InjectView(R.id.OBD_STATUS)
    private TextView obdStatusTextView;
    @InjectView(R.id.vehicle_view)
    private RelativeLayout vv;
    @InjectView(R.id.textViewprotocol)
    private TextView textviewprotocol;
    @InjectView(R.id.map)
    private MapView map;
    @Inject
    private PowerManager powerManager;
    @Inject
    private SharedPreferences prefs;
    private boolean isServiceBound;
    private AbstractGatewayService service;
    public int counter = 0;
    ArrayList<String> JSONobjectList = new ArrayList<>();
    JSONObject jsontime = new JSONObject();
    private PowerManager.WakeLock wakeLock = null;
    private boolean preRequisites = true;
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (errorBL.error == true){
            String message = "Bluetooth connection error. Please turn off/on the OBD Bluetooth device";
            new AlertDialog.Builder(OBDMain.this)
                    .setMessage(message)
                    .setPositiveButton("ok", null)
                    .show();
            errorBL.error = false;
        }

        Log.e(TAG, "ONCREATE MAIN");

        ///////////////////////////
        // to retrieve value registration is on

        if(savedInstanceState != null) {
            liveDataONOFF.liveDataOn = savedInstanceState.getBoolean("liveDataOn");
        }

        if (liveDataONOFF.liveDataOn != null) {
            liveDataONOFF.liveDataOn = true;
        }

       /////////////////////////////

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.main);

       //////////////////////// work even screen off
        Context mContext = getApplicationContext();
        /*
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock =  powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"motionDetection:keepAwake");
        if ((wakeLock != null) &&           // we have a WakeLock
                (wakeLock.isHeld() == false)) {  // but we don't hold it
            wakeLock.acquire();
        }
        */
            ///////////////////////////

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkgpsPermissions();
                }
            }

            ////////////////////////////////////////////////////

            org.osmdroid.config.Configuration.getInstance().load(mContext, PreferenceManager.getDefaultSharedPreferences(mContext));

            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);

            IMapController mapController = map.getController();
            mapController.setZoom(15);

            GeoPoint startPoint = new GeoPoint(43.716667, 10.400000);
            mapController.setCenter(startPoint);


            Marker startMarker = new Marker(map);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(startMarker);

            ///////////////////////////////////////////////////////////

            final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btAdapter != null)
                bluetoothDefaultIsEnable = btAdapter.isEnabled();

            /////////////////////////////////////////////

            // create a log instance to use by this application
            triplog = TripLog.getInstance(this.getApplicationContext());

            obdStatusTextView.setText(getString(R.string.status_obd_disconnected));
            textviewprotocol.setText(getString(R.string.status_protocol));


            this.buttonStart = (Button) this.findViewById(R.id.buttonStart);
            // When user click "buttonStart" button.
            this.buttonStart.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startLiveData();
                }
            });


            this.buttonStop = (Button) this.findViewById(R.id.buttonStop);
            // When user click "buttonStop" button.
            this.buttonStop.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    stopLiveData();
                }
            });

            this.buttonSettings = (Button) this.findViewById(R.id.buttonSettings);
            // When user click "buttonSettings" button.
            this.buttonSettings.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Create a Intent:
                    // (This object contains content that will be sent to).
                    Intent myIntent = new Intent(OBDMain.this, Preference.class);

                    OBDMain.this.startActivity(myIntent);

                }
            });

            this.buttonPair = (Button) this.findViewById(R.id.buttonPair);
            // Called when the user clicks the buttonPair
            buttonPair.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // Create a Intent:
                    // (This object contains content that will be sent to ...).
                    Intent myIntent = new Intent(OBDMain.this, MainBluetoothService.class);

                    OBDMain.this.startActivity(myIntent);
                }
            });

            this.imageButtonBackarrowobdmain = (ImageButton) this.findViewById(R.id.backarrowobdmain);
            // When user click "Backarrow" button.
            this.imageButtonBackarrowobdmain.setOnClickListener(new ImageButton.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(OBDMain.this, MenuChoice.class);
                    OBDMain.this.startActivity(myIntent);
                }
            });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if ( liveDataONOFF.liveDataOn != null){
        outState.putBoolean("liveDataOn", liveDataONOFF.liveDataOn);
        Log.e(TAG, "liveDataOn" + liveDataONOFF.liveDataOn);}
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "onRestoreInstanceState");
    }


    @Override
    protected void onStart() {

        if (liveDataONOFF.liveDataOn != null) {
            Log.e(TAG, "ONSTART" + liveDataONOFF.liveDataOn);
        }
        super.onStart();
    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            bea = location.getBearing();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Todo your code
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Todo your code
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Todo your code
        }
    };

    ////////////////////////////////////////////////////////
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, START_LIVE_DATA, 0, getString(R.string.menu_start_live_data));
        menu.add(0, STOP_LIVE_DATA, 0, getString(R.string.menu_stop_live_data));
        menu.add(0, SETTINGS, 0, getString(R.string.menu_settings));
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case START_LIVE_DATA:
                startLiveData();
                return true;
            case STOP_LIVE_DATA:
                stopLiveData();
                return true;
            case SETTINGS:
                startActivity(new Intent(this, Preference.class));
                return true;
        }
        return false;
    }


    private final Runnable mQueueCommands = new Runnable() {
        public void run() {
            if (service != null && service.isRunning() && service.queueEmpty()) {
                queueCommands();

                final int posLen = 7;

                Log.e(TAG, "RUNNABLE");

                /////////////////////////////////////

                GeoPoint startPointactual = new GeoPoint(lat, lon);
                IMapController mapController = map.getController();
                mapController.setCenter(startPointactual);
                map.getOverlays().clear();

                Marker startMarker = new Marker(map);
                startMarker.setPosition(startPointactual);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(startMarker);

                /////////////////////////////////////

                if (prefs.getBoolean(Preference.ENABLE_FULL_LOGGING_KEY, false)) {

                    // Write the current reading to CSV

                    final String vin = prefs.getString(Preference.VEHICLE_ID_KEY, "UNDEFINED_VIN");
                    Map<String, String> temp = new HashMap<>(commandResult);
                    temp.putAll(commandResult);
                    ObdReading reading = new ObdReading(lat, lon, bea, System.currentTimeMillis(), vin, temp);
                    if (reading != null) {
                        JSONObject json = new JSONObject();

                        try {
                            json.put("lat", lat);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            json.put("long", lon);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            json.put("bea", bea);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        String vincode = reading.getVin();
                        try {
                            json.put("vin", vincode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Map<String, String> mapreadings = reading.getReadings();
                        for (Map.Entry<String, String> entry : mapreadings.entrySet()) {
                            try {
                                json.put(entry.getKey(), entry.getValue());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                        long time = reading.getTimestamp();
                        try {
                            jsontime.put(String.valueOf(time), json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        counter += 1;
                        if (counter > 100) {

                            String jsonString = null;
                            try {
                                jsonString = jsontime.toString(4);
                                jsontime = new JSONObject();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONobjectList.add(jsonString);

                            long mils = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("_dd_MM_yyyy_HH_mm_ss");

                            File fileFileWriter = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + sdf.format(new Date(mils)).toString() + ".json"));
                            try {
                                FileWriter fileWriter = new FileWriter(fileFileWriter, true);
                                BufferedWriter bw = new BufferedWriter(fileWriter);
                                for (int i = 0; i < JSONobjectList.size(); i++) {
                                    bw.write(JSONobjectList.get(i));
                                }
                                bw.flush();
                                bw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ////////////////////////////////////// CALL DPO

                            callCreateDPOVehicle task = new callCreateDPOVehicle();
                            String callresult = String.valueOf(task.execute(fileFileWriter));
                            Log.e(TAG, "CALLDPO EXECUTE");
                            Log.e(TAG, "Button callDPO"+ callresult);

                            //////////////////////////////////////////// delete file
                            /*
                            boolean deleted = fileFileWriter.delete();
                            if(!deleted){
                                boolean deleted2 = false;
                                try {
                                    deleted2 = fileFileWriter.getCanonicalFile().delete();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if(!deleted2){
                                    boolean deleted3 = getApplicationContext().deleteFile(fileFileWriter.getName());
                                }
                            }
                             */
                            /////////////////////////////////////////////// finish delete file
                            /////////////////////////////////////////////// finish call create DPO

                            counter = 0;
                            JSONobjectList.clear();

                        }
                    }

                }
                commandResult.clear();
            }
            // run again in period defined in preferences
            new Handler().postDelayed(mQueueCommands, Preference.getObdUpdatePeriod(prefs));
        }
    };

    private final ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, className.toString() + " service is bound");
            isServiceBound = true;
            service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
            service.setContext(OBDMain.this);
            Log.d(TAG, "Starting live data");
            try {
                service.startService();
                if (preRequisites)
                    btStatusTextView.setText(getString(R.string.status_bluetooth_connected));
            } catch (IOException ioe) {
                Log.e(TAG, "Failure Starting live data");
                btStatusTextView.setText(getString(R.string.status_bluetooth_error_connecting));
                doUnbindService();
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        // This method is *only* called when the connection to the service is lost unexpectedly
        // and *not* when the client unbinds (http://developer.android.com/guide/components/bound-services.html)
        // So the isServiceBound attribute should also be set to false when we unbind from the service.
        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, className.toString() + " service is unbound");
            isServiceBound = false;
        }
    };

    public static String LookUpCommand(String txt) {
        for (AvailableCommandNames item : AvailableCommandNames.values()) {
            if (item.getValue().equals(txt)) return item.name();
        }
        return txt;
    }

    public void updateTextView(final TextView view, final String txt) {
        new Handler().post(new Runnable() {
            public void run() {
                view.setText(txt);
            }
        });
    }

    public void stateUpdate(final ObdCommandJob job) {
        final String cmdName = job.getCommand().getName();
        String cmdResult = "";
        final String cmdID = LookUpCommand(cmdName);

        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
            cmdResult = job.getCommand().getResult();
            if (cmdResult != null && isServiceBound) {
                obdStatusTextView.setText(cmdResult.toLowerCase());
            }
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
            if (isServiceBound)
                stopLiveData();
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
            cmdResult = getString(R.string.status_obd_no_support);
        } else {
            cmdResult = job.getCommand().getFormattedResult();
            if (isServiceBound)
                obdStatusTextView.setText(getString(R.string.status_obd_data));
        }

        if (vv.findViewWithTag(cmdID) != null) {
            TextView existingTV = vv.findViewWithTag(cmdID);
            existingTV.setText(cmdResult);
        } else addTableRow(cmdID, cmdName, cmdResult);
        commandResult.put(cmdID, cmdResult);
        updateTripStatistic(job, cmdID);
    }


    // Current SPEED, RPM and ENGINE TIME

    private void updateTripStatistic(final ObdCommandJob job, final String cmdID) {

        if (currentTrip != null) {
            if (cmdID.equals(AvailableCommandNames.SPEED.toString())) {
                SpeedCommand command = (SpeedCommand) job.getCommand();
                currentTrip.setSpeedMax(command.getMetricSpeed());
            } else if (cmdID.equals(AvailableCommandNames.ENGINE_RPM.toString())) {
                RPMCommand command = (RPMCommand) job.getCommand();
                currentTrip.setEngineRpmMax(command.getRPM());
            } else if (cmdID.endsWith(AvailableCommandNames.ENGINE_RUNTIME.toString())) {
                RuntimeCommand command = (RuntimeCommand) job.getCommand();
                currentTrip.setEngineRuntime(command.getFormattedResult());
            }

        }
    }


    protected void onPause() {
        Log.e(TAG, "OBD MAIN ONPAUSE");
        startServiceForeground();
        releaseWakeLockIfHeld();
        super.onPause();
    }

    protected void onStop() {
        Log.e(TAG, "OBD MAIN ONSTOOP");
        super.onStop();
        startServiceForeground();
        releaseWakeLockIfHeld();
    }

    @Override
    protected void onDestroy() {

        Log.e(TAG, "OBD MAIN ONDESTROY");

        if (locationManager != null) {
            locationManager.removeGpsStatusListener((GpsStatus.Listener) this);
            locationManager.removeUpdates(locationListener);
        }

        releaseWakeLockIfHeld();
        if (isServiceBound) {
            doUnbindService();
        }

        endTrip();

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null && btAdapter.isEnabled() && !bluetoothDefaultIsEnable)
            btAdapter.disable();

        gpsStop();

        super.onDestroy();

    }

    /**
     * If lock is held, release. Lock will be held when the service is running.
     */
    private void releaseWakeLockIfHeld() {
        if (wakeLock.isHeld())
            wakeLock.release();
    }

    @SuppressLint("InvalidWakeLockTag")
    protected void onResume() {
        super.onResume();

        /////////////////////////////////////////
        // to activate screen

        if (liveDataONOFF.liveDataOn != null) {
                Log.e(TAG, "ONRESUME" + liveDataONOFF.liveDataOn);
        }
        //////////////////////////////////////////////////

        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,"ObdReader");

        // get Bluetooth device
        final BluetoothAdapter btAdapter = BluetoothAdapter
                .getDefaultAdapter();

        preRequisites = btAdapter != null && btAdapter.isEnabled();
        if (!preRequisites && prefs.getBoolean(Preference.ENABLE_BT_KEY, false)) {
            preRequisites = btAdapter != null && btAdapter.enable();
        }

        if (!preRequisites) {
            showDialog(BLUETOOTH_DISABLED);
            btStatusTextView.setText(getString(R.string.status_bluetooth_disabled));
        } else {
            btStatusTextView.setText(getString(R.string.status_bluetooth_ok));
        }

        org.osmdroid.config.Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }


    @SuppressLint({"MissingPermission", "NewApi"})
    private void startLiveData() {
        Log.e(TAG, "Starting live data..");

        liveDataONOFF.liveDataOn = true;

        Log.e(TAG, "Starting live data..  " + liveDataONOFF.liveDataOn);

        doBindService();

        currentTrip = triplog.startTrip();
        if (currentTrip == null)
            showDialog(SAVE_TRIP_NOT_AVAILABLE);

        // start command execution
        new Handler().post(mQueueCommands);

        ///////////////////////////////////////

        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            //Toast.makeText(getApplicationContext(), "Please enable Location", Toast.LENGTH_LONG).show();
            Log.e(TAG, "...START GPS...");
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkgpsPermissions();
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        gps_enabled = true;
        //////////////////////////////////////////////

        // screen won't turn off until wakeLock.release()
        //wakeLock.acquire();


        if (prefs.getBoolean(Preference.ENABLE_FULL_LOGGING_KEY, false)) {

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
        }
    }

    private synchronized void gpsStop() {
        if (gps_enabled == true) {
            locationManager.removeUpdates(locationListener);
            gps_enabled = false;
        }
    }

    /////////////////////////////////////////////////////////

    @SuppressLint("LongLogTag")
    private void stopLiveData() {
        Log.d(TAG, "Stopping live data..");

        String token = null;

        Toast.makeText(this, "Live data stopped", Toast.LENGTH_LONG).show();

        gpsStop();

        doUnbindService();
        endTrip();

        releaseWakeLockIfHeld();

        //////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        //TO ACCESS EXTERNAL STORAGE FOR TEST

        String state = Environment.getExternalStorageState();
        mExternalStorageAvailable = mExternalStorageWriteable = true;
        Toast.makeText(getApplicationContext(), "We Can Read And Write ", Toast.LENGTH_LONG).show();

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

        //////////////////////////////////////////////////////////
        // DECOMMENT ONLY IF VEHICLE IS RUNNING
        // TO SAVE DATA IF VEHICLE IS RUNNING

        /*

        if (jsontime.length() >= 1) {

            try {
                token = new RetrieveTokenVehicle().execute("https://ecorridor.iit.cnr.it/auth/realms/c3isp/protocol/openid-connect/token").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.e("TOKEN:", token);

            //////////////////////////////////////////////////////////////////

            String jsonString = null;
            try {
                jsonString = jsontime.toString(4);
                jsontime = new JSONObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONobjectList.add(jsonString);

            long mils = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("_dd_MM_yyyy_HH_mm_ss");

            File fileFileWriter = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + sdf.format(new Date(mils)).toString() + ".json"));

            try {
                FileWriter fileWriter = new FileWriter(fileFileWriter, true);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                for (int i = 0; i < JSONobjectList.size(); i++) {
                    bw.write(JSONobjectList.get(i));
                }
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            ////////////////////////////////////// CALL DPO VEHICLE !!!!!!!!
            callCreateDPOVehicle task = new callCreateDPOVehicle();
            String callresult = String.valueOf(task.execute(fileFileWriter, token));
            Log.e(TAG, "CALLDPO EXECUTED");
            Log.e(TAG, "Button callDPO"+ callresult);

        //////////////////////////////////////////////////
        // END TO SAVE DATA IF VEHICLE IS RUNNING


         */
        //////////////////////////////////////////////////////////
        // DECOMMENT IF WANT TO TEST CREATE DPO OUT OF THE VEHICLE WITH PIPPO TEST JSON

        int test = 0;

        if (test==0) {

                try {
                    //token = new RetrieveTokenVehicle().execute("https://ecorridor2.iit.cnr.it/auth/realms/ecorridor/protocol/openid-connect/token").get();
                    token = new RetrieveTokenVehicle().execute("https://ecorridor-s2c.iit.cnr.it/auth/realms/ecorridor/protocol/openid-connect/token").get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.e("TOKEN:", token);

                // added for test
                File fileFileWriter2 = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator  + "pippoTest.json"));
                //File fileFileWriter2 = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator  + "test-attack-160223.txt"));

                ////////////////////////////////////// CALL DPO VEHICLE !!!!!!!!
                callCreateDPOVehicle task = new callCreateDPOVehicle();
                //String callresult = String.valueOf(task.execute(fileFileWriter, token));
                String callresult = String.valueOf(task.execute(fileFileWriter2, token));

                Log.e(TAG, "CALLDPO EXECUTED");
                Log.e(TAG, "Button callDPO"+ callresult);

        //////////////////////////////////////////////////
        // END TO TEST CREATE DPO OUT OF THE VEHICLE



        //////////////////////////////////////////// delete file
        /*
        boolean deleted = fileFileWriter.delete();
        if(!deleted){
            boolean deleted2 = false;
            try {
                 deleted2 = fileFileWriter.getCanonicalFile().delete();
                 } catch (IOException e) {
                  e.printStackTrace();
                 }
                  if(!deleted2){
                    boolean deleted3 = getApplicationContext().deleteFile(fileFileWriter.getName());
                  }
                 }
         */
            /////////////////////////////////////////////// finish delete file
            /////////////////////////////////////////////// finish call create DPO
        }

        counter = 0;
        JSONobjectList.clear();
        stopServiceForeground ();

        /////////////////
        onCreate(null);
        //////////////////

    }

    /*
    public static void restartActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }
    */

    public void onLocationChanged(Location location) {
    }

    protected void endTrip() {
        if (currentTrip != null) {
            currentTrip.setEndDate(new Date());
            triplog.updateRecord(currentTrip);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem startItem = menu.findItem(START_LIVE_DATA);
        MenuItem stopItem = menu.findItem(STOP_LIVE_DATA);
        MenuItem settingsItem = menu.findItem(SETTINGS);
        //MenuItem getDTCItem = menu.findItem(GET_DTC);

        if (service != null && service.isRunning()) {
            //getDTCItem.setEnabled(false);
            startItem.setEnabled(false);
            stopItem.setEnabled(true);
            settingsItem.setEnabled(false);
        } else {
            //getDTCItem.setEnabled(true);
            stopItem.setEnabled(false);
            startItem.setEnabled(true);
            settingsItem.setEnabled(true);
        }

        return true;
    }

    @SuppressLint("RtlHardcoded")
    private void addTableRow(String id, String key, String val) {

        TableRow tr = new TableRow(this);
        MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(TABLE_ROW_MARGIN, TABLE_ROW_MARGIN, TABLE_ROW_MARGIN,
                TABLE_ROW_MARGIN);
        tr.setLayoutParams(params);

        TextView name = new TextView(this);
        name.setGravity(Gravity.RIGHT);
        name.setText(key + ": ");
        TextView value = new TextView(this);
        value.setGravity(Gravity.LEFT);
        value.setText(val);
        value.setTag(id);
        tr.addView(name);
        tr.addView(value);
    }

    /**
     *
     */
    private void queueCommands() {
        if (isServiceBound) {
            for (ObdCommand Command : ObdConfig.getCommands()) {
                if (prefs.getBoolean(Command.getName(), true))
                    service.queueJob(new ObdCommandJob(Command));
            }
        }
    }

    private void doBindService() {
        if (!isServiceBound) {
            Log.d(TAG, "Binding OBD service..");
            if (preRequisites) {
                btStatusTextView.setText(getString(R.string.status_bluetooth_connecting));
                Intent serviceIntent = new Intent(this, ObdGatewayService.class);
                bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
            } else {
                btStatusTextView.setText(getString(R.string.status_bluetooth_disabled));
                Intent serviceIntent = new Intent(this, MockObdGatewayService.class);
                bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
            }
        }
    }

    private void doUnbindService() {
        if (isServiceBound) {
            if (service.isRunning()) {
                service.stopService();
                if (preRequisites)
                    btStatusTextView.setText(getString(R.string.status_bluetooth_ok));
            }
            Log.d(TAG, "Unbinding OBD service..");
            unbindService(serviceConn);
            isServiceBound = false;
            obdStatusTextView.setText(getString(R.string.status_obd_disconnected));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                btStatusTextView.setText(getString(R.string.status_bluetooth_connected));
            } else {
                Toast.makeText(this, R.string.text_bluetooth_disabled, Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    ////////////////////////////////////////////////////////////////////////////////////

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(OBDMain.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(OBDMain.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(OBDMain.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(OBDMain.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("NewApi")
    private void checkgpsPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkgpsPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else{
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
            break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                break;
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void startServiceForeground() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.setClass(OBDMain.this, OBDMain.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopServiceForeground() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

}
