package com.example.vehiclecontractor

import android.Manifest
import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.vehiclecontractor.databinding.ActivityMainBinding
import com.example.vehiclecontractor.R.navigation
import androidx.core.app.ActivityCompat
import android.os.Build

import android.content.DialogInterface
import android.content.SharedPreferences

import android.content.pm.PackageManager
import android.widget.Button
import android.widget.TextView

import androidx.core.content.ContextCompat

import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import com.example.vehiclecontractor.Extentions.PartnerInfo
import com.example.vehiclecontractor.Extentions.ownnerNumber
import com.example.vehiclecontractor.Extentions.partnerEmail
import com.example.vehiclecontractor.Extentions.partnerName
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val  drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        val navGraph = navController.navInflater.inflate(navigation.mobile_navigation)
        val mPrefs: SharedPreferences = this.getPreferences(MODE_PRIVATE)
        val loggedInUser = mPrefs.getBoolean("isLoggedInUser",false)
        if (loggedInUser) {
            navGraph.setStartDestination( R.id.nav_home)
        } else {
            navGraph.setStartDestination( R.id.loginFragment)
        }
        navController.graph = navGraph

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.myReferences, R.id.loginFragment
            ), drawerLayout
        )
        requestPermission()
        fetchPartnerDetails()
        showOwnerInfo()
        manageMenuActions()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    fun saveRecords(partner: PartnerInfo){
        val mPrefs: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        mPrefs.edit().putString("contractorNum", partner.dealer).commit()
        mPrefs.edit().putBoolean("isLoggedInUser", true).commit()
        mPrefs.edit().putString("paymentId", partner.paymentID).commit()
        mPrefs.edit().putString("name", partner.name).commit()
        mPrefs.edit().putString("emailId", partner.emailID).commit()
        showOwnerInfo()
    }

    fun fetchPartnerDetails() {
        if (ownnerNumber().isNotEmpty()) {
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val databaseReference =
                firebaseDatabase.getReference("PartnersRecord/${ownnerNumber()}")

            databaseReference
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dealer: DataSnapshot) {
                        if (dealer.exists()) {
                            val json = Gson().toJson(dealer.value)
                            val dealerInfo = Gson().fromJson(json, PartnerInfo::class.java)
                            saveRecords(dealerInfo)
                        } else {
                            print("Not Exist")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        showMessageOKCancel("You need to allow access permissions",
                            DialogInterface.OnClickListener { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermission()
                                }
                            })
                    }
                }
            }
        }
        if (permissions != null) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showOwnerInfo() {
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.ownerName).text = partnerName()
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.emailId).text = partnerEmail()
    }

    fun manageMenuActions() {
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.loginFragment -> {
                    val mPrefs: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
                    mPrefs.edit().putBoolean("isLoggedInUser", false).commit()
                    true
                }
                else -> {
                    false
                }
            }

        }
    }
}