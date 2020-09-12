package com.udit.foodrunner.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.udit.foodrunner.Adapter.FavouriteRecyclerAdapter
import com.udit.foodrunner.Adapter.RestaurantItemRecyclerAdapter
import com.udit.foodrunner.ConnectionManager.ConnectionManager
import com.udit.foodrunner.Model.ItemsRestaurant
import com.udit.foodrunner.R
import kotlinx.android.synthetic.main.activity_restaurant_menu.*
import org.json.JSONException
import org.json.JSONObject

class Restaurant_menu : AppCompatActivity() {

    lateinit var itemRecyclerView : RecyclerView
    lateinit var RestaurantItemLayoutManager : RecyclerView.LayoutManager
    lateinit var RestaurantItemRecyclerAdapter : RestaurantItemRecyclerAdapter
    lateinit var RestaurantItemProgressLayout : RelativeLayout
    lateinit var RestaurantItemProgressBar : ProgressBar
    lateinit var btnProceedtoCart : Button
    var id : String? = "100"
    lateinit var menuToolbar : Toolbar
    var restitle : String? = "Restaurant Name"
    val itemlist = arrayListOf<ItemsRestaurant>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)
        itemRecyclerView = findViewById(R.id.RestaurantMenu_RecyclerView)
        RestaurantItemProgressLayout = findViewById(R.id.RestaurantItemProgressLayout)
        RestaurantItemProgressBar = findViewById(R.id.RestaurantItemProgresBar)
        btnProceedtoCart = findViewById(R.id.btnProceedToCart)
        btnProceedToCart.visibility = View.INVISIBLE
        RestaurantItemLayoutManager = LinearLayoutManager(this@Restaurant_menu)
        menuToolbar = findViewById(R.id.menu_toolbar)
        RestaurantItemProgressBar.visibility = View.VISIBLE
        RestaurantItemProgressLayout.visibility = View.VISIBLE

        if(intent!=null)
        {
            id = intent.getStringExtra("restaurant_id")
            restitle = intent.getStringExtra("restaurant_name")
        }
        else
        {
            Toast.makeText(this@Restaurant_menu,"Some error occur",Toast.LENGTH_SHORT).show()
        }
        setUpToolBar()

        val queue = Volley.newRequestQueue(this@Restaurant_menu)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$id"
        if(ConnectionManager().checkConnectivity(this@Restaurant_menu)) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {
                    try{
                        RestaurantItemProgressLayout.visibility = View.GONE
                        RestaurantItemProgressBar.visibility = View.GONE
                        val json = it.getJSONObject("data")
                        val success = json.getBoolean("success")
                        if (success) {
                            val data = json.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                var j = i + 1
                                val itemJsonObject = data.getJSONObject(i)
                                val jsonObject = ItemsRestaurant(
                                    j.toString(),
                                    itemJsonObject.getString("id"),
                                    itemJsonObject.getString("name"),
                                    itemJsonObject.getString("cost_for_one"),
                                    itemJsonObject.getString("restaurant_id"))
                                itemlist.add(jsonObject)
                            }
                            RestaurantItemRecyclerAdapter = RestaurantItemRecyclerAdapter(this@Restaurant_menu, itemlist,btnProceedtoCart,restitle.toString(),id.toString())
                            itemRecyclerView.adapter = RestaurantItemRecyclerAdapter
                            itemRecyclerView.layoutManager = RestaurantItemLayoutManager
                        }
                        else
                        {
                            Toast.makeText(this@Restaurant_menu,"Some Error Occur",Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e : JSONException)
                    {
                        Toast.makeText(this@Restaurant_menu,"Some Unexpected Error Occur",Toast.LENGTH_SHORT).show()
                    }


                }, Response.ErrorListener {
                    Toast.makeText(
                        this@Restaurant_menu,
                        "Response Timeout Error. Please Try Again Later",
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
        else
        {
            val dialog = AlertDialog.Builder(this@Restaurant_menu)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){ _, _ ->

                ActivityCompat.finishAffinity(this@Restaurant_menu)
            }
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()
        }
    }
    fun setUpToolBar()
    {
        setSupportActionBar(menuToolbar)
        supportActionBar?.title = restitle
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
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