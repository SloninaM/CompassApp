package maciej.s.compass

import android.os.Build

class BuildVersionChecker {
    fun isBuildVersionMoreThan23(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

}
