package eg.gov.iti.jets.project.settings.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import eg.gov.iti.jets.project.R
import eg.gov.iti.jets.project.databinding.FragmentSettingsBinding
import eg.gov.iti.jets.project.model.Setup


class Settings : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var pref: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    private lateinit var mapbox: MapboxMap
    private lateinit var myPoint: Point
    private var languageType:String = ""
    private var temperatureSetting:String = ""
    private var speed:String = ""
    private var alertType:String = ""
    private var locationMethod:String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireContext().getSharedPreferences(Setup.SettingsSharedPref, Context.MODE_PRIVATE)
        editor = pref.edit()

        mapbox = binding.alertMapView.getMapboxMap()
        Setup.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.radioButtonEnglish.setOnClickListener {
            binding.radioButtonArabic.isChecked = false
            binding.settingsFAB.visibility = View.VISIBLE
            languageType = "en"

        }
        binding.radioButtonArabic.setOnClickListener {
            binding.radioButtonEnglish.isChecked = false
            binding.settingsFAB.visibility = View.VISIBLE
            languageType = "ar"
        }
        binding.radioButtonAlarms.setOnClickListener {
            binding.radioButtonNotifications.isChecked = false
            binding.settingsFAB.visibility = View.VISIBLE
            alertType = "alarms"
        }
        binding.radioButtonNotifications.setOnClickListener {
            binding.radioButtonAlarms.isChecked = false
            binding.settingsFAB.visibility = View.VISIBLE
            alertType = "notifications"

        }
        binding.radioButtonKelvin.setOnClickListener {
            binding.radioButtonCel.isChecked = false
            binding.radioButtonFer.isChecked = false
            binding.settingsFAB.visibility = View.VISIBLE
            temperatureSetting = "k"
        }
        binding.radioButtonCel.setOnClickListener {
            binding.radioButtonKelvin.isChecked = false
            binding.radioButtonFer.isChecked = false
            binding.settingsFAB.visibility = View.VISIBLE
            temperatureSetting = "c"
        }
        binding.radioButtonFer.setOnClickListener {
            binding.radioButtonKelvin.isChecked = false
            binding.radioButtonCel.isChecked = false
            binding.settingsFAB.visibility = View.VISIBLE
            temperatureSetting = "f"

        }
        binding.radioButtonMPH.setOnClickListener {
            binding.radioButtonMPS.isChecked = false
            binding.settingsFAB.visibility = View.VISIBLE
            speed = "MPH"
        }
        binding.radioButtonMPS.setOnClickListener {
            binding.radioButtonMPH.isChecked = false
            binding.settingsFAB.visibility = View.VISIBLE
            speed = "MPS"

        }
        binding.radioButtonMap.setOnClickListener {
            binding.radioButtonGPS.isChecked = false
            binding.pickLocationButton.visibility = View.VISIBLE
            binding.settingsFAB.visibility = View.VISIBLE
            locationMethod = "map"
        }
        binding.radioButtonGPS.setOnClickListener {
            binding.radioButtonMap.isChecked = false
            binding.pickLocationButton.visibility = View.GONE
            binding.settingsFAB.visibility = View.VISIBLE
            locationMethod = "gps"
            Setup.getLastLocation(requireContext())
        }

        binding.settingsFAB.setOnClickListener {
            if(locationMethod == "gps") Setup.getLastLocation(requireContext())
            else Setup.mFusedLocationClient.removeLocationUpdates(Setup.CallBack.getInstance(requireContext()))
            editor.putString(Setup.SettingsSharedPrefLanguage,languageType)
            editor.putString(Setup.SettingsSharedPrefMapping,locationMethod)
            editor.putString(Setup.SettingsSharedPrefSpeed,speed)
            editor.putString(Setup.SettingsSharedPrefTemp,temperatureSetting)
            editor.putString(Setup.SettingsSharedPrefAlerts,alertType)
            editor.apply()
            Setup.setLocale(requireContext(),languageType)
            requireActivity().recreate()
            binding.settingsFAB.visibility = View.GONE
        }

        setupRadioButtons()
        binding.pickLocationButton.setOnClickListener {
            setupMap(mapbox)
            binding.settingsFAB.visibility = View.VISIBLE
        }

        binding.alertMapFAB.setOnClickListener {
            binding.alertMapCard.visibility = View.GONE
            val mapPref: SharedPreferences = requireContext().getSharedPreferences("MapLocation", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = mapPref.edit()
            editor.putString("lat", myPoint.latitude().toString())
            editor.putString("lon", myPoint.longitude().toString())
            editor.apply()
            val customEditor: SharedPreferences.Editor = pref.edit()
            customEditor.putString(Setup.SettingsSharedPrefMapping,"map")
            customEditor.apply()
        }

        binding.closeMapImg.setOnClickListener{
            binding.alertMapCard.visibility = View.GONE
            binding.settingsFAB.visibility = View.GONE
        }
        mapbox.addOnMapLongClickListener { point ->
            myPoint = point
            binding.alertMapView.annotations.cleanup()
            addAnnotationToMap(point)
            binding.alertMapFAB.visibility = View.VISIBLE
            true
        }
        binding.settingsLottie.setOnClickListener {
            binding.settingsLottie.animate()
        }
    }

    private fun setupRadioButtons() {
        var settingsPref: String = pref.getString(Setup.SettingsSharedPrefLanguage,"N/A")!!
        if(settingsPref=="ar"){
            binding.radioButtonArabic.isChecked = true
            languageType = "ar"
        }
        else {
            binding.radioButtonEnglish.isChecked = true
            languageType = "en"
        }

        settingsPref = pref.getString(Setup.SettingsSharedPrefAlerts,"N/A")!!
        if(settingsPref=="alarms"){
            binding.radioButtonAlarms.isChecked = true
            alertType = "alarms"
        }
        else{
            binding.radioButtonNotifications.isChecked = true
            alertType = "notifications"
        }

        settingsPref = pref.getString(Setup.SettingsSharedPrefTemp,"N/A")!!
        when (settingsPref) {
            "k" -> {
                binding.radioButtonKelvin.isChecked = true
                temperatureSetting = "k"
            }
            "f" ->{
                binding.radioButtonFer.isChecked = true
                temperatureSetting = "f"
            }
            else ->{
                binding.radioButtonCel.isChecked = true
                temperatureSetting = "c"
            }
        }
        settingsPref = pref.getString(Setup.SettingsSharedPrefSpeed,"N/A")!!
        if(settingsPref=="MPH") {
            binding.radioButtonMPH.isChecked = true
            speed = "MPH"
        }
        else {
            binding.radioButtonMPS.isChecked = true
            speed = "MPS"
        }

        settingsPref = pref.getString(Setup.SettingsSharedPrefMapping,"N/A")!!
        if(settingsPref=="map"){
            binding.radioButtonMap.isChecked = true
            binding.pickLocationButton.visibility = View.VISIBLE
            Setup.mFusedLocationClient.removeLocationUpdates(Setup.CallBack.getInstance(requireContext()))
            locationMethod = "map"
        }
        else {
            Setup.getLastLocation(requireContext())
            binding.radioButtonGPS.isChecked = true
            binding.pickLocationButton.visibility = View.GONE
            locationMethod = "gps"
        }

    }
    override fun onStop() {
        super.onStop()
        if(pref.getString(Setup.SettingsSharedPrefMapping,"gps")=="gps") Setup.getLastLocation(requireContext())
        Setup.mFusedLocationClient.removeLocationUpdates(Setup.CallBack.getInstance(requireContext()))
    }

    private fun setupMap(mapbox: MapboxMap) {
        binding.alertMapCard.visibility = View.VISIBLE
        binding.alertMapFAB.visibility = View.GONE
        binding.alertMapView.annotations.cleanup()
        binding.alertMapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        val settingsPref: SharedPreferences = requireContext().getSharedPreferences(Setup.SettingsSharedPref, Context.MODE_PRIVATE)
        val pref:SharedPreferences = if(settingsPref.getString(Setup.SettingsSharedPrefMapping,"gps")=="gps"){
            requireContext().getSharedPreferences(
                Setup.HomeLocationSharedPref,
                Context.MODE_PRIVATE)
        }else{
            requireContext().getSharedPreferences(
                "MapLocation",
                Context.MODE_PRIVATE)
        }
        val initialCamera = CameraOptions.Builder()
            .center(
                Point.fromLngLat(
                    pref.getString("lon", "-94.04")!!.toDouble(),
                    pref.getString("lat", "33.44")!!.toDouble()
                )
            )
            .zoom(6.5)
            .build()
        mapbox.setCamera(initialCamera)
    }

    private fun addAnnotationToMap(point: Point) {
        Setup.bitmapFromDrawableRes(
            requireContext(),
            R.drawable.red_marker
        )?.let {
            val annotationApi = binding.alertMapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }


}