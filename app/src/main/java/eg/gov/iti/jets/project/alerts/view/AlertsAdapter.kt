package eg.gov.iti.jets.project.alerts.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eg.gov.iti.jets.project.databinding.AlertListItemBinding
import eg.gov.iti.jets.project.model.DBAlerts

class AlertsAdapter(var alerts:List<DBAlerts> , var deleteAlert:(DBAlerts)->(Unit)):RecyclerView.Adapter<AlertsAdapter.ViewHolder>() {

    lateinit var binding:AlertListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater:LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertListItemBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.countryName.text = alerts[position].country
        holder.binding.DateText.text = alerts[position].date
        holder.binding.TimeText.text = alerts[position].time
        holder.binding.imgDelete.setOnClickListener {
            deleteAlert(alerts[position])
        }
    }

    override fun getItemCount(): Int {
        return alerts.size
    }

    class ViewHolder(var binding: AlertListItemBinding):RecyclerView.ViewHolder(binding.root)
}