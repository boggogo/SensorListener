package georgikoemdzhiev.sensorlistener;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity   {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent startServiceIntenr = new Intent(this,SensorService.class);


        startService(startServiceIntenr);


        findViewById(R.id.sensorBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isMyServiceRunning(SensorService.class)){
                    stopService(startServiceIntenr);
                    Toast.makeText(MainActivity.this,"Stopping service...", Toast.LENGTH_SHORT).show();
                }else{
                    startService(startServiceIntenr);
                    Toast.makeText(MainActivity.this,"Starting service...", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
