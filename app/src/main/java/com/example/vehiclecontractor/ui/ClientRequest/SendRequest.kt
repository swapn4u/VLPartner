package com.example.vehiclecontractor.ui.ClientRequest

import android.os.Parcel
import android.os.Parcelable
data class SendCustomerInfo (
    val dealerName : String,
    val dealerNumber : String,
    val partnerName : String,
    val partnerNumber : String,
    val partnerAddress: String,
    val partnerPaymentId: String,
    val customerName: String,
    val customerNo: String,
    val vehicleBrand: String,
    val vehicleModel: String,
    var currentStatus: String,
    var updateDate: String,
    var transactionReference: String

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dealerName)
        parcel.writeString(dealerNumber)
        parcel.writeString(partnerName)
        parcel.writeString(partnerNumber)
        parcel.writeString(partnerAddress)
        parcel.writeString(partnerPaymentId)
        parcel.writeString(customerName)
        parcel.writeString(customerNo)
        parcel.writeString(vehicleBrand)
        parcel.writeString(vehicleModel)
        parcel.writeString(currentStatus)
        parcel.writeString(updateDate)
        parcel.writeString(transactionReference)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<SendCustomerInfo> {
        override fun createFromParcel(parcel: Parcel): SendCustomerInfo {
            return SendCustomerInfo(parcel)
        }

        override fun newArray(size: Int): Array<SendCustomerInfo?> {
            return arrayOfNulls(size)
        }
    }
}



