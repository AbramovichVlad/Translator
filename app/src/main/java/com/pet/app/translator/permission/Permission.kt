package com.pet.app.translator.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.pet.app.translator.R
import com.pet.app.translator.ui.BaseActivity


class Permission(context: Context) : BasePermission(context) {

    //must init in Activity onCreate
    fun init(baseActivity: BaseActivity<*>) {
       initStringContract(baseActivity)
       initIntentContract(baseActivity)
    }

    fun checkAndRequest(listener: (Boolean) -> Unit) {
        if (checkPermission()) listener.invoke(true)
        else requestPermission(listener)
    }

    private fun requestPermission(listener: (Boolean) -> Unit) {
        this.listener = listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) requestApiR()
        else {
        }
    }

    private fun requestApiR() {
        val intent = getIntentRequestApiR()
        permissionIntentContract?.launch(intent)
    }

    private fun requestBeforeR(){
        permissionStringContract?.launch(permissionList)
    }
}