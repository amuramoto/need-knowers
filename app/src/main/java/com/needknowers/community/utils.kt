package com.needknowers.community

fun distInMeters(lat1: Float, lng1: Float, lat2: Float, lng2: Float): Float {
    val earthRadius = 6371000.0 //meters
    val dLat = Math.toRadians((lat2 - lat1).toDouble())
    val dLng = Math.toRadians((lng2 - lng1).toDouble())
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1.toDouble())) * Math.cos(Math.toRadians(lat2.toDouble())) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return (earthRadius * c).toFloat()
}