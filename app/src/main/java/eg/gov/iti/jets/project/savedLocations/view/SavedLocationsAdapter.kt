package eg.gov.iti.jets.project.savedLocations.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eg.gov.iti.jets.project.model.SavedLocation
import eg.gov.iti.jets.project.databinding.SavedLocationItemBinding

class SavedLocationsAdapter(private var savedLocations:List<SavedLocation>, val delete:(SavedLocation)->Unit, val showLocation:(SavedLocation)->Unit):RecyclerView.Adapter<SavedLocationsAdapter.ViewHolder>() {

    lateinit var binding: SavedLocationItemBinding

    fun setList(incomingList:List<SavedLocation>){
        savedLocations = incomingList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater:LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)as LayoutInflater
        binding = SavedLocationItemBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.countryName.text = savedLocations[position].name
        holder.binding.imgDelete.setOnClickListener { delete(savedLocations[position]) }
        holder.binding.fullCard.setOnClickListener { showLocation(savedLocations[position]) }
    }
    override fun getItemCount(): Int {
        return savedLocations.size
    }
    class ViewHolder(var binding: SavedLocationItemBinding):RecyclerView.ViewHolder(binding.root)
}