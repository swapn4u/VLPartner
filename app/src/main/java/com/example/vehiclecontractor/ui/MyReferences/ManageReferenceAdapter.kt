package com.example.bookmysaloon.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlin.collections.ArrayList
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.example.vehiclecontractor.R
import com.example.vehiclecontractor.ui.ClientRequest.SendCustomerInfo
import java.util.*


class ManageReferenceAdapter(var contexts: Context, var resources: Int, var items: List<SendCustomerInfo>, val clickListener: (appointment: SendCustomerInfo, callNow: Boolean, whatsApp: Boolean) -> Unit): ArrayAdapter<SendCustomerInfo>(contexts, resources, items) {

    private var allConfirmations = ArrayList<SendCustomerInfo>(items)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflactor = LayoutInflater.from(context)
        val view:View = layoutInflactor.inflate(resources,null)
        val statusImg: ImageView = view.findViewById(R.id.refStatusImg)
        val customer_name: TextView = view.findViewById(R.id.customer_name)
        val customer_num: TextView = view.findViewById(R.id.customer_num)
        val status: TextView = view.findViewById(R.id.status)
        val dateStatus: TextView = view.findViewById(R.id.date)
        val bikeInfo: TextView = view.findViewById(R.id.vehicalDetails)
        val btnCallNow: Button = view.findViewById(R.id.btnCallNow)
        val btnWatsapp: Button = view.findViewById(R.id.btnWhatsApp)

        val confirmation: SendCustomerInfo = allConfirmations[position]
        customer_name.text = "Customer Name: ${confirmation.customerName}"
        customer_num.text = "Customer Mobile: ${confirmation.customerNo}"
        bikeInfo.text =  "Look for: ${confirmation.vehicleBrand} - ${confirmation.vehicleModel}"
        dateStatus.text = confirmation.updateDate
        status.text = confirmation.currentStatus
        statusImg.setImageResource(getImageState(confirmation.currentStatus));
        btnCallNow.setOnClickListener {
            clickListener(allConfirmations[position], true,false)
        }
        btnWatsapp.setOnClickListener {
            clickListener(allConfirmations[position], false,true)
        }
        return view
    }
     fun getImageState(state: String) : Int {
         return  when(state) {
             "Initiated" -> R.drawable.ic_initiate
             "Calling Done" -> R.drawable.ic_calling_done
             "Customer Visited" -> R.drawable.ic_visit
             "Down Payment" -> R.drawable.ic_part_payment
             "Vehicle Delivered" -> R.drawable.ic_delivered
             "Payment Done" -> R.drawable.ic_payment_done
             else -> R.drawable.ic_initiate
         }
     }

    override fun getCount() = allConfirmations.size

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        allConfirmations = ArrayList(items)
    }

    override fun getItem(position: Int) = allConfirmations[position]

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                var filteredList = java.util.ArrayList<SendCustomerInfo>()
                if (constraint.isBlank() or constraint.isEmpty()) {
                    filteredList = items as java.util.ArrayList<SendCustomerInfo>
                } else {
                    val filteredPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                    items.forEach { saloon ->
                        if (saloon.customerName.lowercase(Locale.getDefault()).contains(filteredPattern) == true) {
                            filteredList.add(saloon)
                        }
                    }
                }
                val result = FilterResults()
                result.values = filteredList
                result.count = filteredList.size
                return  result
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                allConfirmations = results.values as java.util.ArrayList<SendCustomerInfo>
                notifyDataSetChanged()
            }

            override fun convertResultToString(result: Any) = (result as SendCustomerInfo).customerName
        }
    }
}



