package com.udit.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurantList")
data class RestaurantEntity(

    @PrimaryKey val RestaurantId : Int,
    @ColumnInfo(name = "restaurant_name") val RestaurantName : String,
    @ColumnInfo(name = "restaurant_priceperperson") val RestaurantPricePerPerson : String,
    @ColumnInfo(name = "restaurant_rating") val RestaurantRating : String,
    @ColumnInfo(name = "restaurant_image") val RestaurantImage : String
)
