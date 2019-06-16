package com.needknowers.community

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.speech.tts.TextToSpeech
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.*
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.needknowers.community.model.AppDirection
import com.needknowers.community.model.AppStep
import com.needknowers.community.model.AppStop
import kotlinx.android.synthetic.main.fragment_direction_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.round


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DirectionListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DirectionListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DirectionListFragment : Fragment(), OnStreetViewPanoramaReadyCallback, SensorEventListener, TextToSpeech.OnInitListener {
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsLang = tts.setLanguage(Locale.US)

            if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language is not supported!")
            } else {
                Log.i("TTS", "Language Supported.")
            }
            Log.i("TTS", "Initialization success.")
        } else {
            Toast.makeText(context!!, "TTS Initialization failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private var hasArrived: Boolean = false
    private lateinit var tts: TextToSpeech
    private var isDirectionDetected: Boolean = false
    private var initialDirection: String? = null
    private var magnetometer: Sensor? = null
    private var accelerometer: Sensor? = null
    private var mSensorManager: SensorManager? = null
    private var distanceApart: Float = 0F
    private var overallSteps: ArrayList<AppStep> = arrayListOf()
    private var overallBigSteps: ArrayList<AppStep> = arrayListOf()
    private var mPanorama: StreetViewPanorama? = null
    private var currentArrivalTransitStop: AppStop? = null
    private var upcomingDepartureTransitStop: AppStop? = null
    val TAG = "DIRECTIONLISTFRAGMENT"

    val args: DirectionListFragmentArgs by navArgs()
    val service = AppService.INSTANCE.service
    var currentBigStepIndex = 0
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var places: PlacesClient
    private var streetViewPanoramaView: StreetViewPanoramaView? = null


    var currentLongitude: Double = 0.0
    var currentLatitude: Double = 0.0
    var currentDestLongitude: Double = 0.0
    var currentDestLatitude: Double = 0.0

    // Use fields to define the data types to return.
    var placeFields = listOf(Place.Field.NAME)

    // Use the builder to create a FindCurrentPlaceRequest.
    var request = FindCurrentPlaceRequest.builder(placeFields).build()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAGG", args.placeName)

    }

    fun makeNetworkCall() {
        service.requestDirection("${args.currentLatLng.latitude},${args.currentLatLng.longitude}",
                "${args.destLatLng.latitude},${args.destLatLng.longitude}",
                "transit", getString(R.string.MAP_API_KEY)
        ).enqueue(object : Callback<AppDirection> {
            override fun onFailure(call: Call<AppDirection>, t: Throwable) {
                Toast.makeText(this@DirectionListFragment.context, "ERROR", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<AppDirection>, response: Response<AppDirection>) {
                Log.d("DIRECTION", response.body().toString())
                val appDirection = response.body()
                if (appDirection != null) {
                    loadDirections(appDirection.routes[0].legs[0].steps)
                    transitDirection()
                    speakDirection()
                    notifyStep()
                }
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(context!!, this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        places = Places.createClient(context!!)
        mSensorManager = getSystemService(context!!, SensorManager::class.java)
        accelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onStreetViewPanoramaReady(panorama: StreetViewPanorama?) {
        mPanorama = panorama
        makeNetworkCall()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_direction_list, container, false)


        streetViewPanoramaView = view.findViewById(R.id.d_streetView)
        streetViewPanoramaView?.onCreate(savedInstanceState)
        streetViewPanoramaView?.getStreetViewPanoramaAsync(this)
        return view
    }

    fun loadDirections(steps: ArrayList<AppStep>) {
        steps.forEach { bigStep ->
            overallBigSteps.add(bigStep)
            if (bigStep.steps != null) {
                overallSteps.addAll(bigStep.steps)
            }
        }
        Log.d("DIRECTION", overallSteps.toString())

    }

    fun speakNearby() {
        overallBigSteps[currentBigStepIndex]
    }

    fun speakDirection() {
        if (overallBigSteps.size <= currentBigStepIndex) {
            return
        }
        val currentBigStep = overallBigSteps[currentBigStepIndex]
        if (currentBigStep.travelMode == "TRANSIT") {
            tts.speak("Take the " + currentBigStep.htmlInstructions, TextToSpeech.QUEUE_ADD, null, null)
        } else if (currentBigStepIndex < overallBigSteps.size - 1 && overallBigSteps[currentBigStepIndex + 1].travelMode == "TRANSIT") {
            tts.speak("Go take the " + overallBigSteps[currentBigStepIndex + 1].htmlInstructions, TextToSpeech.QUEUE_ADD, null, null)
        }
        if (currentBigStepIndex == overallBigSteps.size - 1){
            tts.speak("You are almost there. Walk to ${args.placeName}", TextToSpeech.QUEUE_ADD, null, null)
        }
    }

    fun transitDirection() {
        if (overallBigSteps.size <= currentBigStepIndex) {
            return
        }
        val currentBigStep = overallBigSteps[currentBigStepIndex]
        if (currentBigStep.travelMode == "TRANSIT") {
            currentArrivalTransitStop = overallBigSteps[currentBigStepIndex].transitDetails?.arrivalStop
        }
        if (overallBigSteps.size > 1 && currentBigStepIndex < overallBigSteps.size - 1 && overallBigSteps[currentBigStepIndex + 1].travelMode == "TRANSIT") {
            upcomingDepartureTransitStop = overallBigSteps[currentBigStepIndex + 1].transitDetails?.departureStop
        }
    }

    fun notifyStep() {
        if (overallBigSteps.size <= currentBigStepIndex) {
            return
        }
        val currentStep = overallBigSteps[currentBigStepIndex]
        Log.d("currentStep", currentStep.toString())

        if (currentStep.travelMode == "TRANSIT") {
            currentDestLatitude = currentArrivalTransitStop?.location?.lat
                    ?: throw Exception("currentDestLatitudeNotSet")
            currentDestLongitude = currentArrivalTransitStop?.location?.long
                    ?: throw Exception("currentDestLongitudeNotSet")
            tv_current_direction.text = Html.fromHtml(currentStep.htmlInstructions, Html.FROM_HTML_MODE_COMPACT)
        } else if (overallBigSteps.size > 1 && currentBigStepIndex < overallBigSteps.size - 1 && overallBigSteps[currentBigStepIndex + 1].travelMode == "TRANSIT") {
            currentDestLatitude = upcomingDepartureTransitStop?.location?.lat
                    ?: throw Exception("upcomingDestLatitudeNotSet")
            currentDestLongitude = upcomingDepartureTransitStop?.location?.long
                    ?: throw Exception("upcomingDestLongitudeNotSet")
            tv_current_direction.text = Html.fromHtml("Go take the " + overallBigSteps[currentBigStepIndex + 1].htmlInstructions, Html.FROM_HTML_MODE_COMPACT)
        } else {
            currentDestLatitude = currentStep.endLocation.lat
            currentDestLongitude = currentStep.endLocation.long
            tv_current_direction.text = "You are almost there. Walk to ${args.placeName}"
        }

        mPanorama?.isUserNavigationEnabled = false
        mPanorama?.isStreetNamesEnabled = true
        Log.d("dest", "lat $currentDestLatitude long $currentDestLongitude")
        mPanorama?.setPosition(LatLng(currentDestLatitude, currentDestLongitude))
        if (currentBigStepIndex + 1 < overallBigSteps.size) {
            val nextStep = overallBigSteps[currentBigStepIndex + 1]
            tv_next_direction.text = Html.fromHtml(nextStep.htmlInstructions, Html.FROM_HTML_MODE_COMPACT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        streetViewPanoramaView?.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        streetViewPanoramaView?.onResume()
        mSensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager?.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    var mGravity: FloatArray? = null
    var mGeomagnetic: FloatArray? = null
    override fun onSensorChanged(event: SensorEvent) {

    }

    fun headingToString(x: Double): String {
        val directions = arrayOf("north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest", "north")
        return directions[Math.round(x % 360 / 45).toInt()]
    }

    /** Convert such that North=0, East=90, South=180, West=270. */
    fun azimuthToDegrees(azimuth: Float): Float {
        return ((Math.toDegrees(azimuth.toDouble()) + 360) % 360).toFloat()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 3000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */)

    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        streetViewPanoramaView?.onPause()
        mSensorManager?.unregisterListener(this)
    }

    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            val location = locationResult.locations.first()
            currentLongitude = location.longitude
            currentLatitude = location.latitude

            distanceApart = round(distInMeters(currentLatitude.toFloat(),
                    currentLongitude.toFloat(),
                    currentDestLatitude.toFloat(),
                    currentDestLongitude.toFloat()) * 100) / 100

            tv_meters.text = "$distanceApart m from destination"
            Log.d("TAG", location.toString())
            //upload to server
            if (distanceApart < 300) {
                alertUser()
            }
            if (distanceApart < 20) {
                currentBigStepIndex += 1
                timeCalled = 0
                if (checkIsDone()) {
                    return
                }
                transitDirection()
                speakDirection()
                notifyStep()
            }
        }
    }

    private fun checkIsDone(): Boolean {
        if (currentBigStepIndex == overallBigSteps.size) {
            tts.stop()
            tts.speak("You have arrived!", TextToSpeech.QUEUE_ADD, null, null)
            tv_current_direction.text = "You have arrived! 😎"
            hasArrived = true
            return true
        }
        return false
    }


    private var timeCalled = 0
    private val timeToCall = 2

    private fun alertUser() {
        if (hasArrived){
            return
        }
        if (currentBigStepIndex != overallBigSteps.size) {
            timeCalled = 0
        }
        timeCalled += 1
        if (timeToCall == timeCalled){
            return
        }
        Handler().postDelayed({

            val v = getSystemService(context!!, Vibrator::class.java)
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v?.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                v?.vibrate(500)
            }
            tts.speak("Get ready to get off, you are near", TextToSpeech.QUEUE_ADD, null, null)
            alertUser()
        }, 1000)
    }
}// Required empty public constructor
