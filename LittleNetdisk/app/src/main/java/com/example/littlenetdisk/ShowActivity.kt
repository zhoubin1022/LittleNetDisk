package com.example.littlenetdisk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_show.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat

class ShowActivity : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)
        val intent=Intent()
        val bitmap=intent.getParcelableExtra<Bitmap>("bitmap")
        val path=intent.getStringExtra("path")
        val type=intent.getStringExtra("type")
        var format:Bitmap.CompressFormat?=null
        if(type=="jpg"||type=="jpeg"){
            format=Bitmap.CompressFormat.JPEG
        }else if(type=="png"){
            format=Bitmap.CompressFormat.PNG
        }else{ }
        downloadToSD.setOnClickListener {
            val file= File(path)
            try {
                file.createNewFile()
            }catch (e:Exception){
                Toast.makeText(this,"保存图片出错",Toast.LENGTH_SHORT).show()
            }
            val fos=FileOutputStream(file)
            bitmap?.compress(format,100,fos)
            try{
                fos.flush()
            }catch (e:Exception){
                e.printStackTrace()
            }
            try{
                fos.close()
            }catch (e:Exception){
                e.printStackTrace()
            }
            findViewById<TextView>(R.id.downloadPath).text=path
            findViewById<TextView>(R.id.downloadTime).text=SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(file.lastModified())
            Toast.makeText(this,"图片下载成功",Toast.LENGTH_SHORT).show()
        }
    }
}