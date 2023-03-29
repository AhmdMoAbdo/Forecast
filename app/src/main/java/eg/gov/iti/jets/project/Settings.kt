package eg.gov.iti.jets.project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eg.gov.iti.jets.project.model.Setup
import eg.gov.iti.jets.project.databinding.FragmentSettingsBinding


class Settings : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var pref: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

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

        binding.radioButtonEnglish.setOnClickListener {
            binding.radioButtonArabic.isChecked = false
            editor.putString(Setup.SettingsSharedPrefLanguage,"en")
            editor.apply()
        }
        binding.radioButtonArabic.setOnClickListener {
            binding.radioButtonEnglish.isChecked = false
            editor.putString(Setup.SettingsSharedPrefLanguage,"ar")
            editor.apply()
        }
        binding.radioButtonAlarms.setOnClickListener {
            binding.radioButtonNotifications.isChecked = false
            editor.putString(Setup.SettingsSharedPrefAlerts,"alarms")
            editor.apply()
        }
        binding.radioButtonNotifications.setOnClickListener {
            binding.radioButtonAlarms.isChecked = false
            editor.putString(Setup.SettingsSharedPrefAlerts,"notifications")
            editor.apply()
        }
        binding.radioButtonKelvin.setOnClickListener {
            binding.radioButtonCel.isChecked = false
            binding.radioButtonFer.isChecked = false
            editor.putString(Setup.SettingsSharedPrefTemp,"k")
            editor.apply()
        }
        binding.radioButtonCel.setOnClickListener {
            binding.radioButtonKelvin.isChecked = false
            binding.radioButtonFer.isChecked = false
            editor.putString(Setup.SettingsSharedPrefTemp,"c")
            editor.apply()
        }
        binding.radioButtonFer.setOnClickListener {
            binding.radioButtonKelvin.isChecked = false
            binding.radioButtonCel.isChecked = false
            editor.putString(Setup.SettingsSharedPrefTemp,"f")
            editor.apply()
        }
        binding.radioButtonMPH.setOnClickListener {
            binding.radioButtonMPS.isChecked = false
            editor.putString(Setup.SettingsSharedPrefSpeed,"MPH")
            editor.apply()
        }
        binding.radioButtonMPS.setOnClickListener {
            binding.radioButtonMPH.isChecked = false
            editor.putString(Setup.SettingsSharedPrefSpeed,"MPS")
            editor.apply()
        }
        binding.radioButtonMap.setOnClickListener {
            binding.radioButtonGPS.isChecked = false
            editor.putString(Setup.SettingsSharedPrefMapping,"map")
            editor.apply()
        }
        binding.radioButtonGPS.setOnClickListener {
            binding.radioButtonMap.isChecked = false
            editor.putString(Setup.SettingsSharedPrefMapping,"gps")
            editor.apply()
        }

        setupRadioButtons()
    }

    private fun setupRadioButtons() {
        var settingsPref: String = pref.getString(Setup.SettingsSharedPrefLanguage,"N/A")!!
        if(settingsPref=="ar")binding.radioButtonArabic.isChecked = true
        else binding.radioButtonEnglish.isChecked = true

        settingsPref = pref.getString(Setup.SettingsSharedPrefAlerts,"N/A")!!
        if(settingsPref=="alarms")binding.radioButtonAlarms.isChecked = true
        else binding.radioButtonNotifications.isChecked = true

        settingsPref = pref.getString(Setup.SettingsSharedPrefTemp,"N/A")!!
        when (settingsPref) {
            "k" -> binding.radioButtonKelvin.isChecked = true
            "f" -> binding.radioButtonFer.isChecked = true
            else -> binding.radioButtonCel.isChecked = true
        }

        settingsPref = pref.getString(Setup.SettingsSharedPrefSpeed,"N/A")!!
        if(settingsPref=="MPH") binding.radioButtonMPH.isChecked = true
        else binding.radioButtonMPS.isChecked = true

        settingsPref = pref.getString(Setup.SettingsSharedPrefMapping,"N/A")!!
        if(settingsPref=="map")binding.radioButtonMap.isChecked = true
        else binding.radioButtonGPS.isChecked = true

    }
}