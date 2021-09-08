package maciej.s.compass

import android.location.Location
import android.location.LocationManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationOperationTest {

    private val krakowLocation = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = 50.06457424276781
        longitude = 19.944883991102998
    }

    private val rzeszowLocation = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = 50.04110085192915
        longitude = 21.999095871810884
    }

    private val warszawaLocation = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = 52.230094843820346
        longitude = 21.010013289133397
    }

    private val wroclawLocation = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = 51.10737692359446
        longitude = 17.03843410247427
    }

    private val bearingDelta = 5.0f


    @Test
    fun calculateBearing_krakowRzeszow_returnAbout90(){

        val locationOperation = LocationOperation(krakowLocation,rzeszowLocation)

        val bearing = locationOperation.calculateBearing()

        assertEquals(bearing,90.0f,bearingDelta)
    }

    @Test
    fun calculateBearing_warszawaWroclaw_returnAboutMinus115(){

        val locationOperation = LocationOperation(warszawaLocation,wroclawLocation)

        val bearing = locationOperation.calculateBearing()

        assertEquals(bearing,-115.0f,bearingDelta)
    }
}