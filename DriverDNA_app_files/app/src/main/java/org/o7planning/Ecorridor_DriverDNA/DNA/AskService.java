package org.o7planning.Ecorridor_DriverDNA.DNA;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.Ecorridor_DriverDNA.R;

public class AskService extends AppCompatActivity {

    private ImageButton imageButtonBackarrowaskservice;
    private ImageButton imageButtontestanalytic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_askservice);

        // Find Button by its ID
        this.imageButtonBackarrowaskservice = (ImageButton) this.findViewById(R.id.backarrowaskservice);

        // When user click "Backarrow" button.
        this.imageButtonBackarrowaskservice.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Back to previous Activity.
                AskService.this.finish();
            }
        });


        // Find Button by its ID
        this.imageButtontestanalytic = (ImageButton) this.findViewById(R.id.imageButtontestanalytic);

        // When user click "test" button.
        this.imageButtontestanalytic.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create a Intent:
                // (This object contains content that will be sent to MyDriverDNA).
                Intent myIntent = new Intent(AskService.this, AnalyticOperationsServices.class);

                // Start MyDriverDNA.
                AskService.this.startActivity(myIntent);
            }
        });

    }
}