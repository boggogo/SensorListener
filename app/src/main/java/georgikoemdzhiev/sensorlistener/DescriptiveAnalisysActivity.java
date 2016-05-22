package georgikoemdzhiev.sensorlistener;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import georgikoemdzhiev.sensorlistener.Utils.DStatistics;
import georgikoemdzhiev.sensorlistener.database.AccDataPoint;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DescriptiveAnalisysActivity extends AppCompatActivity {
    private static final String TAG = DescriptiveAnalisysActivity.class.getSimpleName();
    private ScrollView mScrollView;
    private TextView mTextView;

    private RealmConfiguration realmConfig;
    private Realm realm;
    private DStatistics descriptiveStatistics;
    private ArrayList<Float> xValues = new ArrayList<>();
    private ArrayList<Float> yValues = new ArrayList<>();
    private ArrayList<String> chartTimeStampAlongXchrtAxies = new ArrayList<>();
    private  LineChart mLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_analisys);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // in this example, a LineChart is initialized from xml
        mLineChart = (LineChart) findViewById(R.id.chart);

        mScrollView = (ScrollView)findViewById(R.id.scrollViewDesc);
        mTextView = (TextView)findViewById(R.id.desStatisticsTextView);

        realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);

        descriptiveStatistics = new DStatistics();

        readDataFromDB();
        logDescriptiveData();

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

                    xValues.add((float) x);
                    yValues.add((float)y);

                    chartTimeStampAlongXchrtAxies.add(timeStamp);

                    descriptiveStatistics.add(x, y, z);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // lod data
                mTextView.setText("");
                // cut the first 20 readings (misleading) from the data

                mTextView.setText(descriptiveStatistics.toString());
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

        if(id == R.id.action_show_diagram){
            mTextView.setVisibility(View.INVISIBLE);
            mLineChart.setVisibility(View.VISIBLE);

            ArrayList<Entry> xValuesChart = new ArrayList<Entry>();
            ArrayList<Entry> yValuesChart = new ArrayList<Entry>();
            //ArrayList<Entry> valsComp2 = new ArrayList<Entry>();

            //ArrayList<String> xVals = new ArrayList<String>();

            // Crate an Entry for the X value from the xValues arrayList and add it to the chart values arrayList
            for(int i = 0; i<xValues.size();i++){
                Entry newEntry = new Entry(xValues.get(i),i);
                xValuesChart.add(newEntry);
                yValuesChart.add(new Entry(yValues.get(i),i));

            }

//            Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1
//            valsComp1.add(c1e1);
//            Entry c1e2 = new Entry(50.000f, 1); // 1 == quarter 2 ...
//            valsComp1.add(c1e2);
//            // and so on ...

            //Entry c2e1 = new Entry(120.000f, 0); // 0 == quarter 1
            //valsComp2.add(c2e1);
            //Entry c2e2 = new Entry(110.000f, 1); // 1 == quarter 2 ...
            //valsComp2.add(c2e2);

            LineDataSet setX = new LineDataSet(xValuesChart, "X values");
            setX.setAxisDependency(YAxis.AxisDependency.LEFT);
            setX.setColors(new int[] { R.color.red1}, DescriptiveAnalisysActivity.this);

            LineDataSet setY = new LineDataSet(yValuesChart, "Y values");
            setY.setAxisDependency(YAxis.AxisDependency.LEFT);
            setY.setColors(new int[] { R.color.colorPrimaryDark}, DescriptiveAnalisysActivity.this);
            //LineDataSet setComp2 = new LineDataSet(valsComp2, "Company 2");
           // setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);

            // use the interface ILineDataSet
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(setX);
            dataSets.add(setY);
            //dataSets.add(setComp2);




            //xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q");

            LineData data = new LineData(chartTimeStampAlongXchrtAxies, dataSets);
            mLineChart.setData(data);
            mLineChart.invalidate(); // refresh
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
