package com.udit.foodrunner.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.udit.foodrunner.Adapter.HomeRecyclerAdapter
import com.udit.foodrunner.ConnectionManager.ConnectionManager
import com.udit.foodrunner.Model.Restaurant
import com.udit.foodrunner.R
import kotlinx.android.synthetic.main.activity_restaurant_menu.*
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment(val contextparams : Context) : Fragment() {

    lateinit var homeRecyclerAdapter : HomeRecyclerAdapter
    lateinit var homeLayoutManager : RecyclerView.LayoutManager
    lateinit var recyclerViewHomeFragment : RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var ProgressLayout : RelativeLayout
    lateinit var editTextSearch: EditText
    lateinit var dashboard_fragment_cant_find_restaurant : RelativeLayout
    val restaurantList = arrayListOf<Restaurant>()

    var ratingComparator= Comparator<Restaurant> { rest1, rest2 ->

        if(rest1.RestaurantRating.compareTo(rest2.RestaurantRating,true)==0){
            rest1.restaurantName.compareTo(rest2.restaurantName,true)
        }
        else{
            rest1.RestaurantRating.compareTo(rest2.RestaurantRating,true)
        }

    }

    var costComparator= Comparator<Restaurant> { rest1, rest2 ->

        rest1.RestaurantPricePerPerson.compareTo(rest2.RestaurantPricePerPerson,true)

    }
    var nameComparator= Comparator<Restaurant> { rest1, rest2 ->

        rest1.restaurantName.compareTo(rest2.restaurantName,true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerViewHomeFragment = view.findViewById(R.id.RecyclerViewHomeFragment)
        progressBar = view.findViewById(R.id.ProgressBar)
        ProgressLayout = view.findViewById(R.id.ProgressLayout)
        dashboard_fragment_cant_find_restaurant=view.findViewById(R.id.dashboard_fragment_cant_find_restaurant)
        editTextSearch=view.findViewById(R.id.editTextSearch)
        ProgressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        homeLayoutManager = LinearLayoutManager(activity)




        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if(ConnectionManager().checkConnectivity(activity as Context))
        {
            val JsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,url,null,
                Response.Listener {
                    //println("Response is : $it")
                    try {
                        ProgressLayout.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        val json = it.getJSONObject("data")
                        val success = json.getBoolean("success")

                        if(success)
                        {
                            val data = json.getJSONArray("data")

                            for(i in 0 until data.length())
                            {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val JsonObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantList.add(JsonObject)
                            }
                            val sharedPreferences = contextparams.getSharedPreferences("RestaurantPreference",Context.MODE_PRIVATE)
                            val id : String? = sharedPreferences.getString("user_id","abcd")
                            homeRecyclerAdapter = HomeRecyclerAdapter(activity as Context,restaurantList,id)
                            recyclerViewHomeFragment.layoutManager = homeLayoutManager
                            recyclerViewHomeFragment.adapter = homeRecyclerAdapter

                            recyclerViewHomeFragment.addItemDecoration(
                                DividerItemDecoration(recyclerViewHomeFragment.context,
                                    (homeLayoutManager as LinearLayoutManager).orientation)
                            )
                        }
                        else
                        {
                            if(activity!=null)
                                Toast.makeText(activity as Context,"Unexpected Error Occured",Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e : JSONException){
                        if(activity!=null)
                            Toast.makeText(activity as Context,"Exception Error Occured",Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    if(activity!=null)
                        Toast.makeText(activity as Context,"Server Timeout Error",Toast.LENGTH_SHORT).show()

                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["Token"] = "50fc8ed6428723"
                    return headers
                }
            }
            queue.add(JsonObjectRequest)
        }
        else
        {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){ _, _ ->

                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()
        }

        fun filterFun(strTyped:String){//to filter the recycler view depending on what is typed
            val filteredList= arrayListOf<Restaurant>()

            for (item in restaurantList) {
                if (item.restaurantName.toLowerCase(Locale.ROOT).contains(strTyped.toLowerCase(
                        Locale.ROOT
                    )
                    )
                ) {//to ignore case and if contained add to new list

                    filteredList.add(item)

                }
            }

            if(filteredList.size==0){
                dashboard_fragment_cant_find_restaurant.visibility=View.VISIBLE
            }
            else{
                dashboard_fragment_cant_find_restaurant.visibility=View.INVISIBLE
            }

            homeRecyclerAdapter.filterList(filteredList)

        }

        editTextSearch.addTextChangedListener(object : TextWatcher {//as the user types the search filter is applied
        override fun afterTextChanged(strTyped: Editable?) {
            filterFun(strTyped.toString())
        }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }
        )

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId

        when(id){

            R.id.sortbypriceperperson->{
                            Collections.sort(restaurantList, costComparator)
                            restaurantList.reverse()
                            homeRecyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
            R.id.sortbyname ->{
                            Collections.sort(restaurantList, nameComparator)
                            homeRecyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
            R.id.sortbyrating ->{
                            Collections.sort(restaurantList, ratingComparator)
                            restaurantList.reverse()
                            homeRecyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }

            }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home,menu)
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