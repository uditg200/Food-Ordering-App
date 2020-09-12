package com.udit.foodrunner.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.*
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

class loginpage : AppCompatActivity() {

    lateinit var etmobileno : EditText
    lateinit var etpassword : EditText
    lateinit var txtsignup : TextView
    lateinit var txtforgotpassword : TextView
    lateinit var btnlogin : Button
    lateinit var loginpageProgressLayout : RelativeLayout
    lateinit var loginpageProgressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)
        val sharedPreferencess = getSharedPreferences("RestaurantPreference",
            Context.MODE_PRIVATE)


        if(sharedPreferencess.getBoolean("user_logged_in",false)){
            val intent= Intent(this@loginpage , RestaurantsActivity::class.java)
            startActivity(intent)
            finish();
        }

        etmobileno = findViewById(R.id.etxt_mobileno)
        etpassword = findViewById(R.id.etxt_password)
        txtforgotpassword = findViewById(R.id.txt_forgetpassword)
        txtsignup =  findViewById(R.id.txt_signup)
        btnlogin = findViewById(R.id.btn_login)

        loginpageProgressLayout = findViewById(R.id.loginpageProgressLayout)
        loginpageProgressBar = findViewById(R.id.loginpageProgressBar)
        loginpageProgressLayout.visibility = View.VISIBLE
        loginpageProgressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            loginpageProgressLayout.visibility = View.GONE
            loginpageProgressBar.visibility = View.GONE
        },1000)


        val span = SpannableString("Forgot Password?")
        val clickablesapn = object : ClickableSpan(){

            override fun onClick(widget: View) {
                val intent3 = Intent(this@loginpage,
                    forgotpasswordpage::class.java)
                startActivity(intent3)
                finish()
            }
        }
        span.setSpan(clickablesapn,0,16,0)
        span.setSpan(UnderlineSpan(),0,16,0)
        span.setSpan(ForegroundColorSpan(Color.WHITE),0,16,0)
        txtforgotpassword.movementMethod = LinkMovementMethod.getInstance()
        txtforgotpassword.text = span


        val span1 = SpannableString("Don't have an account? Sign up now")
        val clickablesapn1 = object : ClickableSpan(){

            override fun onClick(widget: View) {
                val intent2 = Intent(this@loginpage,
                    registeryourself::class.java)
                startActivity(intent2)
                finish()

            }
        }
        span1.setSpan(clickablesapn1,23,34,0)
        span1.setSpan(UnderlineSpan(),23,34,0)
        span1.setSpan(ForegroundColorSpan(Color.WHITE),23,34,0)
        txtsignup.movementMethod = LinkMovementMethod.getInstance()
        txtsignup.text = span1



        btnlogin.setOnClickListener{

            sharedPreferencess.edit().putBoolean("user_logged_in", false).apply()
            if(ConnectionManager().checkConnectivity(this@loginpage)) {
                    val queue = Volley.newRequestQueue(this@loginpage)
                    val url = "http://13.235.250.119/v2/login/fetch_result"

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", etmobileno.text.toString())
                    jsonParams.put("password", etpassword.text.toString())

                    val jsonObjectRequest =
                        object : JsonObjectRequest(
                            Request.Method.POST, url, jsonParams,
                            Response.Listener {
                                try {
                                    val json = it.getJSONObject("data")
                                    val success = json.getBoolean("success")
                                    if (success) {
                                        Toast.makeText(
                                            this@loginpage,
                                            "Hi ${json.getJSONObject("data").getString("name")}",
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
                                            this@loginpage,
                                            RestaurantsActivity::class.java
                                        )
                                        startActivity(intent6)
                                        finish()

                                    } else {
                                        Toast.makeText(
                                            this@loginpage,
                                            "Some Error Occur",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@loginpage,
                                        "Some Exception Occur",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@loginpage,
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
                val dialog = AlertDialog.Builder(this@loginpage)
                dialog.setTitle("Error")
                dialog.setMessage("No Internet Connection")
                dialog.setPositiveButton("Open Settings"){ _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit"){ _, _ ->

                    ActivityCompat.finishAffinity(this@loginpage)
                }
                dialog.setCancelable(false)
                dialog.create()
                dialog.show()
            }
        }
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