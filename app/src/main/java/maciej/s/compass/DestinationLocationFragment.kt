package maciej.s.compass

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText

class DestinationLocationFragment : DialogFragment() {

    private var isFieldEmpty = true

     var listener: DestinationLocationListener?=null

    interface DestinationLocationListener{
        fun onChangeDestination(latitude:Double,longitude: Double)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         return activity.let{
            val builder = AlertDialog.Builder(it)
            val inflater = activity?.layoutInflater

            val view = inflater?.inflate(R.layout.fragment_destination_location,null)
            builder.setView(view)
            builder.setTitle(getString(R.string.enter_destination_location))
                .setPositiveButton(getString(R.string.ok)) { sth1, sth2 ->
                    val latitudeEditText = view?.findViewById<TextInputEditText>(R.id.etLatitude)
                    val longitudeEditText = view?.findViewById<TextInputEditText>(R.id.etLongitude)
                    checkFields(latitudeEditText,longitudeEditText)
                    if(!isFieldEmpty){
                        val latitude = latitudeEditText!!.text.toString().toDouble()
                        val longitude = longitudeEditText!!.text.toString().toDouble()
                        listener!!.onChangeDestination(latitude, longitude)
                    }
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->

                }

            builder.create()
        }
    }

    private fun checkFields(
        latitudeEditText: TextInputEditText?,
        longitudeEditText: TextInputEditText?
    ) {
            val etLatitude = latitudeEditText?.text?.toString()
            val etLongitude = longitudeEditText?.text?.toString()
            if(etLatitude.isNullOrEmpty() || etLongitude.isNullOrEmpty()){
                isFieldEmpty = true
                displayNotEmptyCoordinatesToast()
            }else{
                isFieldEmpty= false
            }
    }

    private fun displayNotEmptyCoordinatesToast(){
        Toast.makeText(context,getString(R.string.coordinates_cannot_be_empty),Toast.LENGTH_SHORT).show()
    }

}