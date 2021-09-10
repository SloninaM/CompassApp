package maciej.s.compass

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_destination_location.*

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
            builder.setTitle("Enter destination location")
                .setPositiveButton("OK") { _, _ ->
                    checkFields()
                    if(!isFieldEmpty){
                        val latitude = view?.findViewById<TextInputEditText>(R.id.etLatitude)?.text.toString().toDouble()
                        val longitude = view?.findViewById<TextInputEditText>(R.id.etLongitude)?.text.toString().toDouble()
                        listener!!.onChangeDestination(latitude, longitude)
                    }
                }
                .setNegativeButton("Cancel") { _, _ ->

                }

            builder.create()
        }
    }

    private fun checkFields() {
        val etLatitude = etLatitude?.text.toString()
        val etLongitude = etLongitude?.text.toString()
        if(etLatitude.isEmpty() || etLongitude.isEmpty()){
            isFieldEmpty = true
            Toast.makeText(context,"Latitude and Longitude cannot be empty",Toast.LENGTH_SHORT).show()
        }else{
            isFieldEmpty = false
        }
    }

}