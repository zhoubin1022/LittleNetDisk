package com.example.littlenetdisk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        val intent= Intent()
        val resultList=intent.getParcelableArrayListExtra<Image>("result")
        val imageLayoutManger= LinearLayoutManager(this)
        resultRecyclerView.layoutManager=imageLayoutManger
        val imageAdapter= resultList?.let { ImageAdapter(this, it) }
        resultRecyclerView.adapter=imageAdapter
    }
}