package com.udit.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurantList WHERE RestaurantId = :restaurantId")
    fun getRestaurantById(restaurantId : String) : RestaurantEntity

    @Query("SELECT * FROM restaurantList")
    fun getAllRestaurant() : List<RestaurantEntity>
}