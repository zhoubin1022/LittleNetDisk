package com.example.littlenetdisk

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

object QueryUtil {
    private const val requestFoldersUrl = "http://37x366u444.wicp.vip/OnlineAlbum_Jessie_war/folder/getCurrentFolders"
    private const val requestImagesUrl = "http://37x366u444.wicp.vip/OnlineAlbum_Jessie_war/folder/getCurrentImages"

    fun sendQueryFolderRequest(result:String,fid:String,callback:okhttp3.Callback){
        val client= OkHttpClient()
        val requestBody= FormBody.Builder().add("fid",fid).build()
        val request= Request.Builder().url(requestFoldersUrl).addHeader("Cookie",result).post(requestBody).build()
        client.newCall(request).enqueue(callback)
    }

    fun sendQueryImagesRequest(result:String,fid:String,callback:okhttp3.Callback){
        val client= OkHttpClient()
        val requestBody= FormBody.Builder().add("fid",fid).build()
        val request= Request.Builder().url(requestImagesUrl).addHeader("Cookie",result).post(requestBody).build()
        client.newCall(request).enqueue(callback)
    }
}