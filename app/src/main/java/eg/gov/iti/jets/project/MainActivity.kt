package eg.gov.iti.jets.project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.*
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.location.FusedLocationProviderClient
import eg.gov.iti.jets.project.databinding.ActivityMainBinding

const val PERMISSION_ID = 5005

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var geoCoder: Geocoder
    lateinit var mLastLocation: Location


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.fragmentContainerView)
        NavigationUI.setupWithNavController(binding.btmNavBar, navController)
        binding.btmNavBar.background = null
        binding.btmNavBar.menu.getItem(1).isEnabled = false
        binding.btmNavBar.setOnItemSelectedListener {
            when (it) {
                binding.btmNavBar.menu.getItem(0) -> {
                    navController.navigate(R.id.currentLocations)
                    true
                }
                else -> {
                    navController.navigate(R.id.alert)
                    true
                }
            }
        }
        binding.fab.setOnClickListener {
            navController.navigate(R.id.home)
            binding.fab.setColorFilter(Color.BLUE)
            binding.btmNavBar.menu.getItem(0).isCheckable = false
            binding.btmNavBar.menu.getItem(2).isCheckable = false
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geoCoder = Geocoder(this)
        getLastLocation()

    }

    override fun onResume() {
        super.onResume()
        getLastLocation()
    }

    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
            } else {
                Toast.makeText(this, "Turn on Location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManger: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManger.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            mLastLocation = locationResult.lastLocation
            val pref: SharedPreferences =
                this@MainActivity.getSharedPreferences("HomeLocation", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = pref.edit()
            editor.putString("lat", mLastLocation.latitude.toString())
            editor.putString("lon", mLastLocation.longitude.toString())
            editor.commit()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }
}