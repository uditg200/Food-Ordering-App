package com.udit.foodrunner.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.udit.foodrunner.ConnectionManager.ConnectionManager
import com.udit.foodrunner.R
import org.json.JSONException
import org.json.JSONObject

class registeryourself : AppCompatActivity() {

    lateinit var et_registeryourself_name : EditText
    lateinit var et_registeryourself_mobileno : EditText
    lateinit var et_registeryourself_emailid : EditText
    lateinit var et_registeryourself_deliveryaddress : EditText
    lateinit var et_registeryourself_password : EditText
    lateinit var et_registeryourself_confirmpassword : EditText
    lateinit var btn_registeryourself_register : Button
    lateinit var signupProgressLayout : RelativeLayout
    lateinit var signupProgressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeryourself)

        val sharedPreferencess=getSharedPreferences("RestaurantPreference",
            Context.MODE_PRIVATE)

        et_registeryourself_name = findViewById(R.id.etxt_registeryourself_Name)
        et_registeryourself_mobileno = findViewById(R.id.etxt_registeryourself_mobileno)
        et_registeryourself_emailid = findViewById(R.id.etxt_registeryourself_emailid)
        et_registeryourself_deliveryaddress = findViewById(R.id.etxt_registeryourself_Delivery_address)
        et_registeryourself_password = findViewById(R.id.etxt_registeryourself_password)
        et_registeryourself_confirmpassword = findViewById(R.id.etxt_registeryourself_confirm_password)
        btn_registeryourself_register = findViewById(R.id.btn_registeryourself_Register)

        signupProgressLayout = findViewById(R.id.signupProgressLayout)
        signupProgressBar = findViewById(R.id.signupProgressBar)
        signupProgressLayout.visibility = View.VISIBLE
        signupProgressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            signupProgressLayout.visibility = View.GONE
            signupProgressBar.visibility = View.GONE
        },1000)

        btn_registeryourself_register.setOnClickListener {


            sharedPreferencess.edit().putBoolean("user_logged_in", false).apply()
            if(ConnectionManager().checkConnectivity(this@registeryourself)) {
                if(checkForErrors()) {
                    val queue = Volley.newRequestQueue(this@registeryourself)
                    val url = "http://13.235.250.119/v2/register/fetch_result"

                    val jsonParams = JSONObject()
                    jsonParams.put("name", et_registeryourself_name.text.toString())
                    jsonParams.put("mobile_number", et_registeryourself_mobileno.text.toString())
                    jsonParams.put("password", et_registeryourself_password.text.toString())
                    jsonParams.put("address", et_registeryourself_deliveryaddress.text.toString())
                    jsonParams.put("email", et_registeryourself_emailid.text.toString())

                    val jsonObjectRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                            Response.Listener {
                                try {
                                    val json = it.getJSONObject("data")
                                    val success = json.getBoolean("success")
                                    if (success) {
                                        Toast.makeText(
                                            this@registeryourself,
                                            "Successfully registered",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val data = json.getJSONObject("data")
                                        sharedPreferencess.edit().putBoolean("user_logged_in", true)
                                            .apply()
                                        sharedPreferencess.edit()
                                            .putString("user_id", data.getString("user_id")).apply()
                                        sharedPreferencess.edit()
                                            .putString("name", data.getString("name")).apply()
                                        sharedPreferencess.edit()
                                            .putString("email", data.getString("email")).apply()
                                        sharedPreferencess.edit()
                                            .putString(
                                                "mobile_number",
                                                data.getString("mobile_number")
                                            )
                                            .apply()
                                        sharedPreferencess.edit()
                                            .putString("address", data.getString("address")).apply()


                                        val intent6 = Intent(
                                            this@registeryourself,
                                            RestaurantsActivity::class.java
                                        )
                                        startActivity(intent6)
                                        finish()

                                    } else {
                                        Toast.makeText(
                                            this@registeryourself,
                                            "Some Error Occur",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@registeryourself,
                                        "Some Exception Occur",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@registeryourself,
                                    "Response error. Please try again later",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["Token"] = "50fc8ed6428723"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)
                }
            }
            else
            {
                val dialog = AlertDialog.Builder(this@registeryourself)
                dialog.setTitle("Error")
                dialog.setMessage("No Internet Connection")
                dialog.setPositiveButton("Open Settings"){ _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit"){ _, _ ->

                    ActivityCompat.finishAffinity(this@registeryourself)
                }
                dialog.setCancelable(false)
                dialog.create()
                dialog.show()
            }
        }
    }
    fun checkForErrors():Boolean {
        var errorPassCount = 0
        if (et_registeryourself_name.text.isBlank()) {

            et_registeryourself_name.error = "Field Missing!"
        } else {
            errorPassCount++
        }

        if (et_registeryourself_mobileno.text.isBlank()) {
            et_registeryourself_mobileno.error = "Field Missing!"
        } else {
            errorPassCount++
        }

        if (et_registeryourself_emailid.text.isBlank()) {
            et_registeryourself_emailid.error = "Field Missing!"
        } else {
            errorPassCount++
        }

        if (et_registeryourself_deliveryaddress.text.isBlank()) {
            et_registeryourself_deliveryaddress.error = "Field Missing!"
        } else {
            errorPassCount++
        }

        if (et_registeryourself_confirmpassword.text.isBlank()) {
            et_registeryourself_confirmpassword.error = "Field Missing!"
        } else {
            errorPassCount++
        }

        if (et_registeryourself_password.text.isBlank()) {
            et_registeryourself_password.error = "Field Missing!"
        } else {
            errorPassCount++
        }

        if (et_registeryourself_password.text.isNotBlank() && et_registeryourself_confirmpassword.text.isNotBlank())
        {   if (et_registeryourself_password.text.toString() == et_registeryourself_confirmpassword.text.toString()) {
                errorPassCount++
            } else {
            et_registeryourself_confirmpassword.error = "Password don't match"
            }
        }

        return errorPassCount==7
    }


    override fun onBackPressed() {
        val intent = Intent(this@registeryourself,loginpage::class.java)
        startActivity(intent)
        finish()
    }
    override fun onResume() {
        if(!ConnectionManager().checkConnectivity(this)){
            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ _, _ ->
                ActivityCompat.finishAffinity(this)//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }
        super.onResume()
    }
}