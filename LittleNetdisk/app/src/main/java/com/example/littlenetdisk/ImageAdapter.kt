package com.example.littlenetdisk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.UrlQuerySanitizer
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class ImageAdapter(private val context: Activity, private val imageList:List<Image>):RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val fileImage:ImageView=view.findViewById(R.id.fileIcon)
        val name:TextView=view.findViewById(R.id.fileName)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.view_item,parent,false)
        val holder=ViewHolder(view)
        //val layout=LayoutInflater.from(context).inflate(R.layout.activity_show,parent,false)
        holder.itemView.setOnClickListener {
            Toast.makeText(context,"点击图片操作",Toast.LENGTH_SHORT).show()
            val position=holder.adapterPosition
            val file:Image=imageList[position]
            val path=Environment.getExternalStorageDirectory().path+"/${file.name}"
            val absolutePath="D:/TomcatImg/"+file.username+file.path+file.name
            //Toast.makeText(context,"path=$path,uploadName=${file.username},imageName=${file.name}",Toast.LENGTH_SHORT).show()
            //val newFile= File(path)
            //val dateFormat=SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
            Toast.makeText(context,"absolutePath=$absolutePath",Toast.LENGTH_SHORT).show()
            var bitmap:Bitmap?=null
            //val uploadTime=file.uploadTime.toString()
            //Toast.makeText(context,"点击图片操作",Toast.LENGTH_SHORT).show()
            /*if(newFile.exists()){
                val bm=BitmapFactory.decodeFile(path)
                layout.findViewById<TextView>(R.id.downloadPath).text=path
                layout.findViewById<ImageView>(R.id.imageShow).setImageBitmap(bm)
                layout.findViewById<TextView>(R.id.downloadTime).text=newFile.lastModified().toString()
                    //dateFormat.parse(newFile.lastModified().toString()).toString()
                layout.findViewById<Button>(R.id.downloadToSD).visibility=Button.INVISIBLE
            }else{*/
                //从服务器下载图片
            //Toast.makeText(context,"imageid=${file.imageid},visited=${file.visited},album=${file.album},path=${file.path},size=${file.size},fid=${file.fid}",Toast.LENGTH_LONG).show()
            try {
                val url=URL(absolutePath)
                val conn=url.openConnection() as HttpURLConnection
                conn.doInput=true
                conn.connect()
                val fis=conn.inputStream
                bitmap=BitmapFactory.decodeStream(fis)
                fis.close()
                if(bitmap==null) Toast.makeText(context,"查看图片失败",Toast.LENGTH_SHORT).show()
            /*context.runOnUiThread{
                       layout.findViewById<ImageView>(R.id.imageShow).setImageBitmap(bitmap)
                   }*/
            }catch (e:Exception){
                e.printStackTrace()
            }
            //}
            val intent = Intent(context, ShowActivity::class.java)
            /*var type=""
            if(file.name.endsWith(".jpg")||file.name.endsWith(".JPG"))
                type="jpg"
            if(file.name.endsWith(".png")||file.name.endsWith(".PNG"))
                type="png"
            if(file.name.endsWith(".jpeg")||file.name.endsWith(".JPEG"))
                type="jpeg"*/
            intent.putExtra("uploadName",file.username)
            intent.putExtra("imageName",file.name)
            intent.putExtra("bitmap",bitmap)
            intent.putExtra("path",path)
            //intent.putExtra("uploadTime",uploadTime)
            //intent.putExtra("type",type)
            context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageFile=imageList[position]
        holder.name.text=imageFile.name
        Glide.with(context).load(R.drawable.file_add_btn_photo).into(holder.fileImage)
    }

    override fun getItemCount()=imageList.size

}