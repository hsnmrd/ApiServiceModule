package com.raika.apiservicemodule.data

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import com.google.android.material.button.MaterialButton
import com.raika.alertmodule.dialog.ModuleFullScreenAlert
import com.raika.alertmodule.dialog.utility.UtilityFullScreen
import com.raika.apiservicemodule.R
import com.raika.apiservicemodule.data.Resource.Status.SUCCESS
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit

data class DBData<DataBaseClass, A>(
    var dataBase: DataBaseClass,
    var response: A,
)

data class CallBackModel<DataBase, A, ApiService>(
    var dataBase: DBData<DataBase, A>,
    var response: ApiService,
)

data class OptionModel<ApiServiceClass, DataBaseClass, DefaultActivity>(
    var serviceClass: Class<ApiServiceClass>,
    var dataBaseClass: DataBaseClass,
    var defaultActivity: Class<DefaultActivity>,
    var retrofitBuilder: Retrofit.Builder,
)

data class OptionLocalModel<DataBaseClass>(
    var dataBaseClass: DataBaseClass,
    var retrofitBuilder: Retrofit.Builder,
)

data class OptionRemoteModel<ApiServiceClass, DefaultActivity>(
    var serviceClass: Class<ApiServiceClass>,
    var defaultActivity: Class<DefaultActivity>,
    var retrofitBuilder: Retrofit.Builder,
)

data class CheckResponseModel<A>(
    var resource: A?,
    var success: suspend () -> Unit,
    var failed: suspend (message: String) -> Unit
)


/**
 * **operationSendRequest** an operation for
 * * send remote request
 */
fun <ApiService, DefaultActivity, A> operationSendRequest(
    optionRemoteModel: OptionRemoteModel<ApiService, DefaultActivity>,
    networkRequest: suspend (ApiService) -> Response<A>,
): LiveData<Resource<A>> {
    val job = Job()
    val progressState: MutableLiveData<Boolean> = MutableLiveData(false)
    var adAlertDialog: UtilityFullScreen? = null
    
    return liveData(CoroutineScope(job + Dispatchers.IO).coroutineContext) {
        
        controller<A>(this, progressState, job) { adAlertDialog = it }
        
        optionRemoteModel.retrofitBuilder.build().create(optionRemoteModel.serviceClass)?.let {
            val responseStatus = getResult { networkRequest.invoke(it) }
            if (responseStatus.status == SUCCESS) {
                emit(Resource.success(responseStatus.data))
                adAlertDialog?.dismiss()
            } else {
                emit(Resource.error(responseStatus.message.toString()))
                adAlertDialog?.dismiss()
            }
        }
        
        
    }
}


/**
 * **operationSendRequest** an operation for
 * * send remote request
 */
fun <ApiService, DefaultActivity, A> operationSendRequestHandleResponse(
    optionRemoteModel: OptionRemoteModel<ApiService, DefaultActivity>,
    networkRequest: suspend (ApiService) -> Response<A>,
    checkResponse: suspend (
        CheckResponseModel<A>
    ) -> Unit,
): LiveData<Resource<A>> {
    val job = Job()
    val progressState: MutableLiveData<Boolean> = MutableLiveData(false)
    var adAlertDialog: UtilityFullScreen? = null
    
    return liveData(CoroutineScope(job + Dispatchers.IO).coroutineContext) {
        
        controller<A>(this, progressState, job) { adAlertDialog = it }
        
        optionRemoteModel.retrofitBuilder.build().create(optionRemoteModel.serviceClass)?.let {
            val responseStatus = getResult { networkRequest.invoke(it) }
            if (responseStatus.status == SUCCESS) {
                checkResponse(
                        CheckResponseModel(
                                responseStatus.data,
                                { emit(Resource.success(responseStatus.data)) },
                                { emit(Resource.error<A>(it)) }
                        )
                )
                adAlertDialog?.dismiss()
            } else {
                emit(Resource.error(responseStatus.message.toString()))
                adAlertDialog?.dismiss()
            }
        }
        
        
    }
}


/**
 * **operationSendRequestWithCallBack** an operation for
 * * send remote request
 * * update local data
 */
