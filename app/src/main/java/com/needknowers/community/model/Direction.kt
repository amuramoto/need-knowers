package com.needknowers.community.model

import com.google.gson.annotations.SerializedName

data class AppDirection(@SerializedName("routes") val routes: ArrayList<AppRoute>)

data class AppRoute(@SerializedName("legs") val legs: ArrayList<AppLeg>)

data class AppLeg(@SerializedName("steps") val steps: ArrayList<AppStep>)

data class AppStep(@SerializedName("distance") val distance: AppDistance,
                   @SerializedName("duration") val duration: AppDuration,
                   @SerializedName("end_location") val endLocation: AppLocation,
                   @SerializedName("start_location") val startLocation: AppLocation,
                   @SerializedName("steps") val steps: ArrayList<AppStep>?,
                   @SerializedName("html_instructions") val htmlInstructions: String,
                   @SerializedName("travel_mode") val travelMode: String,
                   @SerializedName("transit_details") val transitDetails: AppTransitDetails?
)


data class AppTransitDetails(@SerializedName("arrival_stop") val arrivalStop: AppStop, @SerializedName("departure_stop") val departureStop: AppStop)
data class AppStop(@SerializedName("location") val location: AppLocation)

data class AppDistance(@SerializedName("text") val text: String)
data class AppDuration(@SerializedName("text") val text: String)
data class AppLocation(@SerializedName("lat") val lat: Double, @SerializedName("lng") val long: Double)
