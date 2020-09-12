package com.udit.foodrunner.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udit.foodrunner.Adapter.AboutAppRecyclerAdapter
import com.udit.foodrunner.ConnectionManager.ConnectionManager
import com.udit.foodrunner.Model.AboutApp
import com.udit.foodrunner.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FaQFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var aboutAppRecyclerView : RecyclerView
    lateinit var aboutAppLayoutManager: RecyclerView.LayoutManager
    lateinit var aboutAppAdapter : AboutAppRecyclerAdapter

    val question1 = "Q1) Can I place my order on call?"
    val question2 = "Q2) How do I edit my order after placing it?"
    val question3 = "Q3) How do I cancel my order?"
    val question4 = "Q4) When will my order delivered ?"
    val question5 = "Q5) Why my location is not serviceable ?"
    val question6 = "Q6) Will I get my entire order in single delivery?"
    val question7 = "Q7) What if I have an problem with my delivery?"
    val question8 = "Q8) What payment modes are accepted?"

    val answer1 = "Ans: Sorry, we do not accept orders on call. However You can email us at uditgupta74@yahoo.in."
    val answer2 = "Ans: Please email to uditgupta74@yahoo.in mentioning your order id and we will help you."
    val answer3 = "Ans: Please email to uditgupta74@yahoo.in mentioning your order id and we will help you."
    val answer4 = "Ans: It depends on which area you live and the distance b/w your address and the restaurant from which you ordered." +
                    "Still we always try to deliver your order as soon as possible."
    val answer5 = "Ans: Currently we are giving service in few cities. We will be expanding soon."
    val answer6 = "Ans: This depends on the items you have ordered since sometimes restaurant does not have all the items that you have ordered."
    val answer7 = "Ans: Please email to uditgupta74@yahoo.in mentioning your order id and we will help you."
    val answer8 = "Ans: You can pay by cash. We will soon be introducing more payment methods."

    val faq = arrayListOf<AboutApp>(
        AboutApp(question1,answer1),
        AboutApp(question2,answer2),
        AboutApp(question3,answer3),
        AboutApp(question4,answer4),
        AboutApp(question5,answer5),
        AboutApp(question6,answer6),
        AboutApp(question7,answer7),
        AboutApp(question8,answer8)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fa_q, container, false)

        aboutAppRecyclerView = view.findViewById(R.id.aboutapp_recyclerview)
        aboutAppLayoutManager = LinearLayoutManager(activity as Context)
        aboutAppAdapter = AboutAppRecyclerAdapter(activity as Context,faq)

        aboutAppRecyclerView.layoutManager = aboutAppLayoutManager
        aboutAppRecyclerView.adapter = aboutAppAdapter
        aboutAppRecyclerView.addItemDecoration(
            DividerItemDecoration(aboutAppRecyclerView.context,
                (aboutAppLayoutManager as LinearLayoutManager).orientation))

        return view
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