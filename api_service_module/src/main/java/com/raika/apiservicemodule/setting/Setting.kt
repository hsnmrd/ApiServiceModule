package com.raika.apiservicemodule.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SharedPreference {
    
    private const val noInternetMessage = "no_internet_message"
    
    fun preference(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    
    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }
    
    var SharedPreferences.apiServiceModuleNoInternetMessage
        get() = getString(noInternetMessage, "اطلاعات آفلاین")
        set(value) {
            editMe { it.putString(noInternetMessage, value) }
        }
    
}

fun apiServiceModuleGetPreference(context: Context?): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)

