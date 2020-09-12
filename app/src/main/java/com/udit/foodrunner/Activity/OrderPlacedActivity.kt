package com.udit.foodrunner.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.udit.foodrunner.R

class OrderPlacedActivity : AppCompatActivity() {
    lateinit var btnGoBackToDashboard : Button
    lateinit var OrderPlacedProgressBar : ProgressBar
    lateinit var OrderPlacedProgressLayout : RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        btnGoBackToDashboard = findViewById(R.id.btn_GoBackToDashboard)
        OrderPlacedProgressBar = findViewById(R.id.OrderPlacedProgressBar)
        OrderPlacedProgressLayout = findViewById(R.id.OrderPlacedProgressLayout)
        OrderPlacedProgressBar.visibility = View.VISIBLE
        OrderPlacedProgressLayout.visibility = View.VISIBLE
        Handler().postDelayed({
            OrderPlacedProgressBar.visibility = View.GONE
            OrderPlacedProgressLayout.visibility = View.GONE
        },500)
        btnGoBackToDashboard.setOnClickListener {
            val intent = Intent(this@OrderPlacedActivity,RestaurantsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}