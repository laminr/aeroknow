package biz.eventually.atpl.data.dto

import biz.eventually.atpl.data.db.Topic

/**
 * Created by thibault on 20/03/17.
 */
data class TopicView(val topic: Topic, var hasOfflineData: Boolean = true, var isSync: Boolean = false)