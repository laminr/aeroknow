package biz.eventually.atpl.network.model

import biz.eventually.atpl.network.model.dto.TopicDto

/**
 * Created by thibault on 20/03/17.
 */
data class Subject(val id: Int, val name: String, val topics: List<TopicDto>)