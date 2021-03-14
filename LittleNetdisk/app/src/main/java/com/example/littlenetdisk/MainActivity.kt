package com.example.littlenetdisk

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var imageList=ArrayList<com.example.littlenetdisk.Image>()
    private var folderList=ArrayList<Folder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //初始化 界面
        val prefs =getSharedPreferences("LogActivity",Context.MODE_PRIVATE)
        val result=prefs.getString("cookie","")
        val fatherFid=prefs.getString("fid","")
        if(result==""||fatherFid==""||result==null||fatherFid==null)
            Toast.makeText(this,"信息错误",Toast.LENGTH_SHORT).show()
        else{
            QueryUtil.sendQueryFolderRequest(result,fatherFid,object :okhttp3.Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Toast.makeText(this@MainActivity,"请求错误",Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        try {
                            val typeOf=object :TypeToken<ArrayList<Folder>>() {}.type
                            folderList= Gson().fromJson<ArrayList<Folder>>(responseData,typeOf)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }else{
                        Toast.makeText(this@MainActivity,"wu",Toast.LENGTH_SHORT).show()
                    }
                }

            })
            QueryUtil.sendQueryImagesRequest(result,fatherFid,object :okhttp3.Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Toast.makeText(this@MainActivity,"请求错误",Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        Log.d("tag",responseData)
                        //Toast.makeText(this@MainActivity,responseData,Toast.LENGTH_SHORT).show()
                        try {
                            val typeOf=object :TypeToken<ArrayList<com.example.littlenetdisk.Image>>() {}.type
                            imageList= Gson().fromJson<ArrayList<com.example.littlenetdisk.Image>>(responseData,typeOf)
                            imageList.sortWith(ImageComparator())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }else{
                        Toast.makeText(this@MainActivity,"wu",Toast.LENGTH_SHORT).show()
                    }
                }

            })
            //加载图片
            try {
                Thread.sleep(3000)
            }catch (e:Exception){
                e.printStackTrace()
            }
            val imageLayoutManger=LinearLayoutManager(this)
            fileRecyclerView.layoutManager=imageLayoutManger
            val imageAdapter=ImageAdapter(this,imageList)
            fileRecyclerView.adapter=imageAdapter
            //加载文件夹
            val folderLayoutManger=LinearLayoutManager(this)
            folderRecyclerView.layoutManager=folderLayoutManger
            val folderAdapter=FolderAdapter(this,folderList,imageList,imageAdapter)
            folderRecyclerView.adapter=folderAdapter
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            val home=Intent(Intent.ACTION_MAIN)
            home.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            home.addCategory(Intent.CATEGORY_HOME)
            startActivity(home)
        }
        return super.onKeyDown(keyCode, event)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
        val searchView=(menu?.findItem(R.id.search))?.actionView as SearchView
        searchView.isSubmitButtonEnabled=true
        searchView.imeOptions=EditorInfo.IME_ACTION_SEARCH
        searchView.isIconified=false
        searchView.isIconifiedByDefault=true
        searchView.focusable= View.FOCUSABLE
        searchView.queryHint="请输入关键字"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //搜索代码(联系后端数据库查找符合的内容并显示)
                if(query==null||query==""){
                    Toast.makeText(this@MainActivity,"搜索内容不能为空",Toast.LENGTH_SHORT).show()
                    searchView.focusable=View.NOT_FOCUSABLE
                    return false
                }
                val resultList=ArrayList<com.example.littlenetdisk.Image>()
                for(image in resultList){
                    if(image.name.startsWith(query)){
                        resultList.add(image)
                    }
                }
                //清除焦点
                searchView.focusable=View.NOT_FOCUSABLE
                val intent=Intent(this@MainActivity,SearchResultActivity::class.java)
                intent.putExtra("result",resultList)
                startActivity(intent)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.out->{
                AlertDialog.Builder(this).apply {
                    setTitle("确定退出登录？")
                    setMessage("退出后再次登录需要验证密码")
                    setCancelable(true)
                    setPositiveButton("是"){ _,_->
                        val prefs =getSharedPreferences("LogActivity",Context.MODE_PRIVATE)
                        prefs.edit().clear().apply()
                        finish()
                    }
                    setNegativeButton("否"){ _,_->
                    }
                    show()
                }
            }
            R.id.add->{
                //上传文件
                val intent=Intent(this,UploadActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

}