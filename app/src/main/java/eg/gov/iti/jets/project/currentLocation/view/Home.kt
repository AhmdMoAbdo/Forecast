package eg.gov.iti.jets.project.currentLocation.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimationDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.project.R
import eg.gov.iti.jets.project.currentLocation.viewModel.CurrentLocationViewModel
import eg.gov.iti.jets.project.currentLocation.viewModel.CurrentLocationViewModelFactory
import eg.gov.iti.jets.project.database.ConcreteLocalSource
import eg.gov.iti.jets.project.databinding.FragmentHomeBinding
import eg.gov.iti.jets.project.model.*
import eg.gov.iti.jets.project.network.ApiClient
import eg.gov.iti.jets.project.network.ApiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*

class Home : Fragment() {

    private lateinit var viewModelFactory: CurrentLocationViewModelFactory
    private lateinit var viewModel: CurrentLocationViewModel
    private lateinit var settingsPref: SharedPreferences
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var geocoder: Geocoder
    private lateinit var language: String
    private lateinit var speed: String
    private lateinit var temp: String
    private lateinit var loc:String
    private var saveToDB:Boolean = true
    private lateinit var mFusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsPref = requireContext().getSharedPreferences(Setup.SettingsSharedPref, Context.MODE_PRIVATE)
        language = settingsPref.getString(Setup.SettingsSharedPrefLanguage, "en")!!
        speed = settingsPref.getString(Setup.SettingsSharedPrefSpeed, "N/A")!!
        temp = settingsPref.getString(Setup.SettingsSharedPrefTemp, "N/A")!!
        loc = settingsPref.getString(Setup.SettingsSharedPrefMapping, "gps")!!
        binding.imgSettings.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.settings)
        }
        binding.gpsOffBtn.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        if (!Setup.checkPermissions(requireContext())&&loc=="gps") {
            hideUI()
            showGpsError()

        } else {
            hideGpsError()
            saveToDB = true
            binding.mainWeatherCard.setOnClickListener { animateWeatherCard() }
            binding.homeLottie.setOnClickListener { binding.homeLottie.playAnimation() }

            var pref: SharedPreferences
            var lat: String
            var lon: String
            pref = requireContext().getSharedPreferences(
                Setup.FavToHomeSharedPref,
                Context.MODE_PRIVATE
            )
            if (pref.getString("if", "M") == "F") {
                lat = pref.getString("lat", "33.44")!!
                lon = pref.getString("lon", "-94.04")!!
                binding.textHomeHeader.text = requireContext().getString(R.string.saved)
                binding.homeLottie.setAnimation("bookmark.json")
                val editor = pref.edit()
                saveToDB = false
                editor.putString("if", "M")
                editor.apply()
            } else {
                pref = if (loc == "gps") {
                    requireContext().getSharedPreferences(
                        Setup.HomeLocationSharedPref,
                        Context.MODE_PRIVATE
                    )
                } else {
                    requireContext().getSharedPreferences("MapLocation", Context.MODE_PRIVATE)
                }
                lat = pref.getString("lat", "33.44")!!
                lon = pref.getString("lon", "-94.04")!!
            }

            geocoder = Geocoder(requireContext(), Locale(language))

            viewModelFactory = CurrentLocationViewModelFactory(
                Repository.getInstance(
                    ApiClient.getInstance(),
                    ConcreteLocalSource(requireContext())
                )
            )
            viewModel =
                ViewModelProvider(this, viewModelFactory)[CurrentLocationViewModel::class.java]
            println(viewModel.setTime(1646306882,'T'))
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            if (loc == "gps") {
                Setup.getLastLocation(requireContext())
            }
            if (Setup.checkForInternet(requireContext())) {
                viewModel.getLocation(lat, lon, language)

                lifecycleScope.launch {
                    viewModel.location.collectLatest { result ->
                        when (result) {
                            is ApiState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                showUI()
                                if (saveToDB) {
                                    if (result.data.alerts == null) {
                                        result.data.alerts = emptyList()
                                    }
                                    viewModel.insertHomeDataToDataBase(result.data)
                                }
                                hourlyAdapter =
                                    HourlyAdapter(Setup.getHour(result.data), requireContext())
                                binding.hourlyRecyclerView.adapter = hourlyAdapter
                                hourlyAdapter.notifyDataSetChanged()
                                dailyAdapter = DailyAdapter(
                                    Setup.getDay(result.data, requireContext()),
                                    requireContext()
                                )
                                binding.dailyRecyclerView.adapter = dailyAdapter
                                dailyAdapter.notifyDataSetChanged()
                                setUIData(result.data)
                            }
                            is ApiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                hideUI()
                            }
                            else -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    "Server Failed Try Again Later",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            } else {

                viewModel.getHomeDataFromDataBase()
                viewModel.dbHomeData.observe(viewLifecycleOwner) {
                    if(it!=null) {
                        showUI()
                        setUIData(it)
                        dailyAdapter =
                            DailyAdapter(Setup.getDay(it, requireContext()), requireContext())
                        binding.dailyRecyclerView.adapter = dailyAdapter
                        dailyAdapter.notifyDataSetChanged()

                        hourlyAdapter = HourlyAdapter(Setup.getHour(it), requireContext())
                        binding.hourlyRecyclerView.adapter = hourlyAdapter
                        hourlyAdapter.notifyDataSetChanged()
                        Snackbar.make(
                            binding.coordinator,
                            "Check Your Internet Connection",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }else{
                        hideUI()
                        Snackbar.make(
                            binding.coordinator,
                            "Connect At least once to the Internet to get initial data",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }

    private fun showGpsError() {
        binding.gpsOffBtn.visibility = View.VISIBLE
        binding.gpsOffImage.visibility = View.VISIBLE
        binding.gpsOffTxt.visibility = View.VISIBLE
    }

    private fun hideGpsError() {
        binding.gpsOffBtn.visibility = View.GONE
        binding.gpsOffImage.visibility = View.GONE
        binding.gpsOffTxt.visibility = View.GONE
    }

    private fun hideUI() {
        binding.txtCountryName.visibility = View.GONE
        binding.txtDateAndTime.visibility = View.GONE
        binding.mainHomeConstraint.visibility = View.GONE
        binding.lottieMain.visibility = View.GONE
    }

    private fun showUI() {
        binding.txtCountryName.visibility = View.VISIBLE
        binding.txtDateAndTime.visibility = View.VISIBLE
        binding.mainHomeConstraint.visibility = View.VISIBLE
        binding.lottieMain.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if(Setup.checkPermissions(requireContext())&&binding.gpsOffBtn.visibility==View.VISIBLE){
            val intent = activity?.intent
            activity?.finish()
            startActivity(intent)
        }
    }

    private fun setUIData(root: Root) {
        animateWeatherCard()
        // animateScroll()
        val address:String = try {
            val list = geocoder.getFromLocation(root.lat, root.lon, 3) as MutableList<Address>
            list[0].adminArea.toString() + ", " + list[0].countryName.toString()
        }catch (e:java.lang.Exception){
            "${root.lat} , ${root.lon}"
        }
        binding.txtCountryName.text = address
        binding.txtDateAndTime.text = viewModel.setTime(root.current.dt + root.timezone_offset - 7200, 'F')

        var tempText = ""
        when (temp) {
            "f" -> {
                tempText = Setup.fromCtoF(root.current.temp).toInt().toString()
                binding.unit.text = "F"
            }
            "k" -> {
                tempText = Setup.fromCtoK(root.current.temp).toInt().toString()
                binding.unit.text = "K"
            }
            else -> {
                tempText = root.current.temp.toInt().toString()
            }
        }

        binding.txtDegree.text = tempText
        binding.skyState.text = root.current.weather[0].description
        if(root.current.weather[0].description.contains("snow"))binding.lottieMain.setAnimation("snow.json")
        else if(root.current.weather[0].description.contains("rain"))binding.lottieMain.setAnimation("rain.json")
        Picasso.get().load(Setup.getImage(root.current.weather[0].icon)).into(binding.imgCurrentWeatherState)

        var windSpeed = ""
        windSpeed = if (speed == "MPH") {
            DecimalFormat("##.##").format(Setup.fromMStoMH(root.current.wind_speed))
                .toString() + "M/H"
        } else {
            DecimalFormat("##.##").format(root.current.wind_speed).toString() + "M/S"
        }
        binding.txtWind.text = windSpeed
        val humidity = root.current.humidity.toString() + "%"
        binding.txtHumidity.text = humidity
        val pressure = root.current.pressure.toString() + " hpa"
        binding.txtPressure.text = pressure
        val cloud = root.current.clouds.toString() + "%"
        binding.txtCloud.text = cloud
        val visibility = root.current.visibility.toString() + " M"
        binding.txtVisibility.text = visibility

        val nextStopTime = viewModel.sunRiseOrSunSet(root)
        binding.txtRiseOrSetTime.text = nextStopTime

        if (nextStopTime.split(":")[1][2]=='A') {
            binding.txtRiseOrSet.text = resources.getString(R.string.sunrise)
            binding.imgRiseOrSet.setImageResource(R.drawable.sunrise)
        } else{
            binding.txtRiseOrSet.text = resources.getString(R.string.sunset)
            binding.imgRiseOrSet.setImageResource(R.drawable.sunset)
        }
    }

    private fun animateWeatherCard() {
        val animationDrawable = binding.mainWeatherCard.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2500)
        animationDrawable.setExitFadeDuration(1000)
        animationDrawable.start()
        Handler().postDelayed({ animationDrawable.stop() }, 3000)
    }

    override fun onStop() {
        super.onStop()
        if(Setup.checkPermissions(requireContext())&&loc=="gps"){
            Setup.mFusedLocationClient.removeLocationUpdates(Setup.CallBack.getInstance(requireContext()))
        }
    }

   /* private fun animateScroll() {
        binding.homeConstraint.setBackgroundResource(R.drawable.sun_gradient)
        val animationDrawable = binding.homeConstraint.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2500)
        animationDrawable.setExitFadeDuration(1000)
        animationDrawable.start()
    }*/

    /*override fun onResume() {
        super.onResume()
        if(loc=="gps") {Setup.getLastLocation(requireContext())}
    }*/
}