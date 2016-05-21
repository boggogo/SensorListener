package georgikoemdzhiev.sensorlistener.Utils;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Georgi on 5/21/2016.
 */
public class DStatistics {
    private DescriptiveStatistics xDS;
    private DescriptiveStatistics yDS;
    private DescriptiveStatistics zDS;

    public DStatistics() {
        this.xDS = new DescriptiveStatistics();
        this.yDS = new DescriptiveStatistics();
        this.zDS = new DescriptiveStatistics();
    }

    public void add (double x, double y, double z){
        xDS.addValue(x);
        yDS.addValue(y);
        zDS.addValue(z);
    }
    //=======================MEAN===================
    public double getXMean(){
        return xDS.getMean();
    }

    public double getYMean(){
        return yDS.getMean();
    }

    public double getZMean(){
        return zDS.getMean();
    }
    //=====================MAX=======================
    public double getXMax(){
        return xDS.getMax();
    }

    public double getYMax(){
        return yDS.getMax();
    }

    public double getZMax(){
        return zDS.getMax();
    }
    //===================MIN=========================
    public double getXMin(){
        return xDS.getMin();
    }

    public double getYMin(){
        return yDS.getMin();
    }

    public double getZMin(){
        return zDS.getMin();
    }

    public void clear(){
        xDS.clear();
        yDS.clear();
        zDS.clear();
    }

    @Override
    public String toString() {
        return  "xDS: " + xDS.toString() + "\n" +
                "yDS=" + yDS.toString() + "\n" +
                "zDS=" + zDS.toString() + "\n";
    }
}
