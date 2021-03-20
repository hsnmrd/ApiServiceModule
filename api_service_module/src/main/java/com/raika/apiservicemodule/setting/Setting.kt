package com.raika.apiservicemodule.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SharedPreference {
    
    private const val noInternetMessage = "no_internet_message"
    private const val ignoreRequestTitle = "ignore_request_title"
    private const val ignoreRequestChoiceTitle = "ignore_request_choice_title"
    
    fun preference(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    
    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }
    
    var SharedPreferences.apiServiceModuleSettingNoInternetMessage
        get() = getString(noInternetMessage, "اطلاعات آفلاین")
        set(value) {
            editMe { it.putString(noInternetMessage, value) }
        }
    
    var SharedPreferences.apiServiceModuleSettingIgnoreRequestTitle
        get() = getString(ignoreRequestTitle, "آیا مطمئن به قطع درخواست هستید")
        set(value) {
            editMe { it.putString(ignoreRequestTitle, value) }
        }
    
    var SharedPreferences.apiServiceModuleSettingIgnoreRequestChoiceTitle
        get() = getString(ignoreRequestChoiceTitle, "قطع درخواست")
        set(value) {
            editMe { it.putString(ignoreRequestChoiceTitle, value) }
        }
    
}

fun apiServiceModuleGetPreference(context: Context?): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)

