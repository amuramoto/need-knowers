package com.needknowers.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.internal.it
import kotlinx.android.synthetic.main.fragment_need_knower_main.view.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NeedKnowerMainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NeedKnowerMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NeedKnowerMainFragment : Fragment() {
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    val TAG = NeedKnowerMainFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val places = Places.createClient(context!!)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    Log.d(TAG, location.toString())
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_need_knower_main, container, false)
        view.btn_getDirection.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_needKnowerMainFragment_to_searchLocationFragment))
        view.btn_whereAmI.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_needKnowerMainFragment_to_whereAmIFragment2))

//        // Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(this, null);
//
//        // Construct a PlaceDetectionClient.
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
//
//        // Construct a FusedLocationProviderClient.
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        return view
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}// Required empty public constructor
