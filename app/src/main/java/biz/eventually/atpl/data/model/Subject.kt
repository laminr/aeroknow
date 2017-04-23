package biz.eventually.atpl.data.model

import biz.eventually.atpl.data.model.dto.TopicDto

/**
 * Created by thibault on 20/03/17.
 */
data class Subject(val id: Int, val name: String, val topics: List<TopicDto>)