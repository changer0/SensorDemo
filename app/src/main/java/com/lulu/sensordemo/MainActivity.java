package com.lulu.sensordemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtInfo = (TextView) findViewById(R.id.txt_info);

        // 1. 获取 SensorManager
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 2. 测试部分: 获取所有的传感器, 显示出来
        if (manager != null) {
            List<Sensor> list = manager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor sensor : list) {

            }
        }


    }
}
