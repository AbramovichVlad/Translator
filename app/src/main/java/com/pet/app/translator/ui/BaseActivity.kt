package com.pet.app.translator.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.pet.app.translator.file.FilePicker

abstract class BaseActivity<ViewBind : ViewBinding>(val bindingFactory: (LayoutInflater) -> ViewBind) :
    AppCompatActivity() {

    lateinit var binding: ViewBind
    val filePicker by lazy { FilePicker(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
    }

    open fun initData(){
    }

    open fun initView(){
    }
}