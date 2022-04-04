package com.example.vehiclecontractor.ui.Login

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.example.vehiclecontractor.Extentions.*
import com.example.vehiclecontractor.MainActivity
import com.example.vehiclecontractor.R
import com.example.vehiclecontractor.databinding.LoginFragmentBinding
import com.example.vehiclecontractor.ui.ClientRequest.SendCustomerInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class LoginFragment : Fragment() {
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //
        val mPrefs: SharedPreferences = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
        mPrefs.edit().putBoolean("isLoggedInUser", false).commit()
        auth = Firebase.auth
        //
        binding.btnLogin.setOnClickListener {
            if (!isActiveInternet()) {
                showAlert("Please check your internet connections")
                return@setOnClickListener
            }
            validateLogin()
        }
        return root
    }
    override fun onStart() {
        super.onStart()
        Firebase.auth.signOut()
    }

    override fun onResume() {
        super.onResume()
        (this.requireContext() as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (this.requireContext() as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    private fun validateLogin() {
        if (binding.loginId.text.isEmpty()) {
            showAlert("Please enter your login ID")
            return
        }
        if (binding.password.text.isEmpty()) {
            showAlert("Please enter valid password")
            return
        }
        auth.signInWithEmailAndPassword(binding.loginId.text.toString(), binding.password.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showAlert("Wel-come to Vasu Automobiles.")
                    val ownerNumber = binding.loginId.text.toString().split("@").first()
                    val mPrefs: SharedPreferences = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
                    mPrefs.edit().putString("ownerNumber", ownerNumber).commit()
                    mPrefs.edit().putBoolean("isLoggedInUser", true).commit()
                    (this.requireActivity() as MainActivity).fetchPartnerDetails()
                    this.view?.let { Navigation.findNavController(it).navigate(R.id.action_login_to_home) }
                } else {
                    showAlert("Please enter valid email id or password.")
                }
            }
    }
}