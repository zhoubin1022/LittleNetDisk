package com.example.littlenetdisk

import android.content.Context
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL
import javax.security.auth.callback.Callback

object HttpUtil {
    var rUrl:HttpUrl?=null
    fun sendHttpRequest(userName:String,password:String,callback:okhttp3.Callback){
        val url= URL("http://37x366u444.wicp.vip/OnlineAlbum_Jessie_war/user/login")
        val client=OkHttpClient()
        val requestBody=FormBody.Builder().add("username",userName).add("password",password).build()
        val request=Request.Builder().url(url).post(requestBody).build()
        rUrl=request.url
        client.newCall(request).enqueue(callback)
    }
    fun sendRegisterRequest(userName:String,password:String,callback:okhttp3.Callback){
        val url= URL("http://37x366u444.wicp.vip/OnlineAlbum_Jessie_war/user/Register")
        val client=OkHttpClient()
        val requestBody=FormBody.Builder().add("username",userName).add("password",password).build()
        val request=Request.Builder().url(url).post(requestBody).build()
        client.newCall(request).enqueue(callback)
    }
    /*fun sendIsLoginRequest(userName:String,password:String,callback:okhttp3.Callback){
        val url= URL("http://37x366u444.wicp.vip/OnlineAlbum_Jessie_war/user/isLogin")
        val client=OkHttpClient()
        val requestBody=FormBody.Builder().add("username",userName).add("password",password).build()
        val request=Request.Builder().url(url).post(requestBody).build()
        client.newCall(request).enqueue(callback)
    }*/
}