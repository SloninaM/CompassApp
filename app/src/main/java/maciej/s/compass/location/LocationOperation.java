package maciej.s.compass.location;

import android.location.Location;

import androidx.annotation.NonNull;

public class LocationOperation {

    private Location currentLocation;
    private final Location destinationLocation;

    public LocationOperation(Location currentLocation, Location destinationLocation) {
        this.currentLocation = currentLocation;
        this.destinationLocation = destinationLocation;
    }


    public float calculateDistance(){
        return currentLocation.distanceTo(destinationLocation);
    }

    public float calculateBearing(){
        return currentLocation.bearingTo(destinationLocation);
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

}
