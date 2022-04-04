package com.example.vehiclecontractor.ui.ClientRequest


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.navArgs
import com.example.vehiclecontractor.Extentions.*
import com.example.vehiclecontractor.R.*
import com.example.vehiclecontractor.databinding.FragmentSendClientRequestVcBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import java.util.*
import kotlin.collections.ArrayList
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import android.content.Intent

import com.example.vehiclecontractor.MainActivity

class SendClientRequestVC : Fragment() {
    private var _binding: FragmentSendClientRequestVcBinding? = null
    private val binding get() = _binding!!
    val args: SendClientRequestVCArgs by navArgs()
    var selecetedCompony = ""
    var selectedModel = ""
    var allVehicales: List<Vehical> = listOf()
    var selectedComponyIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendClientRequestVcBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val scennerData = args.scennerData//"Dealer Name: V.L.Automobiles=Dealer No: 89076542344=Partner Name: Swapnil Anil Katkar=Partner No: 8007415573=Address: Borivali (W)"

        val scannedInfo = scennerData.split("=").toTypedArray()

        binding.contrractorDetails.text = scennerData.replace("=","\n")

        binding.btnSubmit.setOnClickListener {
            if (binding.customerName.text.toString().isEmpty()) {
                showAlert("Please enter customer name")
                return@setOnClickListener
            }
            if (binding.customerMobNo.text.toString().isEmpty()) {
                showAlert("Please enter customer mobile number")
                return@setOnClickListener
            }

            val referenceID = UUID.randomUUID().toString()
            val sdf = SimpleDateFormat("dd/MMM/yyyy hh:mm a")
            val currentDate = sdf.format(Date())
            val reqInfo = SendCustomerInfo(scannedInfo[0],scannedInfo[1],scannedInfo[2],scannedInfo[3],scannedInfo[4],paymentID(),
                binding.customerName.text.toString(),
                binding.customerMobNo.text.toString(),
                selecetedCompony, selectedModel,"Initiated",
                currentDate,
                referenceID
            )
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val yearNMonthFor = SimpleDateFormat("yyyy/MMM")
            val yearNMonth = yearNMonthFor.format(Date())
            val databaseReference = firebaseDatabase.getReference("BookingReference/Dealers/${contracorNo()}/$yearNMonth/${referenceID}")
            databaseReference.setValue(reqInfo) { error, ref ->
                if (error == null) {
                    showAlert("Request submitted successfully. our team will connect you soon.")
                }
            }
        }
        fetchConfirmations()
        return root
    }

    private fun fetchConfirmations() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference =
            firebaseDatabase.getReference("Vehicles/Models")

        databaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(confirationDetails: DataSnapshot) {
                    if (confirationDetails.exists()) {
                        val users = confirationDetails.value as Map<String, Array<String>>
                        allVehicales = users.entries.map {
                            val json = Gson().toJson(it.value)
                            val gson = GsonBuilder().create()
                            val theList = gson.fromJson<ArrayList<String>>(
                                json,
                                object : TypeToken<ArrayList<String>>() {}.type
                            )
                            Vehical(it.key, theList)
                        }
                        configureDropDown()
                    } else {
                        showAlert("No Models Found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun configureDropDown() {
        val vehicalCompony = allVehicales.map { it.brand.uppercase() }
        val vehicalComponyDown = ArrayAdapter(this.requireContext(),android.R.layout.simple_spinner_item,vehicalCompony)
        vehicalComponyDown.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.VehicalCompony.adapter = vehicalComponyDown
        binding.VehicalCompony.setSelection(0)
        binding.VehicalCompony.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selecetedCompony = vehicalCompony[position]
                selectedComponyIndex = position
                configureModels()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }
    fun configureModels() {
        val vehicalModels = allVehicales[selectedComponyIndex].model.map { it.uppercase() }
        val vehicalModelDown = ArrayAdapter(this.requireContext(),android.R.layout.simple_spinner_item, vehicalModels)
        vehicalModelDown.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.VehicalModel.adapter = vehicalModelDown
        binding.VehicalModel.setSelection(0)
        binding.VehicalModel.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedModel = vehicalModels[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

}