fun <ApiService, DataBase, DefaultActivity, A> operationSendRequestWithCallBack(
    optionClasses: OptionModel<ApiService, DataBase, DefaultActivity>,
    networkRequest: suspend (ApiService) -> Response<A>,
    updateLocalData: suspend (CallBackModel<DataBase, A, ApiService>) -> Unit,
    updateLocalDataFailed: (suspend (db: DataBase) -> Unit)? = null,
): LiveData<Resource<A>> {
    
    val job = Job()
    val progressState: MutableLiveData<Boolean> = MutableLiveData(false)
    var adAlertDialog: UtilityFullScreen? = null
    
    return liveData(CoroutineScope(job + Dispatchers.IO).coroutineContext) {
        
        controller<A>(this, progressState, job) { adAlertDialog = it }
        
        optionClasses.retrofitBuilder.build().create(optionClasses.serviceClass)?.let {
            val responseStatus = getResult { networkRequest.invoke(it) }
            if (responseStatus.status == SUCCESS) {
                emit(Resource.success(responseStatus.data))
                optionClasses.retrofitBuilder.build().create(optionClasses.serviceClass)?.let {
                    updateLocalData(CallBackModel(DBData(optionClasses.dataBaseClass, responseStatus.data!!), it))
                }
                adAlertDialog?.dismiss()
            } else {
                emit(Resource.error(responseStatus.message.toString()))
                updateLocalDataFailed?.invoke(optionClasses.dataBaseClass)
                adAlertDialog?.dismiss()
            }
        }
        
        
    }
    
}


/**
 * **operationSendRequestWithCallBack** an operation for
 * * send remote request
 * * update local data
 */
fun <ApiService, DataBase, DefaultActivity, A> operationSendRequestWithCallBackHandleResponse(
    optionClasses: OptionModel<ApiService, DataBase, DefaultActivity>,
    networkRequest: suspend (ApiService) -> Response<A>,
    updateLocalData: suspend (CallBackModel<DataBase, A, ApiService>) -> Unit,
    checkResponse: suspend (
        CheckResponseModel<A>
    ) -> Unit,
    updateLocalDataFailed: (suspend (db: DataBase) -> Unit)? = null,
): LiveData<Resource<A>> {
    
    val job = Job()
    val progressState: MutableLiveData<Boolean> = MutableLiveData(false)
    var adAlertDialog: UtilityFullScreen? = null
    
    return liveData(CoroutineScope(job + Dispatchers.IO).coroutineContext) {
        
        controller<A>(this, progressState, job) { adAlertDialog = it }
        
        optionClasses.retrofitBuilder.build().create(optionClasses.serviceClass)?.let {
            val responseStatus = getResult { networkRequest.invoke(it) }
            if (responseStatus.status == SUCCESS) {
                checkResponse(
                        CheckResponseModel(
                                responseStatus.data,
                                {
                                    emit(Resource.success(responseStatus.data))
                                    optionClasses.retrofitBuilder.build().create(optionClasses.serviceClass)?.let {
                                        updateLocalData(CallBackModel(DBData(optionClasses.dataBaseClass, responseStatus.data!!), it))
                                    }
                                },
                                {
                                    emit(Resource.error<A>(it))
                                    updateLocalDataFailed?.invoke(optionClasses.dataBaseClass)
                                }
                        )
                )
                adAlertDialog?.dismiss()
            } else {
                emit(Resource.error(responseStatus.message.toString()))
                updateLocalDataFailed?.invoke(optionClasses.dataBaseClass)
                adAlertDialog?.dismiss()
            }
        }
        
        
    }
    
}


/**
 * **operationSendRequestWithUpdateDatabase** an operation for
 * * send remote request
 * * get local data
 * * update local data
 */
fun <ApiService, DataBase, DefaultActivity, T, A> operationSendRequestWithUpdateDatabaseHandleResponse(
    optionClasses: OptionModel<ApiService, DataBase, DefaultActivity>,
    databaseQuery: (db: DataBase) -> LiveData<T>,
    networkRequest: suspend (ApiService) -> Response<A>,
    updateLocalData: suspend (DBData<DataBase, A>) -> Unit,
    checkResponse: suspend (
        CheckResponseModel<A>
    ) -> Unit,
    updateLocalDataFailed: (suspend (db: DataBase) -> Unit)? = null,
): LiveData<Resource<T>> {
    
    val job = Job()
    val progressState: MutableLiveData<Boolean> = MutableLiveData(false)
    var adAlertDialog: UtilityFullScreen? = null
    
    return liveData(CoroutineScope(job + Dispatchers.IO).coroutineContext) {
        
        controller<T>(this, progressState, job) { adAlertDialog = it }
        
        val source: LiveData<Resource<T>> = databaseQuery.invoke(optionClasses.dataBaseClass).map { Resource.success(it) }
        emitSource(source)
        
        optionClasses.retrofitBuilder.build().create(optionClasses.serviceClass)?.let {
            val responseStatus = getResult { networkRequest.invoke(it) }
            if (responseStatus.status == SUCCESS) {
                checkResponse(
                        CheckResponseModel(
                                responseStatus.data,
                                { updateLocalData(DBData(optionClasses.dataBaseClass, responseStatus.data!!)) },
                                {
                                    emit(Resource.error<T>(it))
                                    updateLocalDataFailed?.invoke(optionClasses.dataBaseClass)
                                }
                        )
                )
                adAlertDialog?.dismiss()
            } else {
                emit(Resource.error(responseStatus.message.toString()))
                updateLocalDataFailed?.invoke(optionClasses.dataBaseClass)
                adAlertDialog?.dismiss()
            }
        }
        
        
    }
    
}

