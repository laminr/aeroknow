package biz.eventually.atpl.data.dto

import biz.eventually.atpl.data.model.Topic

/**
 * Created by thibault on 20/03/17.
 */
data class TopicDto(val topic: Topic, var hasOfflineData: Boolean = true, var isSync: Boolean = true)