package eg.gov.iti.jets.project.currentLocation.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.project.model.Day
import eg.gov.iti.jets.project.model.Setup
import eg.gov.iti.jets.project.databinding.DailyListItemBinding

class DailyAdapter(private val days:List<Day>,var context: Context):RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
    private lateinit var binding: DailyListItemBinding
    lateinit var settingsPref: SharedPreferences
    lateinit var temp:String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater:LayoutInflater =parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)as LayoutInflater
        binding = DailyListItemBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        settingsPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        temp = settingsPref.getString("temp","N/A")!!
        var tempText = ""
        when (temp) {
            "f" -> {
                tempText =  Setup.fromCtoF(days[position].maxTemp).toInt().toString()+ "/"+
                        Setup.fromCtoF(days[position].minTemp).toInt().toString()
                binding.unit.text = "F"
            }
            "k" -> {
                tempText =  Setup.fromCtoK(days[position].maxTemp).toInt().toString()+ "/"+
                        Setup.fromCtoK(days[position].minTemp).toInt().toString()
                binding.unit.text = "K"
            }
            else -> {
                tempText = days[position].maxTemp.toInt().toString()+"/"+
                        days[position].minTemp.toInt().toString()
            }
        }
        holder.binding.dayName.text = days[position].name
        holder.binding.skyState.text = days[position].skyState
        holder.binding.txtDegree.text = tempText
        var image = Setup.getImage(days[position].icon)
        Picasso.get().load(image).into(holder.binding.imgWeather)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    class ViewHolder(var binding: DailyListItemBinding):RecyclerView.ViewHolder(binding.root)

}