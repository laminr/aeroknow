package biz.eventually.atpl.data.model.dto

/**
 * Created by thibault on 20/03/17.
 */
data class TopicDto(val id: Int, val name: String, val questions: Int, var follow: Int, var focus: Int)