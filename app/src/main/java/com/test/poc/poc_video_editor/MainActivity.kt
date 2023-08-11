package com.test.poc.poc_video_editor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.test.poc.poc_video_editor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // data-binding
        val layoutInflater = LayoutInflater.from(this)
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.activity_main,null,false)
        // TODO : INIT

       setContentView(binding.root)
    }
}