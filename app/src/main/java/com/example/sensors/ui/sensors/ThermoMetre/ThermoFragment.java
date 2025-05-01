package com.example.sensors.ui.sensors.ThermoMetre;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sensors.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class ThermoFragment extends Fragment implements SensorEventListener {

    private LineChart chart;
    private SensorManager mSensorManager;
    private Sensor mTempSensor;
    private ArrayList<Entry> entries = new ArrayList<>();
    private LineDataSet dataSet;
    private LineData lineData;

    public ThermoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (mTempSensor == null) {
            Toast.makeText(getContext(), "Ce capteur n\\'est pas supporté", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_thermo, container, false);
        chart = root.findViewById(R.id.chart);
        setupChart();
        return root;
    }

    private void setupChart() {
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.WHITE);

        Description desc = new Description();
        desc.setText("Température ambiante (°C)");
        chart.setDescription(desc);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);

        chart.getAxisRight().setEnabled(false);

        dataSet = new LineDataSet(entries, "Température");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);

        lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private void addEntry(SensorEvent event) {
        if (lineData != null) {
            entries.add(new Entry(entries.size(), event.values[0]));
            dataSet.notifyDataSetChanged();
            lineData.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(50);
            chart.moveViewToX(lineData.getEntryCount());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTempSensor != null) {
            mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        entries.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        entries.clear();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        addEntry(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}