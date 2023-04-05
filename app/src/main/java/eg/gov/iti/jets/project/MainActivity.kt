package eg.gov.iti.jets.project

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import eg.gov.iti.jets.project.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


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

    }

}