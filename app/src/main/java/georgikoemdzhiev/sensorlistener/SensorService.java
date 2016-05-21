package georgikoemdzhiev.sensorlistener;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;

import georgikoemdzhiev.sensorlistener.database.AccDataPoint;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "SensorService";

    private final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    // 3 = @Deprecated Orientation
    //private final static int SENS_GYROSCOPE = Sensor.TYPE_GYROSCOPE;

    RealmConfiguration realmConfig;
    Realm realm ;

    SensorManager mSensorManager;


    private ScheduledExecutorService mScheduler;
    private double[] gravity;
    private double[] linear_acceleration;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        gravity = new double[3];
        linear_acceleration = new double[3];
        realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);

        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent resultIntent = new Intent(this, MainActivity.class);
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Sensor Dashboard");
        builder.setContentText("Collecting sensor data..");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(resultPendingIntent);

        startForeground(1, builder.build());


        startMeasurement();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopMeasurement();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startMeasurement() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);



        // Register the listener
        if (mSensorManager != null) {
            if (accelerometerSensor != null) {
                mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Accelerometer found");
            }

        }
    }

    private void stopMeasurement() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        if (mScheduler != null && !mScheduler.isTerminated()) {
            mScheduler.shutdown();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        if(event.sensor.getType() == SENS_ACCELEROMETER) {

            final float alpha = (float) 0.8;


            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];


            double x = Math.abs(Math.round(linear_acceleration[0]));
            double y = Math.abs(Math.round(linear_acceleration[1]));
            double z = Math.abs(Math.round(linear_acceleration[2]));



            Log.d(TAG, "TimeStamp: " + event.timestamp + " X: " + " " + x + "  " + "Y: " + y + "  " +"Z: " + z);

            AccDataPoint accDataPoint = new AccDataPoint();
            accDataPoint.setX(x);
            accDataPoint.setY(y);
            accDataPoint.setZ(z);

            long timeInMillis = (new Date()).getTime()
                    + (event.timestamp - System.nanoTime()) / 1000000L;

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.UK);


            Date date=new Date(timeInMillis);

            accDataPoint.setTimestamp(formatter.format(date));

            // Persist your data easily
            realm.beginTransaction();
            realm.copyToRealm(accDataPoint);
            realm.commitTransaction();
        }
        //client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG,"Sensor accuracy changed");
    }
}