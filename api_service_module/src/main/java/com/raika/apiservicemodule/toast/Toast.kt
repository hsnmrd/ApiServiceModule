package com.raika.apiservicemodule.toast

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.raika.apiservicemodule.R

fun Context.apiServiceModuleToasting(message: String) {
    val font = ResourcesCompat.getFont(this, R.font.api_service_module_regular)
    val spannableString = SpannableString(message)
    spannableString.setSpan(font, 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    Toast.makeText(this, spannableString, Toast.LENGTH_SHORT).show()
}

fun Context.apiServiceModuleToastingLong(message: String) {
    val font = ResourcesCompat.getFont(this, R.font.api_service_module_regular)
    val spannableString = SpannableString(message)
    spannableString.setSpan(font, 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    Toast.makeText(this, spannableString, Toast.LENGTH_LONG).show()
}