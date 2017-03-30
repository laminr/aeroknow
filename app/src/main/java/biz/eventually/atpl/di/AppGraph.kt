package biz.eventually.atpl.di

import biz.eventually.atpl.ui.source.SourceActivity
import biz.eventually.atpl.ui.subject.SubjectActivity

/**
 * Created by laminr on 19/03/2017.
 */

interface AppGraph {

    fun inject(source: SourceActivity)

    fun inject(source: SubjectActivity)

}
