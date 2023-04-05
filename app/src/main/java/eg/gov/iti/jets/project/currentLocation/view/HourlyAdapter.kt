package eg.gov.iti.jets.project.currentLocation.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.project.model.Hour
import eg.gov.iti.jets.project.model.Setup
import eg.gov.iti.jets.project.databinding.HourlyListItemBinding

class HourlyAdapter(private var hours:List<Hour>,var context: Context):RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {
    private lateinit var binding: HourlyListItemBinding
    private lateinit var settingsPref: SharedPreferences
    lateinit var temp:String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater:LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)as LayoutInflater
        binding = HourlyListItemBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        settingsPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        temp = settingsPref.getString("temp","N/A")!!
        var tempText = ""
        when (temp) {
            "f" -> {
                tempText =  Setup.fromCtoF(hours[position].temp).toInt().toString()
                binding.unit.text = "F"
            }
            "k" -> {
                tempText =  Setup.fromCtoK(hours[position].temp).toInt().toString()
                binding.unit.text = "K"
            }
            else -> {tempText = hours[position].temp.toInt().toString()}
        }
        holder.binding.hour.text = hours[position].hour
        holder.binding.txtDegree.text = tempText
        val image = Setup.getImage(hours[position].icon)
        Picasso.get().load(image).into(holder.binding.weatherIcon)
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    class ViewHolder(var binding: HourlyListItemBinding):RecyclerView.ViewHolder(binding.root)
}