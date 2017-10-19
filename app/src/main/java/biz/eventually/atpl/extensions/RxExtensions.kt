package biz.eventually.atpl.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Thibault de Lambilly on 18/10/17.
 */

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}