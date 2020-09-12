package com.udit.foodrunner.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.udit.foodrunner.ConnectionManager.ConnectionManager
import com.udit.foodrunner.Fragment.*
import com.udit.foodrunner.R

class RestaurantsActivity : AppCompatActivity() {

    lateinit var txtnewactivity_mobile : TextView
    lateinit var txtnewactivity_emailid : TextView
    lateinit var txtnewactivity_name: TextView
    lateinit var txtnewactivity_deliveryaddress : TextView

    lateinit var drawerLayout: DrawerLayout
    lateinit var frameLayout: FrameLayout
    lateinit var toolbar: Toolbar
    lateinit var navigationView: NavigationView
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var txtusername : TextView
    lateinit var txtuserMobileNo : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
//        txtnewactivity_mobile = findViewById(R.id.txt_newactivity_mobileno)
//        txtnewactivity_emailid = findViewById(R.id.txt_newactivity_emailid)
//        txtnewactivity_name = findViewById(R.id.txt_newactivity_name)
//        txtnewactivity_deliveryaddress = findViewById(R.id.txt_newactivity_deliveryaddress)
//        if(intent!=null)
//        {
//            txtnewactivity_mobile.text = intent.getStringExtra("MobileNo")
//            txtnewactivity_emailid.text = intent.getStringExtra("EmailId")
//            txtnewactivity_name.text = intent.getStringExtra("Name")
//            txtnewactivity_deliveryaddress.text = intent.getStringExtra("DeliveryAddress")
//        }
        val sharedPreferences = getSharedPreferences("RestaurantPreference",
            Context.MODE_PRIVATE)

        frameLayout = findViewById(R.id.framelayout)
        coordinatorLayout = findViewById(R.id.coordinatorlayout)
        navigationView = findViewById(R.id.NavigationView)
        drawerLayout = findViewById(R.id.drawerlayout)
        toolbar = findViewById(R.id.toolbar)
        val headerView=navigationView.getHeaderView(0)
        txtusername = headerView.findViewById(R.id.txtPersonName)
        txtuserMobileNo = headerView.findViewById(R.id.txt_UserMobileNo)
        if(intent!=null)
        {
            txtusername.text = sharedPreferences.getString("name","User Name")
            txtuserMobileNo.text = "+91"+sharedPreferences.getString("mobile_number","+1111111111")
        }
        else
        {
            Toast.makeText(this@RestaurantsActivity,"Some Error Occur",Toast.LENGTH_SHORT).show()
        }
        setUpToolbar()
//        txtusername.text = sharedPreferences.getString("name","User Name")
//        txtuserMobileNo.text = sharedPreferences.getString("mobile_number","+1111111111")
        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@RestaurantsActivity,
            drawerLayout,
            R.string.opne_drawer,
            R.string.close_drawer

        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            when(it.itemId)
            {
                R.id.profile ->{
                    openProfile()
                }
                R.id.FavouriteRestaurant ->{
                    openFavourites()
                }
                R.id.logout ->{
                    val dialog = AlertDialog.Builder(this@RestaurantsActivity)
                    dialog.setTitle("Log Out Alert")
                    dialog.setMessage("Are you sure ?")
                    dialog.setNegativeButton("Cancel"){ _, _ ->

                    }
                    dialog.setPositiveButton("Log Out"){ _, _ ->
                        val intent = Intent(this@RestaurantsActivity, loginpage::class.java)
                        sharedPreferences.edit().putBoolean("user_logged_in",false).apply()
                        startActivity(intent)
                        finish()
                    }
                    dialog.create()
                    dialog.show()
                }
                R.id.orderHistory ->{
                    openOrderHistory()
                }
                R.id.FAQ ->{
                    openFAQ()
                }
                R.id.Home ->{
                    openHome()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }
    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Restaurants"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if(id == android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    fun openHome()
    {
        val fragment = HomeFragment(this@RestaurantsActivity)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.framelayout,fragment)
        transaction.commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.Home)
    }
    fun openProfile()
    {
        val fragment = ProfileFragment(this@RestaurantsActivity)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.framelayout,fragment)
        transaction.commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "Profile"
    }
    fun openFavourites()
    {
        val fragment = FavouritesFragment(this@RestaurantsActivity)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.framelayout,fragment)
        transaction.commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "Favourite Restaurants"
    }
    fun openFAQ()
    {
        val fragment = FaQFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.framelayout,fragment)
        transaction.commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "FAQ"
    }
    fun openOrderHistory()
    {
        val fragment = OrderHistoryFragment(this@RestaurantsActivity)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.framelayout,fragment)
        transaction.commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "Order History"
    }

    override fun onBackPressed() {

        val frag = supportFragmentManager.findFragmentById(R.id.framelayout)
        when(frag)
        {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
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