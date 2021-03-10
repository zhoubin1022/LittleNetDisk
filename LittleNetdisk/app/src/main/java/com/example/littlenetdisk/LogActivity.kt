package com.example.littlenetdisk

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_log.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.StringBuilder
import java.net.URL
import javax.security.auth.callback.Callback
import kotlin.concurrent.thread
//主activity
class LogActivity : AppCompatActivity() {
    var result=""
    var headers:Headers?=null
    var fid:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        supportActionBar?.hide()
        val prefs =getPreferences(Context.MODE_PRIVATE)
        val isRemember=prefs.getBoolean("remember",false)
        if(isRemember){
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        logIn.setOnClickListener {
            var passwordCorrect = false
            val userName = username.text.toString()
            val passWord = password.text.toString()
            HttpUtil.sendHttpRequest(userName, passWord, object : okhttp3.Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        try {
                            val jsonObject = JSONObject(responseData)
                            passwordCorrect = jsonObject.getBoolean("status")
                            fid=jsonObject.getInt("data").toString()
                            if(passwordCorrect){
                                headers=response.headers
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (passwordCorrect&&HttpUtil.rUrl!=null&&headers!=null) {//密码正确
                //Toast.makeText(this,"rUrl=${HttpUtil.rUrl.toString()}",Toast.LENGTH_SHORT).show()
                //Toast.makeText(this,"headers=${headers.toString()}",Toast.LENGTH_SHORT).show()
                val cookies=Cookie.parseAll(HttpUtil.rUrl!!, headers!!)
                var cookieStr=StringBuilder()
                for(cookie in cookies){
                    cookieStr.append(cookie.name).append("=").append(cookie.value+";")
                }
                result=cookieStr.toString()
                val prefs = getPreferences(Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean("remember", true)
                editor.putString("userName", userName)
                editor.putString("passWord", passWord)
                editor.putString("cookie",result)
                editor.putString("fid",fid)
                editor.apply()
                //Toast.makeText(this,"result=$result",Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show()
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show()
            }
        }
        signIn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs =getPreferences(Context.MODE_PRIVATE)
        val isRemember=prefs.getBoolean("remember",false)
        if(isRemember){
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val newUser = data?.getSerializableExtra("user") as Users
                val passWord = newUser.password
                val userName = newUser.userName
                val prefs = getPreferences(Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean("remember", true)
                editor.putString("userName", userName)
                editor.putString("passWord", passWord)
                editor.apply()
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}