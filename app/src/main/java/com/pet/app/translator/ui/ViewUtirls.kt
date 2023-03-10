package com.pet.app.translator.ui

import android.widget.Button
import com.pet.app.translator.R

fun Button.setPermissionState(isGranted : Boolean){
    val textRes = if(isGranted) R.string.txt_permission_granted
    else R.string.txt_grant_permission

    this.text = context.getString(textRes)
    this.isEnabled = !isGranted

}