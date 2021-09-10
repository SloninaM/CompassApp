package maciej.s.compass

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionManager {
    fun checkSelfPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
                                context,
                         permission
                     ) == PackageManager.PERMISSION_GRANTED
    }

}
