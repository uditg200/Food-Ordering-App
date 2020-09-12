package com.udit.foodrunner.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udit.foodrunner.Model.Cartitems
import com.udit.foodrunner.R

class CartRecyclerAdapter(val context : Context, val itemlist : ArrayList<Cartitems>) : RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(view : View) : RecyclerView.ViewHolder(view)
    {
        val cartItemname : TextView = view.findViewById(R.id.cartItemName)
        val cartItemPrice : TextView = view.findViewById(R.id.CartItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_recyclerview_singlerow,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
       return itemlist.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
       val cart = itemlist[position]
        holder.cartItemname.text = cart.itemName
        holder.cartItemPrice.text = "Rs "+cart.itemPrice
    }

}