package georgikoemdzhiev.sensorlistener;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import georgikoemdzhiev.sensorlistener.Utils.DStatistics;
import georgikoemdzhiev.sensorlistener.database.AccDataPoint;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DescriptiveAnalisysActivity extends AppCompatActivity {
    private static final String TAG = DescriptiveAnalisysActivity.class.getSimpleName();
    private RealmConfiguration realmConfig;
    private Realm realm;
    private DStatistics descriptiveStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_analisys);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);

        descriptiveStatistics = new DStatistics();

        readDataFromDB();

    }

    private void readDataFromDB() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmObject) {
                RealmResults<AccDataPoint> dataPoints = realmObject.where(AccDataPoint.class).findAll();

                for (final AccDataPoint dataPoint : dataPoints) {
                    final double x = dataPoint.getX();
                    final double y = dataPoint.getY();
                    final double z = dataPoint.getZ();
                    final String timeStamp = dataPoint.getTimestamp();

                    descriptiveStatistics.add(x, y, z);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // lod data
                Log.d(TAG,descriptiveStatistics.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_descriptive, menu);
        return true;
    }
    //Handle tool bar menu item clicks...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_refresh_statistics){
            logDescriptiveData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void logDescriptiveData(){
        // clear previous data
        descriptiveStatistics.clear();
        // read again from the db
        readDataFromDB();
    }

}
