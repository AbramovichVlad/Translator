package com.pet.app.translator.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.pet.app.translator.ui.BaseActivity

open class BasePermission(val context: Context) {
    val permissionList = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var permissionStringContract: ActivityResultLauncher<Array<String>>? = null
    var permissionIntentContract: ActivityResultLauncher<Intent>? = null
    var listener: ((Boolean) -> Unit) = {}
    var listenerUri: ((Uri) -> Unit) = {}

    fun initStringContract(baseActivity: BaseActivity<*>) {
        permissionStringContract =
            baseActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                var allPermissionGranted = true
                it.entries.forEach {
                    val isGranted = it.value
                    if (!isGranted) allPermissionGranted = isGranted
                }
                listener.invoke(allPermissionGranted)
            }
    }

    fun initIntentContract(baseActivity: BaseActivity<*>) {
        permissionIntentContract =
            baseActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                listener.invoke(checkPermission())
            }
    }

    fun getIntentRequestApiR(): Intent {
        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        return intent
    }

    fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            checkMultiple(permissionList)
        }
    }

    private fun checkMultiple(listPermission: Array<String>): Boolean {
        listPermission.forEach {
            if (!checkSingle(it)) return false
        }
        return true
    }

    private fun checkSingle(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

}