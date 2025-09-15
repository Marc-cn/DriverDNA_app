package org.o7planning.Ecorridor_DriverDNA.DNA;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.o7planning.Ecorridor_DriverDNA.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ServiceHistory extends AppCompatActivity {

    private static final String TAG = AnalyticOperationsServices.class.getName();

    private ImageButton imageButtonBackarrowaskservicehistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicehistory);

        //////////////////////////////////////////////////////
        /////////////////////////////////////////////////////

        // Find Button by its ID
        this.imageButtonBackarrowaskservicehistory = (ImageButton) this.findViewById(R.id.backarrowaskservicehistory);

        // When user click "Backarrow" button.
        this.imageButtonBackarrowaskservicehistory.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Back to previous Activity.
                ServiceHistory.this.finish();
            }
        });

        ////////////////////////////////////////////////////////////////

        init();

    }

    public void init() {
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);

        int FontSizeHeader = 19;
        int FontSize = 12;

        TableRow tbrow0 = new TableRow(this);

        /////////////////////////////////////////////////////

        TextView tv0 = new TextView(this);
        tv0.setText(" DATETIME ");
        tv0.setTextColor(Color.WHITE);
        tv0.setTextSize(FontSizeHeader);
        tbrow0.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText(" SERVICE ");
        tv1.setTextColor(Color.WHITE);
        tv1.setTextSize(FontSizeHeader);
        tbrow0.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(" OFFER ");
        tv2.setTextColor(Color.WHITE);
        tv2.setTextSize(FontSizeHeader);
        tbrow0.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText(" QUARTILE ");
        tv3.setTextColor(Color.WHITE);
        tv3.setTextSize(FontSizeHeader);
        tbrow0.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setText(" TICKET ");
        tv4.setTextColor(Color.WHITE);
        tv4.setTextSize(FontSizeHeader);
        tbrow0.addView(tv4);

        stk.addView(tbrow0);

        ///////////////////////////////////////////////////////////////
        // READ DB DATA AND POPULATE DB

        //String Service1DBString = getData();
        JSONObject Service1DBJSON = null;
        BufferedReader someReader = null;

        try {
            someReader = new BufferedReader( new FileReader( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Service1DB.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String someData = null;

        while (true) {
            try {
                if (!(( someData = someReader.readLine() ) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Service1DBJSON = new JSONObject( someData );

                /////////
                TableRow tbrow = new TableRow(this);
                TextView t1v = new TextView(this);
                t1v.setText(Service1DBJSON.getString("Datetime"));
                t1v.setTextColor(Color.WHITE);
                t1v.setGravity(Gravity.CENTER);
                t1v.setTextSize(FontSize);
                tbrow.addView(t1v);

                TextView t2v = new TextView(this);
                t2v.setText(Service1DBJSON.getString("Service"));
                t2v.setTextColor(Color.WHITE);
                t2v.setGravity(Gravity.CENTER);
                t2v.setTextSize(FontSize);
                tbrow.addView(t2v);

                TextView t3v = new TextView(this);
                t3v.setText(Service1DBJSON.getString("Offer"));
                t3v.setTextColor(Color.WHITE);
                t3v.setGravity(Gravity.CENTER);
                t3v.setTextSize(FontSize);
                tbrow.addView(t3v);

                TextView t4v = new TextView(this);
                t4v.setText(Service1DBJSON.getString("Quartile"));
                t4v.setTextColor(Color.WHITE);
                t4v.setGravity(Gravity.CENTER);
                t4v.setTextSize(FontSize);
                tbrow.addView(t4v);

                TextView t5v = new TextView(this);
                t5v.setText(Service1DBJSON.getString("Ticket"));
                t5v.setTextColor(Color.WHITE);
                t5v.setGravity(Gravity.CENTER);
                t5v.setTextSize(FontSize);
                tbrow.addView(t5v);

                stk.addView(tbrow);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        //////////////////////////////////////////////////////////////////////

    }

    /*
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
    */

}