package biz.eventually.atpl.common

import javax.inject.Qualifier

/**
 * Created by Thibault de Lambilly on 18/03/2017.
 */
class ApiUrl(val value: String = ATPL) {
    companion object {

        val ATPL = "atpl"

        val ATPL_MOCK = "atpl_mock"
    }
}
