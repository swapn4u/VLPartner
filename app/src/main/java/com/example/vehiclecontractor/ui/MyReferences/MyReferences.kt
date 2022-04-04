package com.example.vehiclecontractor.ui.MyReferences

import android.Manifest
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bookmysaloon.ui.home.ManageReferenceAdapter
import com.example.vehiclecontractor.Extentions.*
import com.example.vehiclecontractor.R
import com.example.vehiclecontractor.databinding.FragmentHomeBinding
import com.example.vehiclecontractor.databinding.MyReferencesFragmentBinding
import com.example.vehiclecontractor.ui.ClientRequest.SendCustomerInfo
import com.google.firebase.database.*
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class MyReferences : Fragment() {

    private lateinit var viewModel: MyReferencesViewModel
    private var _binding: MyReferencesFragmentBinding? = null
    private lateinit var progressDialog: ProgressDialog
    private val binding get() = _binding!!
    private var allTReferences: ArrayList<SendCustomerInfo> = arrayListOf()
    private var tempReferences: List<SendCustomerInfo> = arrayListOf()
    var table : ManageReferenceAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyReferencesFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //
        progressDialog = ProgressDialog(this.requireContext())
        progressDialog.setTitle("Please wait...!!!")
        progressDialog.setMessage("Loading reference history...")
        progressDialog.setCanceledOnTouchOutside(false)

        if (!isActiveInternet()) {
            showAlert("Please check your internet connections")
        } else {
            fetchConfirmations()
        }
        if (ActivityCompat.checkSelfPermission(this.requireContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                1
            )
        }
        return root
    }

    private fun fetchConfirmations() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val sdf = SimpleDateFormat("yyyy/MMM")
        val currentDate = sdf.format(Date())
        val databaseReference = firebaseDatabase.getReference("BookingReference/Dealers/${contracorNo()}/$currentDate")
            .orderByChild("partnerNumber").equalTo("Partner No: ${ownnerNumber()}")

        databaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(confirationDetails: DataSnapshot) {
                    if (confirationDetails.exists()) {
                        val users = confirationDetails.children

                        users.forEach { salon ->
                            val json = Gson().toJson(salon.value)
                            val confirmation = Gson().fromJson(json, SendCustomerInfo::class.java)
                            if (!allTReferences.contains(confirmation)) {
                                allTReferences.add(confirmation)
                            }
                        }
                        if (allTReferences.isNotEmpty()) {
                            playRingtone()
                        }
                        tempReferences = allTReferences.toMutableList()
                        renderConfirmations(allTReferences)
                    } else {
                        progressDialog.hide()
                        showAlert("No new confirmation found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressDialog.dismiss()
                }
            })

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val json = Gson().toJson(dataSnapshot.value)
                val confirmation = Gson().fromJson(json, SendCustomerInfo::class.java)

                if (!allTReferences.contains(confirmation)) {
                    allTReferences.add(confirmation)
                    tempReferences = allTReferences.toMutableList()
                    updateNewConsfirmations()
//                    Toast.makeText(requireContext(), "New confirmation for ${confirmation.partnerName} added", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                if (dataSnapshot.exists()) {
                    val json = Gson().toJson(dataSnapshot.value)
                    val confirmation = Gson().fromJson(json, SendCustomerInfo::class.java)
                    val existingReference = allTReferences.filter { it.transactionReference == confirmation.transactionReference }
                    if (existingReference.isNotEmpty()) {
                       val index =  allTReferences.indexOf(existingReference.last())
                        allTReferences[index] = confirmation
                        updateNewConsfirmations()
                    }
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val json = Gson().toJson(dataSnapshot.value)
                    val appointment = Gson().fromJson(json, SendCustomerInfo::class.java)
//                    if (appointment.info?.requestId != null) {
//                        removeConfirmation(appointment.info)
//                    }
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {
                progressDialog.hide()
            }
        }
        databaseReference.addChildEventListener(childEventListener)
    }
    private fun updateNewConsfirmations() {
        playRingtone()
        table?.notifyDataSetChanged()
    }

    private fun renderConfirmations(salons: ArrayList<SendCustomerInfo>) {
        salons.also {
            allTReferences = it
            allTReferences.sortByDescending { it.updateDate.getDate()}
            table =  ManageReferenceAdapter(this.requireContext().applicationContext, R.layout.reference_status, it) { record, callNow, whatsApp ->
                if (callNow) {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.setData(Uri.parse("tel:" + "0${record.customerNo}"));
                    startActivity(callIntent)
                }
                if (whatsApp) {
                    val smsNumber = "91${record.customerNo}"
//                    val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
//                    if (isWhatsappInstalled) {
                    val sendIntent = Intent("android.intent.action.MAIN")
                    sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net")
                    startActivity(sendIntent)
//                    } else {
//                        val uri = Uri.parse("market://details?id=com.whatsapp")
//                        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
//                        showAlert("Whats app not installed")
//                        startActivity(goToMarket)
//                    }
                }
            }
            binding.referenceList.adapter = table
        }
        progressDialog.dismiss()
    }

    fun playRingtone() {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(this.requireContext(), notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}