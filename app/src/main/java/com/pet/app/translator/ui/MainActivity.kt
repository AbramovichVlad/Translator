package com.pet.app.translator.ui

import android.content.Intent
import com.pet.app.translator.databinding.ActivityMainBinding
import com.pet.app.translator.parser.XmlCreator
import com.pet.app.translator.parser.XmlParser
import com.pet.app.translator.permission.Permission
import com.pet.app.translator.translator.Translator


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val translator by lazy { Translator() }
    private val xmlCreator by lazy { XmlCreator(this) }
    private val permission by lazy { Permission(this) }


    override fun initData() {
        super.initData()
        filePicker.init(this)
        permission.init(this)
    }

    override fun initView() {
        super.initView()

        binding.btnGrantPermission.setPermissionState(permission.checkPermission())


        binding.btnChoseFile.setOnClickListener {
            filePicker.pickFile { list ->
                XmlParser(this).parseXml(list) {
                    xmlCreator.createXml(it)
                }
            }
        }

        binding.btnGrantPermission.setOnClickListener {
            permission.checkAndRequest {
                binding.btnGrantPermission.setPermissionState(permission.checkPermission())
            }
        }

        binding.btnSelectSaveFolder.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "file/*"
            startActivityForResult(intent, 111)
        }

    }

}