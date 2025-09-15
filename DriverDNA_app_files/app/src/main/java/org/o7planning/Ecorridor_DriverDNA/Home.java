package org.o7planning.Ecorridor_DriverDNA;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class Home extends AppCompatActivity {
    private Button buttonstartdriverdna;
    private Button buttonstartIDS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);


        // Find Button by its ID
        this.buttonstartdriverdna = (Button) this.findViewById(R.id.buttonstartdriverdna);


        // Called when the user clicks the button1.
        buttonstartdriverdna.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create a Intent:
                // (This object contains content that will be sent to MenuChoice).
                Intent myIntent = new Intent(Home.this, MenuChoice.class);

                // Start MenuChoice.
                Home.this.startActivity(myIntent);
            }
        });

        // BUTTON IDS
        // Find Button by its ID
        this.buttonstartIDS = (Button) this.findViewById(R.id.buttonstartids);


        // Called when the user clicks the button1.
        buttonstartIDS.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create a Intent:
                // (This object contains content that will be sent to MenuChoice).
                Intent myIntent = new Intent(Home.this, IDS.class);

                // Start MenuChoice.
                Home.this.startActivity(myIntent);
            }
        });
    }

}