package com.raika.apiservicemodule.shared_prefence

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SharedPreference {

    private const val token = "TOKEN"

    fun preference(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    
    private inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.spToken
        get() = getString(token, "null")
        set(value) {
            editMe { it.putString(token, value) }
        }
}

fun getPreference(context: Context?): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)

