package com.needknowers.community

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DirectionListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DirectionListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DirectionListFragment : Fragment() {

    val args: DirectionListFragmentArgs by navArgs()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_direction_list, container, false)

        return view
    }


}// Required empty public constructor
