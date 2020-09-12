package com.udit.foodrunner.Activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.udit.foodrunner.ConnectionManager.ConnectionManager
import com.udit.foodrunner.R
import org.json.JSONException
import org.json.JSONObject

class OTPActivity : AppCompatActivity() {
    lateinit var etEnterOtp : EditText
    lateinit var etOtpNewPassword : EditText
    lateinit var etOtpConfirmPassword : EditText
    lateinit var btnOtpSubmit : Button
    lateinit var OTPprogressLayout : RelativeLayout
    lateinit var OTPprogressBar : ProgressBar
    var mobNo : String? = "1111111111"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)
        etEnterOtp = findViewById(R.id.et_EnterOtp)
        etOtpNewPassword = findViewById(R.id.et_otpNewPassword)
        etOtpConfirmPassword = findViewById(R.id.et_otpConfirmPassword)
        OTPprogressLayout = findViewById(R.id.OTPprogresslayout)
        OTPprogressBar = findViewById(R.id.OTPprogressBar)
        OTPprogressBar.visibility = View.VISIBLE
        OTPprogressLayout.visibility = View.VISIBLE
        btnOtpSubmit = findViewById(R.id.btn_otpSubmit)
        if(intent!=null)
        {
            mobNo = intent.getStringExtra("mobile_number")
        }
        Handler().postDelayed({
            OTPprogressLayout.visibility = View.GONE
            OTPprogressBar.visibility = View.GONE
        },1000)

        btnOtpSubmit.setOnClickListener {
            var field = 0
            if(etEnterOtp.text.isBlank())
            {
                etEnterOtp.error = "OTP Missing"
            }
            else
            {
                field++
            }
            if(etOtpNewPassword.text.isBlank())
            {
                etOtpNewPassword.error = "New Password Missing"
            }
            else
            {
                field++
            }
            if(etOtpConfirmPassword.text.isBlank())
            {
                etOtpConfirmPassword.error = "Confirm Password Missing"
            }
            else
            {
                field++
            }
            if(field==3)
            {
                if(etOtpNewPassword.text.toString() == etOtpConfirmPassword.text.toString())
                {
                    if(ConnectionManager().checkConnectivity(this@OTPActivity)) {
                        val queue = Volley.newRequestQueue(this@OTPActivity)
                        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                        val jsonParams = JSONObject()
                        jsonParams.put("mobile_number", mobNo.toString())
                        jsonParams.put("password", etOtpNewPassword.text.toString())
                        jsonParams.put("otp",etEnterOtp.text.toString())




                        val jsonObjectRequest =
                            object : JsonObjectRequest(
                                Request.Method.POST, url, jsonParams,
                                Response.Listener {
                                    try {
                                        val json = it.getJSONObject("data")
                                        val success = json.getBoolean("success")
                                        if (success) {
                                                Toast.makeText(
                                                    this@OTPActivity,
                                                    json.getString("successMessage").toString(),
                                                    Toast.LENGTH_SHORT).show()
                                            Toast.makeText(
                                                this@OTPActivity,
                                                "Press Back Button To Go Back To The Login Page",
                                                Toast.LENGTH_SHORT).show()

                                        } else {
                                            Toast.makeText(
                                                this@OTPActivity,
                                                json.getString("errorMessage").toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: JSONException) {
                                        Toast.makeText(
                                            this@OTPActivity,
                                            "Some Exception Occur",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                Response.ErrorListener {
                                    Toast.makeText(
                                        this@OTPActivity,
                                        "Response error. Please try again later",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json; charset=UTF-8"
                                    headers["Token"] = "50fc8ed6428723"
                                    return headers
                                }
                            }
                        queue.add(jsonObjectRequest)
                    }
                    else
                    {
                        val dialog = AlertDialog.Builder(this@OTPActivity)
                        dialog.setTitle("Error")
                        dialog.setMessage("No Internet Connection")
                        dialog.setPositiveButton("Open Settings"){ _, _ ->
                            val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit"){ _, _ ->

                            ActivityCompat.finishAffinity(this@OTPActivity)
                        }
                        dialog.setCancelable(false)
                        dialog.create()
                        dialog.show()
                    }
                }
                else
                {
                    Toast.makeText(this@OTPActivity,"Password Don't Match",Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this@OTPActivity,"Please fill all the above fields",Toast.LENGTH_SHORT).show()
            }
        }
    }
        override fun onBackPressed() {
            val intent = Intent(this@OTPActivity,loginpage::class.java)
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