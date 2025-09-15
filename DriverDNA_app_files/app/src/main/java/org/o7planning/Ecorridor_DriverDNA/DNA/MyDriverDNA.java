package org.o7planning.Ecorridor_DriverDNA.DNA;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

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

import org.o7planning.Ecorridor_DriverDNA.R;

import java.util.ArrayList;


public class MyDriverDNA extends AppCompatActivity {

    //private Button buttonactual;
    //private Button buttongeneral;
    private ImageButton imageButtonBackarrowdriverdna;

    //define max and min for radar chart
    public static final float MAX = 5, MIN = 1;
    //nb qualities radar chart
    public static final int NB_QUALITIES = 4;
    private RadarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_mydriverdna);

        // Find Button by its ID
        //this.buttonactual = (Button) this.findViewById(R.id.buttoncurrentdriverdna);
        //this.buttongeneral = (Button) this.findViewById(R.id.buttongeneraldriverdna);
        this.imageButtonBackarrowdriverdna = (ImageButton) this.findViewById(R.id.backarrowservice1);
        this.chart = (RadarChart) this.findViewById(R.id.chart);

        // we configure the radar chart
        chart.setBackgroundColor(Color.rgb(255, 255, 255));
        chart.getDescription().setEnabled(false);

        // hide legend
        chart.getLegend().setEnabled(true);

        //useful to export graph
        chart.setWebLineWidth(1f);
        chart.setWebColor(Color.BLACK);
        chart.setWebColorInner(Color.BLACK);
        chart.setWebAlpha(100);
        setData();
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



        /*
        // When user click "Actual" button.
        this.buttonactual.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });
        */

        // When user click "Backarrow" button.
        this.imageButtonBackarrowdriverdna.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Back to previous Activity.
                MyDriverDNA.this.finish();
            }
        });

    }

    // for radar chart


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

        // generate random values
        for (int i=0; i < NB_QUALITIES; i++) {
            float val1 = (int) (Math.random() * MAX) + MIN;
            employee1.add(new RadarEntry(val1));

            float val2 = (int) (Math.random() * MAX) + MIN;
            employee2.add(new RadarEntry(val2));
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

}