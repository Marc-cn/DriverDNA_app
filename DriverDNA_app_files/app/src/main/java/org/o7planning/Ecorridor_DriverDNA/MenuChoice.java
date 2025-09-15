package org.o7planning.Ecorridor_DriverDNA;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import org.o7planning.Ecorridor_DriverDNA.DNA.AskService;
import org.o7planning.Ecorridor_DriverDNA.DNA.MyDriverDNA;


public class MenuChoice extends AppCompatActivity {

    private Button buttonMyDriverDNA;
    private Button buttonmenuaskservice;
    private Button buttonlivedata;
    private ImageButton imageButtonBackarrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_menu);

        this.buttonMyDriverDNA = (Button) this.findViewById(R.id.buttonMyDriverDNA);
        this.buttonmenuaskservice = (Button) this.findViewById(R.id.buttonmenuaskaservice);
        this.buttonlivedata = (Button) this.findViewById(R.id.buttonlivedata);
        this.imageButtonBackarrow = (ImageButton) this.findViewById(R.id.backarrowmenu);


        // Called when the user clicks the buttonlivedata
        buttonlivedata.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create a Intent:
                // (This object contains content that will be sent to MyDriverDNA).
                Intent myIntent = new Intent(MenuChoice.this, OBDMain.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Start MyDriverDNA.
                MenuChoice.this.startActivity(myIntent);
            }
        });


        // Called when the user clicks the buttonmenucomplete.
        buttonMyDriverDNA.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create a Intent:
                // (This object contains content that will be sent to MyDriverDNA).
                Intent myIntent = new Intent(MenuChoice.this, MyDriverDNA.class);

                // Start MyDriverDNA.
                MenuChoice.this.startActivity(myIntent);
            }
        });

        // Called when the user clicks the buttonmenuasking.
        buttonmenuaskservice.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Create a Intent:
                // (This object contains content that will be sent to AskService).
                Intent myIntent = new Intent(MenuChoice.this, AskService.class);

                // Start.
                MenuChoice.this.startActivity(myIntent);
            }
        });

        // When user click "Backarrow" button.
        this.imageButtonBackarrow.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(MenuChoice.this, Home.class);

                // Start.
                MenuChoice.this.startActivity(myIntent);
            }
        });

    }


}