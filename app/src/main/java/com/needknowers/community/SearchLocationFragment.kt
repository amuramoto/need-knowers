package com.needknowers.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.fragment_search_location.view.*

class SearchLocationFragment : Fragment() {

    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var destinationLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null
    private var placeName: String? = null
    var placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_location, container, false)
        autocompleteFragment = childFragmentManager.
                findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setCountry("SG")
        autocompleteFragment.setPlaceFields(placeFields)
        autocompleteFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener{
            override fun onPlaceSelected(place: Place) {
                Log.d("Place", place.toString())
                this@SearchLocationFragment.destinationLatLng = place.latLng
                view.btn_go.isEnabled = true
            }

            override fun onError(p0: Status) {

            }
        })

        view.btn_go.setOnClickListener {
            if (destinationLatLng != null){
                SearchLocationFragmentDirections.actionSearchLocationFragmentToDirectionListFragment(destinationLatLng!!,
                        placeName!!, currentLatLng!!)
                findNavController().navigate(R.id.action_searchLocationFragment_to_directionListFragment)
            } else {
                throw Exception("No LatLng")
            }
        }
        return view
    }

}
