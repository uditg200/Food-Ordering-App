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

class forgotpasswordpage : AppCompatActivity() {

    lateinit var etforgotpassword_mobileno : EditText
    lateinit var etforgotpassword_emailid : EditText
    lateinit var btnforgotpassword_next : Button
    lateinit var forgotpasswordProgressLayout : RelativeLayout
    lateinit var forgotpasswordProgressBar : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpasswordpage)
        etforgotpassword_mobileno = findViewById(R.id.etxt_forgotpasswordpage_mobileno)
        etforgotpassword_emailid = findViewById(R.id.etxt_forgotpasswordpage_emailid)
        btnforgotpassword_next = findViewById(R.id.btn_forgotpasswordpage_next)

        forgotpasswordProgressLayout = findViewById(R.id.forgotPasswordProgressLayout)
        forgotpasswordProgressBar = findViewById(R.id.forgotPasswordProgressBar)
        forgotpasswordProgressLayout.visibility = View.VISIBLE
        forgotpasswordProgressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            forgotpasswordProgressLayout.visibility = View.GONE
            forgotpasswordProgressBar.visibility = View.GONE
        },1000)

        btnforgotpassword_next.setOnClickListener {

            if (etforgotpassword_mobileno.text.isBlank())
            {
                etforgotpassword_mobileno.error = "Mobile Number Missing"
            }else{
                if(etforgotpassword_emailid.text.isBlank()){
                    etforgotpassword_emailid.error = "Email Missing"
                }else {

                    if(ConnectionManager().checkConnectivity(this@forgotpasswordpage)) {
                            val queue = Volley.newRequestQueue(this@forgotpasswordpage)
                            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                            val jsonParams = JSONObject()
                            jsonParams.put("mobile_number", etforgotpassword_mobileno.text.toString())
                            jsonParams.put("email", etforgotpassword_emailid.text.toString())


                            val jsonObjectRequest =
                                object : JsonObjectRequest(
                                    Request.Method.POST, url, jsonParams,
                                    Response.Listener {
                                        try {
                                            val json = it.getJSONObject("data")
                                            val success = json.getBoolean("success")
                                            if (success) {
                                                val firstTry = json.getBoolean("first_try")
                                                if(firstTry) {
                                                    Toast.makeText(
                                                        this@forgotpasswordpage, "OTP sent",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    val intent5 = Intent(this@forgotpasswordpage,
                                                        OTPActivity::class.java)
                                                    intent5.putExtra("mobile_number",etforgotpassword_mobileno.text.toString())
                                                    startActivity(intent5)
                                                    finish()
                                                }
                                                else{
                                                    Toast.makeText(
                                                        this@forgotpasswordpage,
                                                        "OTP already sent",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    val intent5 = Intent(this@forgotpasswordpage,
                                                        OTPActivity::class.java)
                                                    intent5.putExtra("mobile_number",etforgotpassword_mobileno.text.toString())
                                                    startActivity(intent5)
                                                    finish()
                                                }

                                            } else {
                                                Toast.makeText(
                                                    this@forgotpasswordpage,
                                                    json.getString("errorMessage").toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } catch (e: JSONException) {
                                            Toast.makeText(
                                                this@forgotpasswordpage,
                                                "Some Exception Occur",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    Response.ErrorListener {
                                        Toast.makeText(
                                            this@forgotpasswordpage,
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
                        val dialog = AlertDialog.Builder(this@forgotpasswordpage)
                        dialog.setTitle("Error")
                        dialog.setMessage("No Internet Connection")
                        dialog.setPositiveButton("Open Settings"){ _, _ ->
                            val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit"){ _, _ ->

                            ActivityCompat.finishAffinity(this@forgotpasswordpage)
                        }
                        dialog.setCancelable(false)
                        dialog.create()
                        dialog.show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
            val intent = Intent(this@forgotpasswordpage,loginpage::class.java)
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