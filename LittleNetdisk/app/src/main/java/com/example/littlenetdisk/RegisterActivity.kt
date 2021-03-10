package com.example.littlenetdisk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.Call
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.concurrent.thread

class RegisterActivity : AppCompatActivity() {
    var registerCorrect=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerButton.setOnClickListener {
            val newUserName=new_username.text.toString().trim()
            val newPassword=new_password.text.toString().trim()
            if(newUserName.isEmpty()){
                Toast.makeText(this,"账号不能为空",Toast.LENGTH_SHORT).show()
                findViewById<EditText>(R.id.new_username).requestFocus()
            }else if (newPassword.isEmpty()){
                Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show()
                findViewById<EditText>(R.id.new_password).requestFocus()
            }else{
                    //注册
                    HttpUtil.sendRegisterRequest(newUserName,newPassword,object :okhttp3.Callback{
                        override fun onFailure(call: Call, e: IOException) {
                            //Toast.makeText(this@RegisterActivity,"请求错误",Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val responseData=response.body?.string()
                            try {
                                val jsonObject= JSONObject(responseData)
                                registerCorrect = jsonObject.getBoolean("status")
                                val text=jsonObject.getString("data")
                                Log.d("Activity", text)
                                //Toast.makeText(this@RegisterActivity,text,Toast.LENGTH_SHORT).show()
                            }catch (e: JSONException){
                                //Toast.makeText(this@RegisterActivity,"JSONException",Toast.LENGTH_SHORT).show()
                                Log.d("Activity", "JSONException")
                                e.printStackTrace()
                            }
                        }
                    })
                    try {
                        Thread.sleep(2000)
                    }catch (e:InterruptedException){
                        e.printStackTrace()
                    }
                    if(registerCorrect){
                        val intent=Intent()
                        intent.putExtra("user",Users(newUserName,newPassword))
                        setResult(RESULT_OK,intent)
                        Toast.makeText(this,"注册成功",Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        //注册遇到问题
                        Toast.makeText(this,"注册失败",Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}