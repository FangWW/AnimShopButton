package com.w.animshopbutton

import android.os.Parcel
import android.os.Parcelable

/**
 * @Author: FangJW
 * @Date: 2018/8/3 21:06
 */
data class MenuBean(var id: Int, var amount: Int, var name: String, var money: Int, var isMultiSpe: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(amount)
        parcel.writeString(name)
        parcel.writeInt(money)
        parcel.writeByte(if (isMultiSpe) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MenuBean> {
        override fun createFromParcel(parcel: Parcel): MenuBean {
            return MenuBean(parcel)
        }

        override fun newArray(size: Int): Array<MenuBean?> {
            return arrayOfNulls(size)
        }
    }
}