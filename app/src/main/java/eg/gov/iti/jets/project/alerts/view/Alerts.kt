package eg.gov.iti.jets.project.alerts.view

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import android.text.format.DateFormat
import androidx.lifecycle.ViewModelProvider
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import eg.gov.iti.jets.project.R
import eg.gov.iti.jets.project.alerts.viewModel.AlertsViewModel
import eg.gov.iti.jets.project.alerts.viewModel.AlertsViewModelFactory
import eg.gov.iti.jets.project.database.ConcreteLocalSource
import eg.gov.iti.jets.project.databinding.DeleteDialogBinding
import eg.gov.iti.jets.project.model.Setup
import eg.gov.iti.jets.project.databinding.FragmentAlertsBinding
import eg.gov.iti.jets.project.model.DBAlerts
import eg.gov.iti.jets.project.model.Repository
import eg.gov.iti.jets.project.network.ApiClient
import java.text.SimpleDateFormat
import java.util.*


class Alerts : Fragment() {


    private lateinit var binding: FragmentAlertsBinding
    private lateinit var myPoint: Point
    private lateinit var geocoder: Geocoder
    private lateinit var alertsViewModel: AlertsViewModel
    private lateinit var alertsViewModelFactory: AlertsViewModelFactory
    private lateinit var alertsAdapter: AlertsAdapter
    private lateinit var requestIdPref:SharedPreferences
    private lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestIdPref = requireContext().getSharedPreferences("RequestID",Context.MODE_PRIVATE)
        editor = requestIdPref.edit()
        geocoder = Geocoder(requireContext())
        val mapbox = binding.alertMapView.getMapboxMap()
        binding.alertMapCard.visibility = View.GONE
        binding.imgSettings.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.settings)
        }

        fun checkSizeAndShowOrHideImages(size:Int){
            if(size==0){
                binding.noAlertsImg.visibility = View.VISIBLE
                binding.noAlertsText.visibility = View.VISIBLE
            }else{
                binding.noAlertsImg.visibility = View.GONE
                binding.noAlertsText.visibility = View.GONE
            }
        }
        alertsViewModelFactory = AlertsViewModelFactory(Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(requireContext())))
        alertsViewModel = ViewModelProvider(this,alertsViewModelFactory)[AlertsViewModel::class.java]
        alertsViewModel.dbAlerts.observe(viewLifecycleOwner){ dbAlerts ->
            checkSizeAndShowOrHideImages(dbAlerts.size)
            for (i in dbAlerts){
                if(shouldBeDeleted(i.date,i.time)) {
                    alertsViewModel.deleteAlert(i)
                    cancelAlarm(i.id.toInt())
                }
                checkSizeAndShowOrHideImages(dbAlerts.size)
            }
            alertsAdapter = AlertsAdapter(dbAlerts){
                showAlert(it)
                checkSizeAndShowOrHideImages(dbAlerts.size)
            }
            binding.alertsRecyclerView.adapter = alertsAdapter
            alertsAdapter.notifyDataSetChanged()
        }
        val c = Calendar.getInstance()
        binding.alertFAB.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth -> c.set(Calendar.YEAR, year)

                    c.set(Calendar.MONTH, month)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    TimePickerDialog(requireContext(), { _, hourOfDay, minute ->

                        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        c.set(Calendar.MINUTE, minute)
                        setupMap(mapbox)

                        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), DateFormat.is24HourFormat(requireContext())).show()
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()
        }
        mapbox.addOnMapLongClickListener { point ->
            myPoint = point
            binding.alertMapView.annotations.cleanup()
            addAnnotationToMap(point)
            binding.alertMapFAB.visibility = View.VISIBLE
            true
        }

        binding.alertMapFAB.setOnClickListener {
            binding.alertMapCard.visibility = View.GONE
            val list = geocoder.getFromLocation(myPoint.latitude(), myPoint.longitude(), 3) as MutableList<Address>
            val address = list[0].adminArea.toString() + ", " + list[0].countryName.toString()
            val date:String = getDate(c)
            val time:String = getTime(c)
            var nextRequestId = requestIdPref.getString("ID","0")!!.toInt()
            nextRequestId++
            val dbAlert = DBAlerts(nextRequestId.toLong(),address,date,time)
            editor.putString("ID",nextRequestId.toString())
            editor.apply()
            alertsViewModel.insertAlert(dbAlert)
            checkSizeAndShowOrHideImages(1)
            val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireContext(), AlertReceiver::class.java)
            intent.putExtra("lat",myPoint.latitude().toString())
            intent.putExtra("lon",myPoint.longitude().toString())
            intent.putExtra("id",nextRequestId.toString())
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), nextRequestId, intent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
        }

        binding.closeMapImg.setOnClickListener{
            binding.alertMapCard.visibility = View.GONE
        }

        binding.alertLottie.setOnClickListener {
            binding.alertLottie.animate()
        }
    }

    private fun setupMap(mapbox:MapboxMap) {
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
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(binding.alertMapView)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    private fun getDate(calendar: Calendar):String{
        val unit =
            if(calendar.get(Calendar.DAY_OF_MONTH)==1||calendar.get(Calendar.DAY_OF_MONTH)==21||calendar.get(Calendar.DAY_OF_MONTH)==31) "st"
            else if(calendar.get(Calendar.DAY_OF_MONTH)==2||calendar.get(Calendar.DAY_OF_MONTH)==22) "nd"
            else if(calendar.get(Calendar.DAY_OF_MONTH)==3||calendar.get(Calendar.DAY_OF_MONTH)==23) "rd"
            else "th"
        return  "${calendar.get(Calendar.DAY_OF_MONTH)}$unit of ${SimpleDateFormat("MMMM").format(calendar.time)},${calendar.get(Calendar.YEAR)}"
    }

    private fun getTime(calendar: Calendar):String{
        return if(calendar.get(Calendar.HOUR_OF_DAY)>12) "${calendar.get(Calendar.HOUR_OF_DAY)-12}:${calendar.get(Calendar.MINUTE)}PM"
        else if(calendar.get(Calendar.HOUR_OF_DAY)==12) "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}PM"
        else if(calendar.get(Calendar.HOUR_OF_DAY)<12) "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}AM"
        else if(calendar.get(Calendar.HOUR_OF_DAY)==0) "12:${calendar.get(Calendar.MINUTE)}AM"
        else "12:Am"
    }

    private fun cancelAlarm(id:Int){
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), id, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    private fun shouldBeDeleted(date:String,time:String):Boolean{
        val dateArr = date.split(" ")
        val dayString:String = dateArr[0].takeWhile { !it.isLetter() }
        val dayNumber:Int = dayString.toInt()
        val monthNumber = when (dateArr[2].split(",")[0]) {
            "January" -> 0
            "February" -> 1
            "March" -> 2
            "April" -> 3
            "May" -> 4
            "June" -> 5
            "July" -> 6
            "August" -> 7
            "September" -> 8
            "October" -> 9
            "November" -> 10
            else -> 11
        }
        val yearNumber = dateArr[2].split(",")[1].toInt()

        val timeArr = time.split(":")
        var hourNumber = timeArr[0].toInt()
        val amOrPm = timeArr[1].takeLastWhile { it.isLetter() }
        val minuteString =  timeArr[1].takeWhile { !it.isLetter() }
        val minuteNumber = minuteString.toInt()
        if(amOrPm[0]=='P'&&hourNumber!=12){hourNumber+=12}

        val c : Calendar = Calendar.getInstance()
        return if(yearNumber>c.get(Calendar.YEAR)) false
        else if(yearNumber<c.get(Calendar.YEAR)) true
        else{
            if(monthNumber>c.get(Calendar.MONTH)) false
            else if(monthNumber<c.get(Calendar.MONTH)) true
            else{
                if(dayNumber>c.get(Calendar.DAY_OF_MONTH)) false
                else if(dayNumber<c.get(Calendar.DAY_OF_MONTH)) true
                else{
                    val myTimeInSec = hourNumber*60*60+minuteNumber*60
                    val actualTimeInSec = c.get(Calendar.HOUR_OF_DAY)*60*60+c.get(Calendar.MINUTE)*60
                    myTimeInSec < actualTimeInSec
                }
            }
        }
    }

    private fun showAlert(dbAlert:DBAlerts){
        val dialogCard:DeleteDialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogCard.root)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialogCard.yesButton.setOnClickListener{
            cancelAlarm(dbAlert.id.toInt())
            alertsViewModel.deleteAlert(dbAlert)
            alertsAdapter.notifyDataSetChanged()
            dialog.hide()
        }
        dialogCard.noButton.setOnClickListener {
            dialog.hide()
        }
    }
}