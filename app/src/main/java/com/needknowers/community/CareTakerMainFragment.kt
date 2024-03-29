package com.needknowers.community

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.fragment_care_taker_main.view.*


class CareTakerMainFragment : Fragment(), OnMapReadyCallback {

    private lateinit var db: FirebaseFirestore
    var mMap: GoogleMap? = null
    var needKnowerPin: Marker? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("needKnowers")
    var currentLatLng: LatLng? = null

    private var isAnimateCamera: Boolean = true
    val args: CareTakerMainFragmentArgs by navArgs()

    private var mListener: OnFragmentInteractionListener? = null


    // Include the OnCreate() method here too, as described above.
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val singapore = LatLng(1.352, 103.82)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 13f))
        needKnowerPin = mMap?.addMarker(
                MarkerOptions()
                        .visible(true)
                        .position(dawson_pos)
                        .title(args.needKnower.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
//
        needKnowerPin?.showInfoWindow()
        db.collection("NeedKnowers")
                .document(args.needKnower.id)
                .addSnapshotListener { documentSnapshot: DocumentSnapshot?, _: FirebaseFirestoreException? ->
                    val start: Map<String, Double> = documentSnapshot?.get("start") as Map<String, Double>?
                            ?: mapOf()
                    val end: Map<String, Double> = documentSnapshot?.get("end") as Map<String, Double>?
                            ?: mapOf()

                    if (start.containsKey("lat") && start.containsKey("long")) {

                        val destinationPin = mMap?.addMarker(
                                MarkerOptions()
                                        .position(LatLng(start["lat"]!!, start["long"]!!))
                                        .title("Destination")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                    }
                    if (end.containsKey("lat") && end.containsKey("long")) {
                        val startingPin = mMap?.addMarker(
                                MarkerOptions()
                                        .position(LatLng(end["lat"]!!, end["long"]!!))
                                        .title("Starting Location")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                    }
                    val directionListMap = documentSnapshot?.get("direction") as List<Map<String, Double>>
                    setPolyline(directionListMap)
                }
        myRef.child(args.needKnower.id).addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val lat: Double = snapshot.child("lat").value.toString().toDouble()
                val long: Double = snapshot.child("long").value.toString().toDouble()
                Log.d("lat", lat.toString())
                Log.d("long", long.toString())
                currentLatLng = LatLng(lat, long)
                needKnowerPin?.position = LatLng(lat, long)
                if (isAnimateCamera) {
                    mMap?.animateCamera(CameraUpdateFactory.newLatLng(LatLng(lat, long)))
                    isAnimateCamera = false
                }
            }

        })
    }

    fun setPolyline(directionListMap: List<Map<String, Double>>) {
        val latLngList: ArrayList<LatLng> = arrayListOf()
        directionListMap.forEach {
            latLngList.add(LatLng(it["startLat"]!!, it["startLong"]!!))
            latLngList.add(LatLng(it["endLat"]!!, it["endLong"]!!))
        }
        val line = mMap?.addPolyline(PolylineOptions()
                .addAll(latLngList)
                .width(5F)
                .color(Color.RED))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_care_taker_main, container, false)
        // Inflate the layout for this fragment
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(activity!!,
                R.array.persons_array, android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        view.findViewById<Toolbar>(R.id.toolbar2).apply {
            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(navController.graph)
            setupWithNavController(navController, appBarConfiguration)
            title = "${args.needKnower.name} Location"
        }
        view.focus_button.setOnClickListener {
            if (currentLatLng != null) {
                mMap?.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng))
            }

        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {


        private val dawson_pos = LatLng(1.351, 103.82)

        fun newInstance(param1: String, param2: String): CareTakerMainFragment {

            return CareTakerMainFragment()
        }
    }

}// Required empty public constructor
