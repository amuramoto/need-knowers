package com.needknowers.community

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000

class LoginFragment : Fragment() {
    private var nextScreenCallback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Setup the button click
        val btnCareTaker = view.findViewById<Button>(R.id.btn_careTaker)
        btnCareTaker.setOnClickListener {
            nextScreenCallback = {
                findNavController().navigate(R.id.action_loginFragment_to_careTakerMainFragment)
            }
            getLocationPermission()
        }

        val btnNeedKnower = view.findViewById<Button>(R.id.btn_needKnower)
        btnNeedKnower.setOnClickListener {
            nextScreenCallback = {
                findNavController().navigate(R.id.action_loginFragment_to_needKnowerMainFragment)
            }
            getLocationPermission()
        }

        return view
    }


    private fun getLocationPermission() {
        /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(activity!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            nextScreenCallback?.invoke()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    nextScreenCallback?.invoke()
                }
            }
        }
    }
//
//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>,
//                                            grantResults: IntArray) {
//
//    }

}
