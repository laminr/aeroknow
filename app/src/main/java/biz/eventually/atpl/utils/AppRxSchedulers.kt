package biz.eventually.atpl.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Thibault de Lambilly on 07/10/2017.
 */
data class AppRxSchedulers(
        val disk: Scheduler = Schedulers.single(),
        val network: Scheduler = Schedulers.io(),
        val main: Scheduler = AndroidSchedulers.mainThread()
)