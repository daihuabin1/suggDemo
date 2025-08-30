package com.example.mycalendertest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.RecyclerView


class MainActivity : ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_activity)

        initView()

    }

    private fun initView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val list = mutableListOf<String>("name","age","type")
        val adapter = SwipeAdapter(list)
        recyclerView.adapter = adapter

        // 设置item点击回调
        adapter.setOnItemClickListener { position, data ->
            android.widget.Toast.makeText(this, "点击了：$data", android.widget.Toast.LENGTH_SHORT).show()
        }
    }


}
