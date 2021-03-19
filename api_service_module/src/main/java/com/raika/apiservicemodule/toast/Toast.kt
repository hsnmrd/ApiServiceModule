package com.raika.apiservicemodule.toast

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.raika.apiservicemodule.R

fun Context.toasting(message: String) {
    val font = ResourcesCompat.getFont(this, R.font.regular)
    val spannableString = SpannableString(message)
    spannableString.setSpan(font, 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    Toast.makeText(this, spannableString, Toast.LENGTH_SHORT).show()
}

fun Context.toastingLong(message: String) {
    val font = ResourcesCompat.getFont(this, R.font.regular)
    val spannableString = SpannableString(message)
    spannableString.setSpan(font, 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    Toast.makeText(this, spannableString, Toast.LENGTH_LONG).show()
}