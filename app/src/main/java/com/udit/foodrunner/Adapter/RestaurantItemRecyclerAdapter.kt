package com.udit.foodrunner.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.udit.foodrunner.Activity.CartActivity
import com.udit.foodrunner.Model.ItemsRestaurant
import com.udit.foodrunner.R

class RestaurantItemRecyclerAdapter(val context : Context, val ItemList : ArrayList<ItemsRestaurant>, val btnProceedtoCart : Button, val restName : String, val restId : String) : RecyclerView.Adapter<RestaurantItemRecyclerAdapter.RestaurantItemViewHolder>() {

    var selecteditem : Int = 0
    var additemid = ArrayList<String>()
    class RestaurantItemViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val txtItemNo : TextView = view.findViewById(R.id.txt_itemNo)
        val txtItemName : TextView = view.findViewById(R.id.txt_itemname)
        val txtItemPrice : TextView = view.findViewById(R.id.txt_itemprice)
        val btnAddToCart : Button = view.findViewById(R.id.btn_itemaddtoCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurantmenu_recyclerview_singlerow,parent,false)
        return RestaurantItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ItemList.size
    }

    override fun onBindViewHolder(holder: RestaurantItemViewHolder, position: Int) {
        val item = ItemList[position]
        holder.txtItemNo.text = item.ItemNo
        holder.txtItemName.text = item.ItemName
        holder.txtItemPrice.text ="Rs."+ item.ItemPrice
        holder.btnAddToCart.setOnClickListener {
            if(holder.btnAddToCart.text.toString().equals("Add"))
            {
                //Toast.makeText(context,"${item.Itemid} added to cart",Toast.LENGTH_SHORT).show()
                holder.btnAddToCart.text = "Remove"
                holder.btnAddToCart.setBackgroundResource(R.drawable.remove_roundbutton)
                selecteditem++
                additemid.add(item.Itemid)
            }
            else
            {
                //Toast.makeText(context,"${item.Itemid} removed from cart",Toast.LENGTH_SHORT).show()
                holder.btnAddToCart.text = "Add"
                holder.btnAddToCart.setBackgroundResource(R.drawable.roundedbutton)
                selecteditem--
                additemid.remove(item.Itemid)
            }
            if(selecteditem>0)
            {
                btnProceedtoCart.visibility = View.VISIBLE
                btnProceedtoCart.setOnClickListener{
                    val intent = Intent(context,CartActivity::class.java)
                    intent.putStringArrayListExtra("additemid",additemid)
                    intent.putExtra("restaurant_name",restName)
                    intent.putExtra("restaurant_Id",restId)
                    context.startActivity(intent)
                }
            }
            else
            {
                btnProceedtoCart.visibility = View.INVISIBLE
            }
        }

    }
}