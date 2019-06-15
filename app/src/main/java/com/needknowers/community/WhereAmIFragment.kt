package com.needknowers.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_where_am_i.view.*
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import com.google.android.gms.tasks.CancellationToken
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*
import java.util.Arrays.asList
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.gms.tasks.Task





class WhereAmIFragment : Fragment(), OnStreetViewPanoramaReadyCallback {

    private lateinit var places: PlacesClient
    private var streetViewPanoramaView: StreetViewPanoramaView? = null
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    var longitude: Double? = null
    var latitude: Double? = null
    val TAG = NeedKnowerMainFragment::class.java.simpleName

    // Use fields to define the data types to return.
    var placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS)

    // Use the builder to create a FindCurrentPlaceRequest.
    var request = FindCurrentPlaceRequest.builder(placeFields).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        places = Places.createClient(context!!)
        requestLastLocation()
        val placeResponse = places.findCurrentPlace(request)
        placeResponse.addOnCompleteListener {
            Log.d(TAG, it.toString())
        }

    }

    fun requestLastLocation() {
        mFusedLocationProviderClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, it.result.toString())
                longitude = it.result?.longitude
                latitude = it.result?.latitude
                streetViewPanoramaView?.getStreetViewPanoramaAsync(this)
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_where_am_i, container, false)
        streetViewPanoramaView = view.findViewById(R.id.streetviewpanorama)
        streetViewPanoramaView?.onCreate(savedInstanceState)
        streetViewPanoramaView?.getStreetViewPanoramaAsync(this)

        return view
    }

    override fun onPause() {
        super.onPause()
        streetViewPanoramaView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        streetViewPanoramaView?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        streetViewPanoramaView?.onDestroy()
    }

    override fun onStreetViewPanoramaReady(panorama: StreetViewPanorama?) {
        Log.d("SV", "READY $longitude $latitude")
        if (latitude == null || longitude == null || panorama == null){
            requestLastLocation()
            return
        }
        panorama.isUserNavigationEnabled = false
        panorama.isStreetNamesEnabled = true
        panorama.setPosition(LatLng(latitude!!, longitude!!))
    }


}
