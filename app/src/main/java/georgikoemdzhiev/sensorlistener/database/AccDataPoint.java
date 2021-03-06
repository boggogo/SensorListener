package georgikoemdzhiev.sensorlistener.database;

import io.realm.RealmObject;

/**
 * Created by koemdzhiev on 21/05/16.
 */
public class AccDataPoint extends RealmObject {
    private double x;
    private double y;
    private double z;
    private String timestamp;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

