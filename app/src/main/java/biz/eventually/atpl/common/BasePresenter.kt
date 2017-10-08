package biz.eventually.atpl.common

import biz.eventually.atpl.utils.AppRxSchedulers

/**
 * Created by thibault on 22/03/17.
 */
open class RxBaseManager {

    protected val scheduler: AppRxSchedulers = AppRxSchedulers()
}