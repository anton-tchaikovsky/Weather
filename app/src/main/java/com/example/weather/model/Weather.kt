package com.example.weather.model

import android.os.Parcel
import android.os.Parcelable

data class Weather(
    val city: City = getDefaultCity(),
    val temperature: Int = 15,
    val feelsLike: Int = 9): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(City::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(city, flags)
        parcel.writeInt(temperature)
        parcel.writeInt(feelsLike)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Weather> {
        override fun createFromParcel(parcel: Parcel): Weather {
            return Weather(parcel)
        }

        override fun newArray(size: Int): Array<Weather?> {
            return arrayOfNulls(size)
        }
    }
}
