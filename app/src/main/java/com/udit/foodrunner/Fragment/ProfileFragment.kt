package com.udit.foodrunner.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.udit.foodrunner.ConnectionManager.ConnectionManager
import com.udit.foodrunner.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment(val contextparams : Context) : Fragment() {

    lateinit var txtProfileName: TextView
    lateinit var txtProfileId: TextView
    lateinit var txtProfileAddress: TextView
    lateinit var txtProfileContactNo: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val sharedPreferences = contextparams.getSharedPreferences(
            "RestaurantPreference",
            Context.MODE_PRIVATE
        )

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        txtProfileName = view.findViewById(R.id.txt_profilename)
        txtProfileAddress = view.findViewById(R.id.txt_profileAddress)
        txtProfileContactNo = view.findViewById(R.id.txt_profileContactNo)
        txtProfileId = view.findViewById(R.id.txt_profileEmailid)

        txtProfileName.text = sharedPreferences.getString("name", "UserName")
        txtProfileId.text = sharedPreferences.getString("email", "UserName")
        txtProfileAddress.text = sharedPreferences.getString("address", "UserName")
        txtProfileContactNo.text = sharedPreferences.getString("mobile_number", "UserName")
        return view
    }

    override fun onResume() {
        if(!ConnectionManager().checkConnectivity(activity as Context)){
                val alterDialog=androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                alterDialog.setTitle("No Internet")
                alterDialog.setMessage("Internet Connection can't be establish!")
                alterDialog.setPositiveButton("Open Settings"){ _, _ ->
                    val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)//open wifi settings
                    startActivity(settingsIntent)
                }

                alterDialog.setNegativeButton("Exit"){ _, _ ->
                    ActivityCompat.finishAffinity(activity as Activity)//closes all the instances of the app and the app closes completely
                }
                alterDialog.setCancelable(false)

                alterDialog.create()
                alterDialog.show()

        }
        super.onResume()
    }
}

