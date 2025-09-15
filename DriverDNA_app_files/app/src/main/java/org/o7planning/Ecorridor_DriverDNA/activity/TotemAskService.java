package org.o7planning.Ecorridor_DriverDNA.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.Ecorridor_DriverDNA.DNA.AnalyticOperationsServices;
import org.o7planning.Ecorridor_DriverDNA.R;

public class TotemAskService extends AppCompatActivity {

    private ImageButton imageButtonBackarrowTotemaskservice;
    private Button imageButtonCallTotemService1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calltotem);

        // Find Button by its ID
        this.imageButtonBackarrowTotemaskservice = (ImageButton) this.findViewById(R.id.backarrowTotemaskservice);

        // When user click "Backarrow" button.
        this.imageButtonBackarrowTotemaskservice.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Back to previous Activity.
                TotemAskService.this.finish();
            }
        });

        // Find Button by its ID
        this.imageButtonCallTotemService1 = (Button) this.findViewById(R.id.buttonTotemService1);

        // When user click "test" button.
        this.imageButtonCallTotemService1.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create a Intent:
                // (This object contains content that will be sent to MyDriverDNA).
                Intent myIntent = new Intent(TotemAskService.this, AnalyticOperationsServices.class);

                // Start MyDriverDNA.
                TotemAskService.this.startActivity(myIntent);

            }
        });

    }
}