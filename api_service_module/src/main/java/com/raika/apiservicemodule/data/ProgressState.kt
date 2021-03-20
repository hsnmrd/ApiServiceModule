package com.raika.apiservicemodule.data

import android.util.Log
import com.raika.alertmodule.progress.ModuleProgress
import com.raika.alertmodule.progress.Progress
import com.raika.apiservicemodule.setting.SharedPreference.apiServiceModuleNoInternetMessage
import com.raika.apiservicemodule.setting.apiServiceModuleGetPreference
import com.raika.apiservicemodule.toast.apiServiceModuleToasting

/**
 * * show progress when async task is running
 * * hide progress after result received: results are [Resource.Status.SUCCESS], [Resource.Status.ERROR], [Resource.Status.LOADING]
 * @param resource pass result of observing data
 * @param progress pass an object from [Progress]
 * @param successListener listen if status is [Resource.Status.SUCCESS]
 */

fun <T> updateProgressByState(
    resource: Resource<T>,
    progress: ModuleProgress?,
    errorListener: ((error: String) -> Unit)? = null,
    successListener: (() -> Unit)? = null
) {
    when (resource.status) {
    
        Resource.Status.LOADING -> {
            Log.e("api_service_module", "LOADING")
            progress?.show()
            progress?.clickListener {
                Log.e("api_service_module", "progress clicked")
                resource.clickListener?.invoke(it)!!
            }
        }

        Resource.Status.SUCCESS -> {
            Log.e("api_service_module", "SUCCESS")
            resource.data?.let {
                successListener?.invoke()
                progress?.hide()
            }
            if (resource.data == null) {
                progress?.hide()
            }
        }

        Resource.Status.ERROR -> {
            Log.e("api_service_module", "ERROR")
            errorListener?.invoke(resource.message.toString())
            progress?.hide()
            when {
                resource.message.toString() == "اتصال اینترنت خود را بررسی کنید" -> {
                    progress?.progressContext?.apiServiceModuleToasting(apiServiceModuleGetPreference(progress.progressContext).apiServiceModuleNoInternetMessage.toString())
                }
                resource.message.toString() == "کار قطع شد" -> {}
                else -> {
                    progress?.progressContext?.apiServiceModuleToasting(resource.message.toString())
                }
            }
        }

    }
}