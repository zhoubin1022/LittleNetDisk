package com.example.littlenetdisk

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.time.LocalDateTime

class Image() :Serializable,Parcelable{
    var imageid:Int=-1
    var name:String=""
    var username:String=""
    var visited:Int=-1
    var album:String=""
    var path:String=""
    var uploadTime:LocalDateTime?=null
    var size:Long=-1
    var fid:Int=-1

    constructor(parcel: Parcel) : this() {
        imageid = parcel.readInt()
        name = parcel.readString().toString()
        username = parcel.readString().toString()
        visited = parcel.readInt()
        album = parcel.readString().toString()
        path = parcel.readString().toString()
        size = parcel.readLong()
        fid = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(imageid)
        parcel.writeString(name)
        parcel.writeString(username)
        parcel.writeInt(visited)
        parcel.writeString(album)
        parcel.writeString(path)
        parcel.writeLong(size)
        parcel.writeInt(fid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}
