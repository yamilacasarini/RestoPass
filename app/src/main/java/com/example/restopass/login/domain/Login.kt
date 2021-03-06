package com.example.restopass.login.domain

import com.example.restopass.domain.Restaurant

data class Login(
    val email: String,
    val password: String
)

data class GoogleLogin(
    val googleToken: String
)

data class LoginResponse(
    val user: User,
    val xAuthToken: String,
    val xRefreshToken: String,
    val creation: Boolean = false //Jackson ignora el prefijo "is" si es un Boolean.
)

data class LoginRestaurantResponse(
    val user: RestaurantUser,
    val xAuthToken: String,
    val xRefreshToken: String
)

data class RestaurantUser(
    val email : String,
    val restaurant : Restaurant
)

data class User(
    val email: String,
    val name: String,
    val lastName: String,
    val actualMembership: Int?,
    var visits : Int,
    var favoriteRestaurants: MutableList<String>?,
    val b2BUserEmployee : B2BUserEmployee?,
    val membershipFinalizeDate : String?,
    val membershipEnrolledDate : String?,
    var isSubscribedToTopic: Boolean = true
) {
   val firebaseTopic = email.replace("@", "")
}

data class B2BUserEmployee (
    val companyName : String,
    val percentageDiscountPerMembership : List<Float>
)
