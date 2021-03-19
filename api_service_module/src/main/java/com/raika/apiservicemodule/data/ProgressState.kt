package com.raika.apiservicemodule.data

import com.raika.alertmodule.progress.ModuleProgress
import com.raika.alertmodule.progress.Progress
import com.raika.apiservicemodule.toast.toasting

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
            progress?.clickListener {
                resource.clickListener?.invoke(it)!!
            }
            progress?.show()
        }

        Resource.Status.SUCCESS -> {
            resource.data?.let {
                successListener?.invoke()
                progress?.hide()
            }
            if (resource.data == null) {
                progress?.hide()
            }
        }

        Resource.Status.ERROR -> {
            errorListener?.invoke(resource.message.toString())
            progress?.hide()
            when {
                resource.message.toString() == "اتصال اینترنت خود را بررسی کنید" -> {
                    progress?.progressContext?.toasting("اطلاعات آفلاین")
                }
                resource.message.toString() == "کار قطع شد" -> {
                    progress?.progressContext?.toasting("عملیات متوقف شد")
                }
                else -> {
                    progress?.progressContext?.toasting(resource.message.toString())
                }
            }
        }

    }
}