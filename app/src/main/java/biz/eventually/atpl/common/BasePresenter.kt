package biz.eventually.atpl.common

import biz.eventually.atpl.utils.AppRxSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Thibault de Lambilly on 22/03/17.
 */
open class RxBaseManager {

    protected val scheduler: AppRxSchedulers = AppRxSchedulers()

    protected val disposables = CompositeDisposable()
}