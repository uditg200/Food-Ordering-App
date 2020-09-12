package com.udit.foodrunner.Adapter

import android.content.Context
import android.content.Intent
import android.content.RestrictionEntry
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udit.foodrunner.Activity.Restaurant_menu
import com.udit.foodrunner.Model.Restaurant
import com.udit.foodrunner.R
import com.udit.foodrunner.database.RestaurantEntity

class FavouriteRecyclerAdapter(val context : Context,var FavouriteList : List<RestaurantEntity>)
    : RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>()
{
    class FavouriteViewHolder(view : View):RecyclerView.ViewHolder(view){
        val txtfavouriteRestaurantName : TextView = view.findViewById(R.id.txt_FavouriteRecyclerRestaurantName)
        val txtfavouriteRestaurantRating : TextView = view.findViewById(R.id.txt_FavouriteRecyclerRestaurantRating)
        val txtfavouriteRestaurantPricePerPerson : TextView = view.findViewById(R.id.txt_FavouriteRecyclerRestaurantPricePerPerson)
        val imgfavouriteRestaurantImage : ImageView = view.findViewById(R.id.img_FavouriteRecyclerRestaurantImage)
        val favouriteRestaurantContent : LinearLayout = view.findViewById(R.id.FavouriteRestaurantContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favourite_recyclerview_singlerow,parent,false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return FavouriteList.size
    }
    fun filterList(filteredList: List<RestaurantEntity>) {//to update the recycler view depending on the search
        FavouriteList = filteredList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val favres = FavouriteList[position]
        holder.txtfavouriteRestaurantName.text = favres.RestaurantName
        holder.txtfavouriteRestaurantRating.text = favres.RestaurantRating
        holder.txtfavouriteRestaurantPricePerPerson.text = "Rs " + favres.RestaurantPricePerPerson + "/person"
        Picasso.get().load(favres.RestaurantImage).error(R.drawable.restaurant_default_image).into(holder.imgfavouriteRestaurantImage)
        holder.favouriteRestaurantContent.setOnClickListener {
            val intent = Intent(context, Restaurant_menu::class.java)
            intent.putExtra("restaurant_id",favres.RestaurantId.toString())
            intent.putExtra("restaurant_name",favres.RestaurantName)
            context.startActivity(intent)
        }
    }
}