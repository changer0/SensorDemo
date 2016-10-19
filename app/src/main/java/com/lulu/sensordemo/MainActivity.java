package com.lulu.sensordemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mManager;
    private Sensor mLightSensor;
    private TextView mTxtInfo;
    private Sensor mGravitySensor;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtInfo = (TextView) findViewById(R.id.txt_info);

        // 1. 获取 SensorManager
        mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 2. 测试部分: 获取所有的传感器, 显示出来
        if (mManager != null) {
            List<Sensor> list = mManager.getSensorList(Sensor.TYPE_ALL);

            StringBuffer sb = new StringBuffer("传感器类型: \n");
            sb.append("size: ").append(list.size()).append("\n");

            for (Sensor sensor : list) {
                sb.append(sensor.getName().toString()).append("\n");
            }
            mTxtInfo.setText(sb.toString());

            // --------------------------
            //使用亮度传感器
            mLightSensor = mManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (mLightSensor != null) {
                //传感器获取之后一定要判断一下, 是否存在
                mManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_UI);
            }

            // ----------------------------
            // 设置重力传感器
            mGravitySensor = mManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            if (mGravitySensor != null) {
                mManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_UI);
            }

            // ----------------------------
            //设置加速度传感器
            mAccelerometer = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometer != null) {
                mManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
            }


        }


    }

    @Override
    protected void onDestroy() {
        // !!! 一定要取消注册, 否则会造成Activity内存泄漏
        if (mManager != null) {
            //以下代码可以终止单个传感器的检测
//            if (mLightSensor != null) {
//                //取消特定传感器的监听, 如果监听器还有其他传感器, 那么仍然可以使用不会被取消
//                mManager.unregisterListener(this, mLightSensor);
//            }
            mManager.unregisterListener(this);//取消监听器所处理的所有 传感器的检测
        }
        super.onDestroy();
    }


    ///////////////////////////////////////////////////////////////////////////
    // SensorListener的回调方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 当程序得到传感器的数值之后, 数据就会被封装成 SensorEvent 传递给这个方法
     * 根据这个方法的持续调用,  就可以了解数据的变化
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // 1. 处理传感器数据, 先检查传感器的类型;
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_LIGHT) {
            //processLight(event);
        } else if (type == Sensor.TYPE_GRAVITY) {
            //processGravity(event);
        } else if (type == Sensor.TYPE_ACCELEROMETER) {
            processAccelerometer(event);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * 处理加速度
     * @param event
     */
    private int mShakeCount = 0;
    private void processAccelerometer(SensorEvent event) {
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];
        StringBuilder sb = new StringBuilder();
        sb.append("AX: ").append(x).append('\n');
        sb.append("AY: ").append(y).append('\n');
        sb.append("AZ: ").append(z).append('\n');
        mTxtInfo.setText(sb.toString());

        float ax = Math.abs(x);
        float ay = Math.abs(y);
        float az = Math.abs(z);
        //计算绝对值, 超过12代表大幅度的晃动

        if (ax >= 12 || ay >= 12 || az >= 12) {
            mShakeCount++;
            if (mShakeCount > 4) {
                // TODO: 2016/10/19 摇了
                Toast.makeText(this, "小伙你摇得不轻啊", Toast.LENGTH_SHORT).show();
                mShakeCount = 0;
            }
        }

    }


    /**
     * 处理亮度变化
     * @param event
     */
    private void processLight(SensorEvent event) {
        // 2. 处理传感器的数值
        float[] values = event.values;
        // 3. 亮度数值 只有一个元素, 根据 SensorManger常量来处理
        float light = values[0];
        mTxtInfo.setText(String.valueOf(light));

        float screenLight = 0;
        if (light >= SensorManager.LIGHT_NO_MOON && light < SensorManager.LIGHT_FULLMOON) {
            screenLight = 0.1f;
        }else if (light >= SensorManager.LIGHT_FULLMOON && light < SensorManager.LIGHT_CLOUDY) {
            screenLight = 0.2f;
        }else if (light >= SensorManager.LIGHT_CLOUDY && light < SensorManager.LIGHT_SUNRISE) {
            screenLight = 0.3f;
        }else if (light >= SensorManager.LIGHT_SUNRISE && light < SensorManager.LIGHT_OVERCAST) {
            screenLight = 0.4f;
        }else if (light >= SensorManager.LIGHT_OVERCAST && light < SensorManager.LIGHT_SHADE) {
            screenLight = 0.5f;
        }else if (light >= SensorManager.LIGHT_SHADE && light < SensorManager.LIGHT_SUNLIGHT) {
            screenLight = 0.6f;
        }else if (light >= SensorManager.LIGHT_SUNLIGHT && light < SensorManager.LIGHT_SUNLIGHT_MAX) {
            screenLight = 0.7f;
        }else if (light >= SensorManager.LIGHT_SUNLIGHT_MAX ) {
            screenLight = 1f;
        }

        // TODO: 2016/10/19 调整屏幕亮度 (针对Activity)

        adjustScreenLight(screenLight);
    }

    private void adjustScreenLight(float l) {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = l;
        window.setAttributes(params);
    }

    /**
     * 处理重力变化
     * @param event
     */
    private void processGravity(SensorEvent  event) {
        float[] values = event.values;
        float gx = values[0];
        float gy = values[1];
        float gz = values[2];

        StringBuilder sb = new StringBuilder();
        sb.append("GX: ").append(gx).append('\n');
        sb.append("GY: ").append(gy).append('\n');
        sb.append("GZ: ").append(gz).append('\n');
        mTxtInfo.setText(sb.toString());


    }

}
