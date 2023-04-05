package eg.gov.iti.jets.project.splash.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import eg.gov.iti.jets.project.MainActivity
import eg.gov.iti.jets.project.R
import eg.gov.iti.jets.project.databinding.ActivitySplashBinding
import eg.gov.iti.jets.project.databinding.IntroDialogBinding
import eg.gov.iti.jets.project.model.Setup

class Splash : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding
    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var geoCoder: Geocoder
    private lateinit var myPoint: Point
    private lateinit var pref: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    private lateinit var mapbox: MapboxMap
    private var step = 1
    private lateinit var dialog: Dialog
    private lateinit var dialogCard:IntroDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogCard = IntroDialogBinding.inflate(layoutInflater)
        dialog = Dialog(this)
        dialog.setContentView(dialogCard.root)

        mapbox = binding.alertMapView.getMapboxMap()
        binding.alertMapCard.visibility = View.GONE
        pref = this.getSharedPreferences(Setup.SettingsSharedPref, Context.MODE_PRIVATE)
        editor = pref.edit()
        if(pref.getString("goToSecondPage","false")=="True"){
            Setup.setLocale(this,pref.getString(Setup.SettingsSharedPrefLanguage,"en")!!)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        Setup.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geoCoder = Geocoder(this)
        binding.backImg.visibility = View.GONE
        pagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.addOnPageChangeListener(listener)
        binding.wormDotsIndicator.attachTo(binding.viewPager)
        binding.nextImg.setOnClickListener {
            if (getItem(0) < 3) {
                binding.viewPager.setCurrentItem(getItem(1), true)
            } else {
                showSettingsDialog()
            }
        }
        binding.backImg.setOnClickListener {
            if (getItem(0) > 0) {
                binding.viewPager.setCurrentItem(getItem(-1), true)
            }
        }
        binding.alertMapFAB.setOnClickListener {
            binding.alertMapCard.visibility = View.GONE
            step++
            val mapPref: SharedPreferences = this.getSharedPreferences("MapLocation", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = mapPref.edit()
            editor.putString("lat", myPoint.latitude().toString())
            editor.putString("lon", myPoint.longitude().toString())
            editor.apply()
            showSettingsDialog()
        }

        binding.closeMapImg.setOnClickListener{
            binding.alertMapCard.visibility = View.GONE
        }
        mapbox.addOnMapLongClickListener { point ->
            myPoint = point
            binding.alertMapView.annotations.cleanup()
            addAnnotationToMap(point)
            binding.alertMapFAB.visibility = View.VISIBLE
            true
        }
    }

    private fun getItem(i: Int): Int {
        return binding.viewPager.currentItem + i
    }

    private var listener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            if (position == 0) binding.backImg.visibility = View.GONE
            else binding.backImg.visibility = View.VISIBLE
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun showSettingsDialog() {

        prepareDialog(step,dialogCard)
        if (step == 1) {
            dialogCard.back.visibility = View.GONE
            dialogCard.thirdRadio.visibility = View.VISIBLE
        }else{
            dialogCard.back.visibility = View.VISIBLE
            dialogCard.thirdRadio.visibility = View.GONE
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialogCard.next.setOnClickListener {
            saveToPref(dialogCard)
        }
        dialogCard.back.setOnClickListener {
            step--
            showSettingsDialog()
        }
    }

    private fun prepareDialog(step:Int,dialogCard:IntroDialogBinding){
        when(step){
            1->{
                dialogCard.questionText.text = this.getString(R.string.preferredTemp)
                dialogCard.firstRadio.text = this.getString(R.string.fahrenheit)
                dialogCard.secondRadio.text = this.getString(R.string.kelvin)
                dialogCard.thirdRadio.text = this.getString(R.string.celsius)
                dialogCard.pageNumber.text = "1/5"
            }
            2->{
                dialogCard.questionText.text = this.getString(R.string.preferredLocation)
                dialogCard.firstRadio.text = this.getString(R.string.gps)
                dialogCard.secondRadio.text = this.getString(R.string.map)
                dialogCard.pageNumber.text = "2/5"
            }
            3->{
                dialogCard.questionText.text = this.getString(R.string.preferredWindUnit)
                dialogCard.firstRadio.text = this.getString(R.string.mph)
                dialogCard.secondRadio.text = this.getString(R.string.mps)
                dialogCard.pageNumber.text = "3/5"
            }
            4->{
                dialogCard.questionText.text = this.getString(R.string.preferredAlert)
                dialogCard.firstRadio.text = this.getString(R.string.alarm)
                dialogCard.secondRadio.text = this.getString(R.string.notification)
                dialogCard.pageNumber.text = "4/5"
            }
            5->{
                dialogCard.questionText.text = this.getString(R.string.preferredLanguage)
                dialogCard.firstRadio.text = this.getString(R.string.english)
                dialogCard.secondRadio.text = this.getString(R.string.arabic)
                dialogCard.pageNumber.text = "5/5"
            }
        }
    }

    private fun saveToPref(dialogCard:IntroDialogBinding) {
        when (step) {
            1 -> {
                when (dialogCard.radioGroup.checkedRadioButtonId) {
                    dialogCard.firstRadio.id -> {
                        editor.putString(Setup.SettingsSharedPrefTemp,"f")
                        editor.apply()
                        step++
                        showSettingsDialog()
                    }
                    dialogCard.secondRadio.id -> {
                        editor.putString(Setup.SettingsSharedPrefTemp,"k")
                        editor.apply()
                        step++
                        showSettingsDialog()
                    }
                    dialogCard.thirdRadio.id -> {
                        editor.putString(Setup.SettingsSharedPrefTemp,"c")
                        editor.apply()
                        step++
                        showSettingsDialog()
                    }else->{
                        Toast.makeText(this,"Pick a temperature unit to continue",Toast.LENGTH_LONG).show()
                    }
                }
            }
            2 -> {
                when (dialogCard.radioGroup.checkedRadioButtonId) {
                    dialogCard.firstRadio.id -> {
                        editor.putString(Setup.SettingsSharedPrefMapping,"gps")
                        editor.apply()
                        Setup.getLastLocation(this)
                        step++
                        showSettingsDialog()
                    }
                    dialogCard.secondRadio.id -> {
                        editor.putString(Setup.SettingsSharedPrefMapping,"map")
                        editor.apply()
                        dialog.hide()
                        setupMap(mapbox)

                    }else->{
                        Toast.makeText(this,"Pick A method to determine your location to continue",Toast.LENGTH_LONG).show()
                    }
                }
            }
            3 -> {
                when (dialogCard.radioGroup.checkedRadioButtonId) {
                    dialogCard.firstRadio.id -> {
                        if(pref.getString(Setup.SettingsSharedPrefMapping,"gps")=="gps") Setup.getLastLocation(this)
                        editor.putString(Setup.SettingsSharedPrefSpeed,"MPH")
                        editor.apply()
                        step++
                        showSettingsDialog()
                    }
                    dialogCard.secondRadio.id -> {
                        if(pref.getString(Setup.SettingsSharedPrefMapping,"gps")=="gps") Setup.getLastLocation(this)
                        editor.putString(Setup.SettingsSharedPrefSpeed,"MPS")
                        editor.apply()
                        step++
                        showSettingsDialog()
                    }else ->{
                        Toast.makeText(this,"Pick A speed unit to continue",Toast.LENGTH_LONG).show()
                    }
                }
            }
            4 -> {
                when (dialogCard.radioGroup.checkedRadioButtonId) {
                    dialogCard.firstRadio.id -> {
                        editor.putString(Setup.SettingsSharedPrefAlerts,"alarms")
                        editor.apply()
                        step++
                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        requestPermissions(
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            1)
                        }
                        showSettingsDialog()
                    }
                    dialogCard.secondRadio.id -> {
                        editor.putString(Setup.SettingsSharedPrefAlerts,"notifications")
                        editor.apply()
                        step++
                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                            requestPermissions(
                                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                1)
                        }
                        showSettingsDialog()
                    }else ->{
                         Toast.makeText(this,"Pick an alert system to continue",Toast.LENGTH_LONG).show()
                    }
                }
            }
            5 -> {
                Setup.getLastLocation(this)
                when (dialogCard.radioGroup.checkedRadioButtonId) {
                    dialogCard.firstRadio.id -> {
                        Setup.setLocale(this,"en")
                        editor.putString(Setup.SettingsSharedPrefLanguage,"en")
                        editor.apply()
                        editor.putString("goToSecondPage","True")
                        editor.apply()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    dialogCard.secondRadio.id -> {
                        Setup.getLastLocation(this)
                        Setup.setLocale(this,"ar")
                        recreate()
                        editor.putString(Setup.SettingsSharedPrefLanguage,"ar")
                        editor.apply()
                        editor.putString("goToSecondPage","True")
                        editor.apply()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }else ->{
                        Toast.makeText(this,"Pick your preferred language to continue",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupMap(mapbox: MapboxMap) {
        binding.alertMapCard.visibility = View.VISIBLE
        binding.alertMapFAB.visibility = View.GONE
        binding.alertMapView.annotations.cleanup()
        binding.alertMapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        val pref: SharedPreferences = this.getSharedPreferences(Setup.HomeLocationSharedPref, Context.MODE_PRIVATE)
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
           this,
            R.drawable.red_marker
        )?.let {
            val annotationApi = binding.alertMapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(binding.alertMapView)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }
}