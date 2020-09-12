package com.udit.foodrunner.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.udit.foodrunner.ConnectionManager.ConnectionManager
import com.udit.foodrunner.Model.Cartitems
import com.udit.foodrunner.Model.OrderHistoryRestaurant
import com.udit.foodrunner.R
import org.json.JSONException

class OrderHistoryAdapter(val context: Context, val orderedRestaurantList:ArrayList<OrderHistoryRestaurant>): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolderOrderHistoryRestaurant>() {

    class ViewHolderOrderHistoryRestaurant(view: View) : RecyclerView.ViewHolder(view) {
        val textViewResturantName: TextView = view.findViewById(R.id.textViewResturantName)
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val recyclerViewItemsOrdered: RecyclerView = view.findViewById(R.id.recyclerViewItemsOrdered)
        val txtHistoryTotalCost : TextView = view.findViewById(R.id.txt_historyTotalCost)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderOrderHistoryRestaurant {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_recycler_view_single_row, parent, false)

        return ViewHolderOrderHistoryRestaurant(view)
    }

    override fun getItemCount(): Int {
        return orderedRestaurantList.size
    }

    override fun onBindViewHolder(holder: ViewHolderOrderHistoryRestaurant, position: Int) {
        val restaurantObject = orderedRestaurantList[position]


        holder.textViewResturantName.text = restaurantObject.restaurantName
        var formatDate=restaurantObject.orderPlacedAt
        formatDate=formatDate.replace("-","/")//21-02-20 to 21/02/20
        formatDate=formatDate.substring(0,6)+"20"+formatDate.substring(6,8)//21/02/20 to 21/02/2020
        holder.textViewDate.text =  formatDate


        var layoutManager = LinearLayoutManager(context)
        var orderedItemAdapter: CartRecyclerAdapter// the same cart adapter can be used to fill the ordered items for each restaurant

        if (ConnectionManager().checkConnectivity(context)) {

            try {

                val orderItemsPerRestaurant=ArrayList<Cartitems>()

                val sharedPreferencess=context.getSharedPreferences("RestaurantPreference",
                    Context.MODE_PRIVATE)

                val user_id=sharedPreferencess.getString("user_id","0")

                val queue = Volley.newRequestQueue(context)

                val url ="http://13.235.250.119/v2/orders/fetch_result/" + user_id.toString()

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            val fetechedRestaurantJsonObject = data.getJSONObject(position)//restaurant at index of position

                            orderItemsPerRestaurant.clear()
                            holder.txtHistoryTotalCost.text = "Rs "+fetechedRestaurantJsonObject.getString("total_cost")

                            val foodOrderedJsonArray=fetechedRestaurantJsonObject.getJSONArray("food_items")

                            for(j in 0 until foodOrderedJsonArray.length())//loop through all the items
                            {
                                val eachFoodItem = foodOrderedJsonArray.getJSONObject(j)//each food item
                                val itemObject = Cartitems(
                                    eachFoodItem.getString("cost"),
                                    eachFoodItem.getString("name")
                                )

                                orderItemsPerRestaurant.add(itemObject)

                            }

                            orderedItemAdapter = CartRecyclerAdapter(
                                context,//pass the relativelayout which has the button to enable it later
                                orderItemsPerRestaurant
                            )//set the adapter with the data

                            holder.recyclerViewItemsOrdered.adapter =
                                orderedItemAdapter//bind the  recyclerView to the adapter

                            holder.recyclerViewItemsOrdered.layoutManager = layoutManager //bind the  recyclerView to the layoutManager


                        }
                    },
                    Response.ErrorListener {
                        println("Error12menu is " + it)

                        Toast.makeText(
                            context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"] = "application/json"
                        headers["token"] = "50fc8ed6428723"

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    context,
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }
}