package com.example.vehiclecontractor.Extentions

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.vehiclecontractor.R
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.*

fun String.getDate(): Date {
    return SimpleDateFormat("dd/MMM/yyyy hh:mm a").parse(this)
}

fun Fragment?.runOnUiThread(action: () -> Unit) {
    this ?: return
    if (!isAdded) return // Fragment not attached to an Activity
    requireActivity().runOnUiThread(action)
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.isActiveInternet() : Boolean {
    val connectivityManager= requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo=connectivityManager.activeNetworkInfo
    return  networkInfo!=null && networkInfo.isConnected
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
fun Fragment.toast(info: String) {
    requireContext().toast(info)
}

fun Fragment.showAlert(message: String) {
    Alerter.create(requireActivity())
        .setTitle("Vasu Auto Mobiles")
        .setText(message)
        .setBackgroundColorInt(Color.BLACK)
        .setIcon(R.drawable.ic_bike)
        .show()
}

fun Activity.ownnerNumber(): String {
    val mPrefs: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
    val number = mPrefs.getString("ownerNumber","")
    return  number ?: ""
}
fun Activity.partnerName(): String {
    val mPrefs: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
    val number = mPrefs.getString("name","")
    return  number ?: ""
}
fun Activity.partnerEmail(): String {
    val mPrefs: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
    val number = mPrefs.getString("emailId","")
    return  number ?: ""
}
fun Fragment.ownnerNumber(): String {
    val mPrefs: SharedPreferences = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
    val number = mPrefs.getString("ownerNumber","")
    return  number ?: ""
}
fun Fragment.paymentID(): String {
    val mPrefs: SharedPreferences = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
    val number = mPrefs.getString("paymentId","")
    return  number ?: ""
}
fun Fragment.partnerName(): String {
    val mPrefs: SharedPreferences = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
    val number = mPrefs.getString("name","")
    return  number ?: ""
}
fun Fragment.partnerEmail(): String {
    val mPrefs: SharedPreferences = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
    val number = mPrefs.getString("emailId","")
    return  number ?: ""
}

fun Fragment.contracorNo(): String {
    val mPrefs: SharedPreferences = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
    val number = mPrefs.getString("contractorNum","")
    return  number ?: ""
}