package com.example.littlenetdisk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Response
import okhttp3.internal.notify
import org.json.JSONException
import java.io.IOException

class FolderAdapter(private val context: MainActivity, private var folderList:ArrayList<Folder>,private var imageList:ArrayList<Image>,private var adapter: ImageAdapter): RecyclerView.Adapter<FolderAdapter.ViewHolder>()  {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val fileImage: ImageView =view.findViewById(R.id.fileIcon)
        val folderName: TextView =view.findViewById(R.id.fileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.view_item,parent,false)
        val holder=ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position=holder.adapterPosition
            val  file:Folder=folderList[position]
            val prefs=context.getSharedPreferences("LogActivity",Context.MODE_PRIVATE)
            val result=prefs.getString("cookie","")
            var fFid = if(position==0){
                Toast.makeText(context,"返回上一级",Toast.LENGTH_SHORT).show()
                file.father
            }else{
                Toast.makeText(context,"打开下一级",Toast.LENGTH_SHORT).show()
                file.fid
            }
            if(result!=null){
                QueryUtil.sendQueryFolderRequest(result,fFid.toString(),object :okhttp3.Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        Toast.makeText(context,"请求错误",Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call, response: Response) {
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            try {
                                val typeOf=object : TypeToken<ArrayList<Folder>>() {}.type
                                folderList= Gson().fromJson<ArrayList<Folder>>(responseData,typeOf)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }

                })
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                QueryUtil.sendQueryImagesRequest(result,fFid.toString(),object :okhttp3.Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        Toast.makeText(context,"请求错误",Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            try {
                                val typeOf=object :TypeToken<ArrayList<Image>>() {}.type
                                imageList= Gson().fromJson(responseData,typeOf)
                                //imageList.sortWith(ImageComparator())
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }

                })
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                notifyDataSetChanged()
                adapter.notifyDataSetChanged()
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageFolder=folderList[position]
        holder.folderName.text=imageFolder.folderName
        Glide.with(context).load(R.drawable.file_add_btn_folder).into(holder.fileImage)
    }

    override fun getItemCount()=folderList.size
}