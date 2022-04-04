package com.example.vehiclecontractor.Extentions

import android.os.Parcel
import android.os.Parcelable
import com.example.vehiclecontractor.ui.ClientRequest.SendCustomerInfo
import com.google.gson.annotations.SerializedName

data class Vehical(
    val brand: String,
    val model: List<String>
)


data class PartnerInfo (
    val dealer: String,
    val paymentID: String,
    val name: String,
    val emailID: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dealer)
        parcel.writeString(paymentID)
        parcel.writeString(name)
        parcel.writeString(emailID)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<PartnerInfo> {
        override fun createFromParcel(parcel: Parcel): PartnerInfo {
            return PartnerInfo(parcel)
        }

        override fun newArray(size: Int): Array<PartnerInfo?> {
            return arrayOfNulls(size)
        }
    }
}