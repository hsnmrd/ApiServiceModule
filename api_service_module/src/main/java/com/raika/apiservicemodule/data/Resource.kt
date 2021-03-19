package com.raika.apiservicemodule.data

import android.content.Context
import androidx.lifecycle.MutableLiveData

/**
 * **Resource** a class that handle remote request **response**
 * @param status: set status of response [Status.SUCCESS], [Status.ERROR], [Status.LOADING]
 * @param data: pass response of request
 * @param message: if [status] is [Status.ERROR] then pass message of exception
 * @param clickListener: if [status] is [Status.LOADING] then pass message of exception
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?, val clickListener: ((context: Context) -> MutableLiveData<Boolean>)?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null)
        }
        
        fun <T> error(message: String, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, message, null)
        }
    
        /**
         * invoke listener, which called in [taop.sana.ir.utils.data.OperationKt] #loading function
         */
        fun <T> showLoading(data: T? = null, clickListener: (context: Context) -> MutableLiveData<Boolean>): Resource<T> {
            return Resource(Status.LOADING, data, null, clickListener)
        }
    }
}