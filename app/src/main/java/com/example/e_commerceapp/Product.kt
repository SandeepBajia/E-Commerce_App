package com.example.e_commerceapp

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import java.net.URL
@IgnoreExtraProperties
data class  Product  constructor(val imageUrl : String ? = null,
                    val productName : String? = null,
                    val productPriceReal : String? = null,
                    val discountProductPrice : String? = null,
                    val percentageOff : String? = null ,
                    val productStatus : String? = null ,
                    val productDescription :String? = null ,
                    val sellerId:String? = null,
                    val userId:String? = null,
                    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(productName)
        parcel.writeString(productPriceReal)
        parcel.writeString(discountProductPrice)
        parcel.writeString( percentageOff)
        parcel.writeString(productStatus)
        parcel.writeString(productDescription)
        parcel.writeString(sellerId)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}

