package com.suya.binderservice

import android.os.Parcel
import android.os.Parcelable


/**
 * Created by suya on 2020/10/10.
 * email:suyahe828@gmail.com
 */
data class Book(val bookId: Int, val bookName: String) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(bookId)
        writeString(bookName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Book> = object : Parcelable.Creator<Book> {
            override fun createFromParcel(source: Parcel): Book = Book(source)
            override fun newArray(size: Int): Array<Book?> = arrayOfNulls(size)
        }
    }
}
