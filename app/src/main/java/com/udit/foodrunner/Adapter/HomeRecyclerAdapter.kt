package com.udit.foodrunner.Adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import com.squareup.picasso.Picasso
import com.udit.foodrunner.Activity.Restaurant_menu
import com.udit.foodrunner.Fragment.FaQFragment
import com.udit.foodrunner.Model.Restaurant
import com.udit.foodrunner.R
import com.udit.foodrunner.database.RestaurantDatabase
import com.udit.foodrunner.database.RestaurantEntity

class HomeRecyclerAdapter(val context : Context,var RestaurantList : ArrayList<Restaurant>,val id : String?) : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view : View) : RecyclerView.ViewHolder(view)
    {
        val txtRestaurantName : TextView = view.findViewById(R.id.txt_RecyclerRestaurantName)
        val txtRestaurantPricePerPerson : TextView = view.findViewById(R.id.txt_RecyclerRestaurantPricePerPerson)
        val txtRestaurantRating : TextView = view.findViewById(R.id.txt_RecyclerRestaurantRating)
        val imgRestaurantImage : ImageView = view.findViewById(R.id.img_RecyclerRestaurantImage)
        val RestaurantContent : LinearLayout = view.findViewById(R.id.RestaurantContent)
        val imgFavouriteRestaurantIcon : ImageView = view.findViewById(R.id.imgFavouriteResttaurantIcon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_recycle_view_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {

        return RestaurantList.size
    }
    fun filterList(filteredList: ArrayList<Restaurant>) {//to update the recycler view depending on the search
        RestaurantList = filteredList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = RestaurantList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantPricePerPerson.text = "Rs ${restaurant.RestaurantPricePerPerson}/person"
        holder.txtRestaurantRating.text = restaurant.RestaurantRating
        Picasso.get().load(restaurant.RestaurantImage).error(R.drawable.restaurant_default_image).into(holder.imgRestaurantImage)

        //-----------------------------------------------AsyncTask Operation------------------------------------------------------
        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId.toInt(),
            restaurant.restaurantName,
            restaurant.RestaurantPricePerPerson,
            restaurant.RestaurantRating,
            restaurant.RestaurantImage
        )

        val favRes = DBAsynTask(context,restaurantEntity,1,id).execute().get()
        if(favRes)
        {
            holder.imgFavouriteRestaurantIcon.setImageResource(R.drawable.ic_favouriterestaurant)
        }
        else
        {
            holder.imgFavouriteRestaurantIcon.setImageResource(R.drawable.ic_notfavouriterestaurant)
        }

        holder.imgFavouriteRestaurantIcon.setOnClickListener {

            if(!DBAsynTask(context,restaurantEntity,1,id).execute().get()) {
                val success = DBAsynTask(context, restaurantEntity, 2,id).execute().get()
                if (success) {
                    holder.imgFavouriteRestaurantIcon.setImageResource(R.drawable.ic_favouriterestaurant)
                    Toast.makeText(context,"${restaurant.restaurantName} added to favourite",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(context,"Unexpected Error Occur",Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                val success = DBAsynTask(context, restaurantEntity, 3,id).execute().get()
                if (success) {
                    holder.imgFavouriteRestaurantIcon.setImageResource(R.drawable.ic_notfavouriterestaurant)
                    Toast.makeText(context,"${restaurant.restaurantName} removed from favourites",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(context,"Unexpected Error Occur",Toast.LENGTH_SHORT).show()
                }
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------

        holder.RestaurantContent.setOnClickListener {
            val intent = Intent(context,Restaurant_menu::class.java)
            intent.putExtra("restaurant_id",restaurant.restaurantId)
            intent.putExtra("restaurant_name",restaurant.restaurantName)
            context.startActivity(intent)
        }

    }
    //--------------------------------------------------AsyncTask Class------------------------------------------------------------
    class DBAsynTask(val context: Context,val restaurantEntity : RestaurantEntity, val mode : Int, val id : String?) : AsyncTask<Void,Void,Boolean>()
    {
        val db = id?.let {
            Room.databaseBuilder(context,RestaurantDatabase :: class.java,
                it
            ).build()
        }
        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){

                1 ->{
                    // find restaurant by id
                    val restaurant : RestaurantEntity? = db?.restaurantDao()?.getRestaurantById(restaurantEntity.RestaurantId.toString())
                    db?.close()
                    return restaurant != null
                }
                2 ->{
                    // insert restaurant details in database
                    db?.restaurantDao()?.insertRestaurant(restaurantEntity)
                    db?.close()
                    return true
                }
                3 ->{
                    // delete restaurant details from database
                    db?.restaurantDao()?.deleteRestaurant(restaurantEntity)
                    db?.close()
                    return true
                }
            }

            return false
        }

    }

}