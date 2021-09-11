package maciej.s.compass.location;

import android.location.Location;

public class LocationOperation {

    private Location currentLocation;
    private final Location destinationLocation;

    public LocationOperation(Location destinationLocation) {
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
