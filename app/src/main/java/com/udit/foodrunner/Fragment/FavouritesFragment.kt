package com.udit.foodrunner.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.RestrictionEntry
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.udit.foodrunner.Adapter.FavouriteRecyclerAdapter
import com.udit.foodrunner.Adapter.HomeRecyclerAdapter
import com.udit.foodrunner.ConnectionManager.ConnectionManager
import com.udit.foodrunner.Model.Restaurant
import com.udit.foodrunner.R
import com.udit.foodrunner.database.RestaurantDatabase
import com.udit.foodrunner.database.RestaurantEntity
import org.json.JSONException
import org.json.JSONObject
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FavouritesFragment(val contextparams : Context) : Fragment() {

    lateinit var favouriteRecyclerView : RecyclerView
    lateinit var favouriteLayoutManager: RecyclerView.LayoutManager
    lateinit var favouriteRecyclerAdapter : FavouriteRecyclerAdapter
    lateinit var favouriteProgressLayout : RelativeLayout
    lateinit var favouriteProgressBar : ProgressBar
    lateinit var editTextSearch: EditText
    lateinit var dashboard_fragment_cant_find_restaurant : RelativeLayout
    val sharedPreferences = contextparams.getSharedPreferences("RestaurantPreference",Context.MODE_PRIVATE)
    val id  = sharedPreferences.getString("user_id","abcd")
    var favRestaurantList = listOf<RestaurantEntity>()
    //var list = arrayListOf<RestaurantEntity>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        favouriteRecyclerView = view.findViewById(R.id.FavouriteRecyclerView)
        favouriteLayoutManager = LinearLayoutManager(activity as Context)
        favouriteProgressLayout = view.findViewById(R.id.FavouriteProgressLayout)
        dashboard_fragment_cant_find_restaurant=view.findViewById(R.id.dashboard_fragment_cant_find_restaurant)
        editTextSearch=view.findViewById(R.id.editTextSearch)
        favouriteProgressBar = view.findViewById(R.id.FavouriteProgressBar)
        favouriteProgressLayout.visibility = View.VISIBLE
        favouriteProgressBar.visibility = View.VISIBLE
        favRestaurantList = RetrieveAsynTask(activity as Context,id).execute().get()

        if(activity != null)
        {
            favouriteProgressLayout.visibility = View.GONE
            favouriteProgressBar.visibility = View.GONE
            favouriteRecyclerAdapter = FavouriteRecyclerAdapter(activity as Context,favRestaurantList)
            favouriteRecyclerView.layoutManager = favouriteLayoutManager
            favouriteRecyclerView.adapter = favouriteRecyclerAdapter
        }

        fun filterFun(strTyped:String){//to filter the recycler view depending on what is typed
            val filteredList= arrayListOf<RestaurantEntity>()

            for (item in favRestaurantList) {
                if (item.RestaurantName.toLowerCase(Locale.ROOT).contains(strTyped.toLowerCase(
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

            favouriteRecyclerAdapter.filterList(filteredList)

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

    class RetrieveAsynTask(val context : Context, val id : String?) : AsyncTask<Void,Void,List<RestaurantEntity>>()
    {
        val db = id?.let {
            Room.databaseBuilder(context,RestaurantDatabase::class.java,
                it
            ).build()
        }
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity>? {
            val result = db?.restaurantDao()?.getAllRestaurant()
            return result
        }
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