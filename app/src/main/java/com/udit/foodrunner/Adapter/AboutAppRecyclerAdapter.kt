package com.udit.foodrunner.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udit.foodrunner.Model.AboutApp
import com.udit.foodrunner.Model.Restaurant
import com.udit.foodrunner.R

class AboutAppRecyclerAdapter(val context : Context,val RestaurantAboutApp : ArrayList<AboutApp>) : RecyclerView.Adapter<AboutAppRecyclerAdapter.AboutAppViewHolder>() {

    class AboutAppViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val txtQuestionFaq : TextView = view.findViewById(R.id.txt_questionFaq)
        val txtAnswerFaq : TextView = view.findViewById(R.id.txt_AnswerFaq)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutAppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.aboutapp_recyclerview_singlerow,parent,false)
        return AboutAppViewHolder(view)
    }

    override fun getItemCount(): Int {
        return RestaurantAboutApp.size
    }

    override fun onBindViewHolder(holder: AboutAppViewHolder, position: Int) {
        val aboutapp = RestaurantAboutApp[position]
        holder.txtQuestionFaq.text = aboutapp.question
        holder.txtAnswerFaq.text = aboutapp.Answer
    }

}