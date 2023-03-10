package com.pet.app.translator.file

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.pet.app.translator.R
import com.pet.app.translator.ui.BaseActivity

class FilePicker(private val context: Context) {
    private var startActivityForResult: ActivityResultLauncher<Intent>? = null

    private var listener: ((Uri) -> Unit) = {}


    //must init in Activity onCreate
    fun init(baseActivity: BaseActivity<*>) {
        startActivityForResult =
            baseActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data;
                    val contentUri = data?.data
                    contentUri?.let { it1 -> listener.invoke(it1) }
                }
            }
    }

    private fun getIntentPickFile(): Intent? {
        val data = Intent(Intent.ACTION_GET_CONTENT)
        data.addCategory(Intent.CATEGORY_OPENABLE)
        data.type = FILE_TYPE_XML
        return Intent.createChooser(data, context.getString(R.string.txt_chose_file))
    }

    fun pickFile(action: (Uri) -> Unit) {
        listener = action
        val intentPick = getIntentPickFile()
        startActivityForResult?.launch(intentPick)
    }

    fun pickFolderForSave(fileName: String) {

    }

    companion object {
        private const val FILE_TYPE_XML = "text/xml"
    }
}