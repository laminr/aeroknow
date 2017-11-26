package biz.eventually.atpl.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import biz.eventually.atpl.common.RxBaseManager
import biz.eventually.atpl.data.NetworkStatus

/**
 * Created by Thibault de Lambilly on 26/11/2017.
 *
 */
abstract class BaseRepository : RxBaseManager() {

    protected var status: MutableLiveData<NetworkStatus> = MutableLiveData()

    fun networkStatus(): LiveData<NetworkStatus> = status

}