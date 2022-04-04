package com.example.vehiclecontractor.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vehiclecontractor.Extentions.contracorNo
import com.example.vehiclecontractor.Extentions.ownnerNumber
import com.example.vehiclecontractor.Extentions.showAlert
import com.example.vehiclecontractor.databinding.FragmentHomeBinding
import com.example.vehiclecontractor.ui.ClientRequest.SendCustomerInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnQrScanner.setOnClickListener {
           val scanner = HomeFragmentDirections.actionNavHomeToQRCodeScanner2()
           findNavController().navigate(scanner)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
