package com.example.littlenetdisk

import android.app.DownloadManager
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

object UploadUtil {
    /*private const val charset:String="utf-8"
    private const val timeOut=10*1000
    private val boundary=UUID.randomUUID().toString()
    private const val prefix="--"
    private const val lineEnd="\r\n"
    private const val CONTENT_TYPE="multipart/form-data"*/
    private const val requestUrl = "http://37x366u444.wicp.vip/OnlineAlbum_Jessie_war/image/upload"

    /*fun uploadImage(context: Context,fid: Int, file: File, requestUrl: String): Int{
        var res=0
        var result=""
        try {
            Log.d("tag","into load")
            Toast.makeText(context,"into load", Toast.LENGTH_SHORT).show()
            val url= URL(requestUrl)
            val conn=url.openConnection() as HttpURLConnection
            conn.readTimeout= timeOut
            conn.connectTimeout= timeOut
            conn.doInput=true
            conn.doOutput=true
            conn.useCaches=false
            conn.requestMethod="POST"
            conn.setRequestProperty("Charset", charset)
            conn.setRequestProperty("Connection", "keep-alive")
            conn.setRequestProperty("Content-Type", "$CONTENT_TYPE;boundary=$boundary")
            Log.d("tag","conn is successful")
            Toast.makeText(context,"conn is successful", Toast.LENGTH_SHORT).show()
            if (file!=null){
                Log.d("tag","file is not null")
                Toast.makeText(context,"file is not null", Toast.LENGTH_SHORT).show()
                val dos=DataOutputStream(conn.outputStream)
                val params= arrayOf("fid","upload")
                val values= arrayOf(fid.toString(),file.name)
                for (i in params.indices){
                    var sb=StringBuffer()
                    sb.append(prefix)
                    sb.append(boundary)
                    sb.append(lineEnd)
                    sb.append("Content-Disposition: form-data; name=\"${params[i]}\"; fid=\"${values[i]}\"")
                    sb.append(lineEnd)
                    sb.append(lineEnd)
                    dos.write(sb.toString().toByteArray())
                }

                val fis=FileInputStream(file)
                val bytes = ByteArray(1024)
                var len = 0
                while(len!=-1){
                    len=fis.read(bytes)
                    if(len==-1) break
                    dos.write(bytes, 0, len)
                }
                fis.close()
                dos.write(lineEnd.toByteArray())
                val dataEnd=(prefix+boundary+prefix+lineEnd).toByteArray()
                dos.write(dataEnd)
                dos.flush()
                res=conn.responseCode
                Log.d("tag","response code:$res")
                if(res==200){
                    Log.d("tag","request success")
                    val input=conn.inputStream
                    var sb1=StringBuffer()
                    var ss=0
                    while(ss!=-1){
                        ss=input.read()
                        if(len==-1) break
                        sb1.append(ss as Char)
                    }
                    result=sb1.toString()
                    Log.d("tag","result: $result")
                }else{
                    Log.d("tag","request error")
                }
            }
        }catch (e: MalformedURLException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }
        // Toast.makeText(, "res=$res", Toast.LENGTH_SHORT)
        return res
    }*/

    fun uploadImageFile(context: Context,result:String,fid: String, imageFile: File,callback:Callback){
        var mediaType=""
        if(imageFile.name.contains("png")||imageFile.name.contains("PNG"))
            mediaType="image/png"
        if(imageFile.name.contains("jpg")||imageFile.name.contains("JPG"))
            mediaType="image/jpg"
        if(imageFile.name.contains("jpeg")||imageFile.name.contains("JPEG"))
            mediaType="image/jpeg"
        val client=OkHttpClient()
        val file=File(imageFile.absolutePath)
        val fileBody= file.asRequestBody(mediaType.toMediaTypeOrNull())
        val requestBody=MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("upload",imageFile.name,fileBody)
            .addFormDataPart("fid", fid)
            .build()
        val request=Request.Builder().url(requestUrl).addHeader("Cookie",result).post(requestBody).build()
        client.newCall(request).enqueue(callback)
    }
}