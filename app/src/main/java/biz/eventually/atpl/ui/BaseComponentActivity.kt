package biz.eventually.atpl.ui

import android.support.v7.app.AppCompatActivity
import javax.inject.Inject

/**
 * Created by Thibault de Lambilly on 04/04/2017.
 */
open class BaseComponentActivity: AppCompatActivity() {

    override fun onDestroy() {
        super.onDestroy()
    }
}