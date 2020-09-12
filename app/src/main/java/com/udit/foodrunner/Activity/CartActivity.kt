
package com.udit.foodrunner.Activity

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.udit.foodrunner.Adapter.CartRecyclerAdapter
import com.udit.foodrunner.ConnectionManager.ConnectionManager
import com.udit.foodrunner.Model.Cartitems
import com.udit.foodrunner.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var cartRecyclerView : RecyclerView
    lateinit var cartLayoutManager: RecyclerView.LayoutManager
    lateinit var cartRecyclerAdapter : CartRecyclerAdapter
    lateinit var cartProgressBar : ProgressBar
    lateinit var cartProgressLayout : RelativeLayout
    lateinit var btnPlaceOrder : Button
    lateinit var addeditemlist : ArrayList<String>
    lateinit var restname : String
    lateinit var restId : String
    lateinit var cartToolbar : Toolbar
    lateinit var txtCartRestaurantName : TextView
    var totalPrice = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        val CartMenuItems = arrayListOf<Cartitems>()
        cartRecyclerView = findViewById(R.id.myCartRecyclerView)
        cartLayoutManager = LinearLayoutManager(this@CartActivity)
        cartProgressBar = findViewById(R.id.cartProgressBar)
        txtCartRestaurantName = findViewById(R.id.txtCartRestaurantName)
        cartToolbar = findViewById(R.id.cartToolbar)
        val sharedPreferences = getSharedPreferences("RestaurantPreference",Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("user_id","abcd")
        cartProgressLayout = findViewById(R.id.cartProgressLayout)
        cartProgressLayout.visibility = View.VISIBLE
        cartProgressBar.visibility = View.VISIBLE
        btnPlaceOrder = findViewById(R.id.btn_placeOrder)
        if(intent!=null)
        {
            addeditemlist = intent.getStringArrayListExtra("additemid")
            restname = intent.getStringExtra("restaurant_name")
            restId = intent.getStringExtra("restaurant_Id")
        }
        txtCartRestaurantName.text = restname
        setUpToolBar()
        if(ConnectionManager().checkConnectivity(this@CartActivity))
        {
            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restId"
                val jsonObjectRequest = object:JsonObjectRequest(Request.Method.GET,url,null,
                    Response.Listener {
                        try {
                            cartProgressLayout.visibility = View.GONE
                            cartProgressBar.visibility = View.GONE
                            val json = it.getJSONObject("data")
                            val success = json.getBoolean("success")
                            if (success) {
                                val data = json.getJSONArray("data")
                                CartMenuItems.clear()
                                for (i in 0 until data.length()) {
                                    val addeditemobject = data.getJSONObject(i)
                                    if (addeditemlist.contains(addeditemobject.getString("id"))) {
                                        val cartitems = Cartitems(
                                            addeditemobject.getString("cost_for_one"),
                                            addeditemobject.getString("name")
                                        )
                                        totalPrice += addeditemobject.getString("cost_for_one").toInt()
                                        CartMenuItems.add(cartitems)
                                    }
                                }
                                btnPlaceOrder.text = "Place Order(Rs $totalPrice)"
                                cartRecyclerAdapter =
                                    CartRecyclerAdapter(this@CartActivity, CartMenuItems)
                                cartRecyclerView.adapter = cartRecyclerAdapter
                                cartRecyclerView.layoutManager = cartLayoutManager
                            } else {
                                Toast.makeText(
                                    this@CartActivity,
                                    "Error Occur ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        catch (e : JSONException){
                            Toast.makeText(this@CartActivity,"Error Occured. Please Try Again Later.",Toast.LENGTH_SHORT).show()
                        }

                },Response.ErrorListener {
                    Toast.makeText(this@CartActivity,"Response Timeout Error. Please Try Again Later.",Toast.LENGTH_SHORT).show()
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["Token"] = "50fc8ed6428723"
                        return headers
                    }

                }
            queue.add((jsonObjectRequest))
        }
        else
        {
            val dialog = AlertDialog.Builder(this@CartActivity)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){ _, _ ->

                ActivityCompat.finishAffinity(this@CartActivity)
            }
            dialog.create()
            dialog.show()
        }

        btnPlaceOrder.setOnClickListener {
            if(ConnectionManager().checkConnectivity(this@CartActivity))
            {
                val queue = Volley.newRequestQueue(this@CartActivity)
                val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                val jsonArray = JSONArray()
                for(i in 0 until addeditemlist.size)
                {
                    val orderIdObject = JSONObject()
                    orderIdObject.put("food_item_id",addeditemlist[i])
                    jsonArray.put(orderIdObject)
                }
                val jsonparams = JSONObject()
                jsonparams.put("user_id",userid.toString())
                jsonparams.put("restaurant_id",restId)
                jsonparams.put("total_cost",totalPrice.toString())
                jsonparams.put("food",jsonArray)
                val jsonObjectRequest = object:JsonObjectRequest(Request.Method.POST,url,jsonparams,
                    Response.Listener {
                        try {
                            val json = it.getJSONObject("data")
                            val success = json.getBoolean("success")
                            if(success)
                            {
                                //Toast.makeText(this@CartActivity,"Order Placed",Toast.LENGTH_SHORT).show()
                                createNotification()
                                val intent = Intent(this@CartActivity,OrderPlacedActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                            else
                            {
                                Toast.makeText(this@CartActivity,json.getString("errorMessage").toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                        catch (e : JSONException){
                            Toast.makeText(this@CartActivity,"Error Occur. Please Try Again Later.",Toast.LENGTH_SHORT).show()
                        }

                    },Response.ErrorListener {
                        Toast.makeText(this@CartActivity,"Response Timeout Error. Please Try Again Later.",Toast.LENGTH_SHORT).show()
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["Token"] = "50fc8ed6428723"
                        return headers
                    }

                }
                queue.add((jsonObjectRequest))

            }
            else
            {
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setMessage("No Internet Connection")
                dialog.setPositiveButton("Open Settings"){ _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit"){ _, _ ->

                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.setCancelable(false)
                dialog.create()
                dialog.show()
            }
        }
    }
    fun setUpToolBar()
    {
        setSupportActionBar(cartToolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if(id==android.R.id.home)
        {
           super.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    fun createNotification(){
        val notificationId=1;
        val channelId="personal_notification"



        val notificationBulider= NotificationCompat.Builder(this,channelId)
        notificationBulider.setSmallIcon(R.mipmap.ic_appicon)
        notificationBulider.setContentTitle("Order Placed")
        notificationBulider.setContentText("Your order has been successfully placed!")
        notificationBulider.setStyle(NotificationCompat.BigTextStyle().bigText("Ordered from $restname and amounting to Rs.$totalPrice"))
        notificationBulider.setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManagerCompat= NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId,notificationBulider.build())

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)//less than oreo
        {
            val name ="Order Placed"
            val description="Your order has been successfully placed!"
            val importance= NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel= NotificationChannel(channelId,name,importance)

            notificationChannel.description=description

            val notificationManager=  (getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)

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