package georgikoemdzhiev.sensorlistener;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import georgikoemdzhiev.sensorlistener.database.AccDataPoint;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity   {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mTextView;
    RealmConfiguration realmConfig;
    Realm realm ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final Intent startServiceIntenr = new Intent(this,SensorService.class);

        mTextView = (TextView)findViewById(R.id.databaseTextView);
        realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //Handle tool bar menu item clicks...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            mTextView.setText("");

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realmObject) {
                    RealmResults<AccDataPoint> dataPoints = realmObject.where(AccDataPoint.class).findAll();

                    for(final AccDataPoint dataPoint:dataPoints){
                        final double x = dataPoint.getX();
                        final double y = dataPoint.getY();
                        final double z = dataPoint.getZ();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.append("X:" + x + "Y:" + y+ "Z:" + z + "\n");
                            }
                        });
                    }
                }
            });


            return true;
        }

        if(id == R.id.action_delete_all_data){
            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();
            mTextView.setText("");
        }


        return super.onOptionsItemSelected(item);
    }
}
