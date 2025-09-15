package org.o7planning.Ecorridor_DriverDNA.DNA;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.Ecorridor_DriverDNA.R;

public class AnalyticOperationsServices extends AppCompatActivity {

    private static final String TAG = AnalyticOperationsServices.class.getName();

    private ImageButton imageButtonBackarrowaskservice;
    private Button imageButtonCallAnalyticService1;
    private Button imageButtonCallAnalyticServiceHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callanalytic);

        //////////////////////////////////////////////////////
        /////////////////////////////////////////////////////

        // Find Button by its ID
        this.imageButtonBackarrowaskservice = (ImageButton) this.findViewById(R.id.backarrowaskservice);

        // When user click "Backarrow" button.
        this.imageButtonBackarrowaskservice.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Back to previous Activity.
                AnalyticOperationsServices.this.finish();
            }
        });

        ////////////////////////////////////////////////////////////////

        // Find Button by its ID
        this.imageButtonCallAnalyticService1 = (Button) this.findViewById(R.id.buttonService1);

        // When user click "test" button.
        this.imageButtonCallAnalyticService1.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create a Intent:
                // (This object contains content that will be sent to MyDriverDNA).
                Intent myIntent = new Intent(AnalyticOperationsServices.this, Service1Vehicle.class);

                // Start MyDriverDNA.
                AnalyticOperationsServices.this.startActivity(myIntent);


            }
        });

        ////////////////////////////////////////////////////////////////

        // Find Button by its ID
        this.imageButtonCallAnalyticServiceHistory = (Button) this.findViewById(R.id.buttonServiceHistory);

        // When user click "test" button.
        this.imageButtonCallAnalyticServiceHistory.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create a Intent:
                // (This object contains content that will be sent to MyDriverDNA).
                Intent myIntent = new Intent(AnalyticOperationsServices.this, ServiceHistory.class);

                // Start MyDriverDNA.
                AnalyticOperationsServices.this.startActivity(myIntent);


            }
        });

    }
}