/**
 * **operationSendRequestWithUpdateDatabase** an operation for
 * * send remote request
 * * get local data
 * * update local data
 */
fun <ApiService, DataBase, DefaultActivity, T, A> operationSendRequestWithUpdateDatabase(
    optionClasses: OptionModel<ApiService, DataBase, DefaultActivity>,
    databaseQuery: (db: DataBase) -> LiveData<T>,
    networkRequest: suspend (ApiService) -> Response<A>,
    updateLocalData: suspend (DBData<DataBase, A>) -> Unit,
    updateLocalDataFailed: (suspend (db: DataBase) -> Unit)? = null,
): LiveData<Resource<T>> {
    
    val job = Job()
    val progressState: MutableLiveData<Boolean> = MutableLiveData(false)
    var adAlertDialog: UtilityFullScreen? = null
    
    return liveData(CoroutineScope(job + Dispatchers.IO).coroutineContext) {
        
        controller<T>(this, progressState, job) { adAlertDialog = it }
        
        val source: LiveData<Resource<T>> = databaseQuery.invoke(optionClasses.dataBaseClass).map { Resource.success(it) }
        emitSource(source)
        
        optionClasses.retrofitBuilder.build().create(optionClasses.serviceClass)?.let {
            val responseStatus = getResult { networkRequest.invoke(it) }
            if (responseStatus.status == SUCCESS) {
                updateLocalData(DBData(optionClasses.dataBaseClass, responseStatus.data!!))
                adAlertDialog?.dismiss()
            } else {
                emit(Resource.error(responseStatus.message.toString()))
                updateLocalDataFailed?.invoke(optionClasses.dataBaseClass)
                adAlertDialog?.dismiss()
            }
        }
        
        
    }
    
}


/**
 * **operationGetLocalData** an operation for
 * * get a [liveData] local data
 */
fun <DataBase, T> operationGetLocalData(
    optionLocalModel: OptionLocalModel<DataBase>,
    databaseQuery: (db: DataBase) -> LiveData<T>,
): LiveData<Resource<T>> =
    liveData(Dispatchers.IO) {
        val source: LiveData<Resource<T>> = databaseQuery.invoke(optionLocalModel.dataBaseClass).map { Resource.success(it) }
        emitSource(source)
    }


/**
 * **operationGetLocalData** an operation for
 * * launch a [suspend] function
 */
fun <DataBase> operationGetLocalData(
    optionLocalModel: OptionLocalModel<DataBase>,
    databaseQuery: suspend (db: DataBase) -> Unit,
) {
    GlobalScope.launch(Dispatchers.IO) { databaseQuery(optionLocalModel.dataBaseClass) }
}


/**
 * **loading** a function that
 * * handle [job] (cancel and continue)
 * * show loading progress and hide progress
 */

private suspend fun <T> controller(
    liveDataScope: LiveDataScope<Resource<T>>,
    progressState: MutableLiveData<Boolean>,
    job: CompletableJob,
    adListener: (UtilityFullScreen) -> Unit,
) {
    liveDataScope.emit(Resource.showLoading {
        ModuleFullScreenAlert(it, R.layout.api_service_module_root_dialog_deny_request)
                .setCancelable(true)
                .onViewCreate { dialog ->
                    progressState.postValue(false)
                    adListener(dialog.dialogRD)
                
                    val btnCancel = dialog.view.findViewById<MaterialButton>(R.id.api_service_module_btn_root_dialog_deny_request)
                    val clRoot = dialog.view.findViewById<ConstraintLayout>(R.id.api_service_module_cl_alert_dialog_rf_task_root)
                    btnCancel.setOnClickListener {
                        job.cancel()
                        GlobalScope.launch {
                            liveDataScope.emit(Resource.error("کار قطع شد"))
                        }
                        dialog.dialogRD.dismiss()
                    }
                
                    clRoot.setOnClickListener {
                        dialog.dialogRD.dismiss()
                        progressState.postValue(true)
                    }
                    dialog.dialogRD.setOnCancelListener { progressState.postValue(true) }
                }
                .show()
        return@showLoading progressState.distinctUntilChanged() as MutableLiveData<Boolean>
    })
}

