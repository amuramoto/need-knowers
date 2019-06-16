package com.needknowers.community;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class CareTakerMainFragment extends Fragment
        implements OnMapReadyCallback {

    GoogleMap mMap;
    Marker m1;
    Marker m2;
    Marker m3;
    Marker m4;
    Marker m5;

    private static final int DEFAULT_ZOOM = 15;



    private static final LatLng dawson_pos = new LatLng(1.351, 103.82);
    private static final LatLng kenneth_pos = new LatLng(1.353, 103.82);
    private static final LatLng natalie_pos = new LatLng(1.354, 103.82);
    private static final LatLng wei_pos = new LatLng(1.355, 103.82);
    private static final LatLng john_pos = new LatLng(1.356, 103.82);



    // Include the OnCreate() method here too, as described above.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng singapore = new LatLng(1.352, 103.82);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, DEFAULT_ZOOM));

        m1 = mMap.addMarker(new MarkerOptions()
                .position(dawson_pos)
                .title("Dawson")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        m1.setTag(0);
        m1.setVisible(false);

        m2 = mMap.addMarker(new MarkerOptions()
                .position(kenneth_pos)
                .title("Kenneth")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        m2.setTag(0);

        m2.setVisible(false);

        m3 = mMap.addMarker(new MarkerOptions()
                .position(natalie_pos)
                .title("Natasha")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        m3.setTag(0);
        m3.setVisible(false);

        m4 = mMap.addMarker(new MarkerOptions()
                .position(wei_pos)
                .title("Wei Wang")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        m4.setTag(0);
        m4.setVisible(false);

        m5 = mMap.addMarker(new MarkerOptions()
                .position(john_pos)
                .title("John")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        m5.setTag(0);
        m5.setVisible(false);

    }

    private OnFragmentInteractionListener mListener;

    public CareTakerMainFragment() {
        // Required empty public constructor
    }

    public static CareTakerMainFragment newInstance(String param1, String param2) {
        CareTakerMainFragment fragment = new CareTakerMainFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_care_taker_main, container, false);
        // Inflate the layout for this fragment
        final Spinner spinner = (Spinner) view.findViewById(R.id.ct_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.persons_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItemId() == 0){
                    m1.setVisible(true);
                    m1.showInfoWindow();
                    m2.setVisible(false);
                    m3.setVisible(false);
                    m4.setVisible(false);
                    m5.setVisible(false);
                }
                if (spinner.getSelectedItemId() == 1){
                    m1.setVisible(false);
                    m2.setVisible(true);
                    m2.showInfoWindow();

                    m3.setVisible(false);
                    m4.setVisible(false);
                    m5.setVisible(false);
                }
                if (spinner.getSelectedItemId() == 2){
                    m1.setVisible(false);
                    m2.setVisible(false);
                    m3.setVisible(true);
                    m3.showInfoWindow();

                    m4.setVisible(false);
                    m5.setVisible(false);
                }
                if (spinner.getSelectedItemId() == 3){
                    m1.setVisible(false);
                    m2.setVisible(false);
                    m3.setVisible(false);
                    m4.setVisible(true);
                    m4.showInfoWindow();

                    m5.setVisible(false);
                }
                if (spinner.getSelectedItemId() == 4){
                    m1.setVisible(false);
                    m2.setVisible(false);
                    m3.setVisible(false);
                    m4.setVisible(false);
                    m5.setVisible(true);
                    m5.showInfoWindow();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
