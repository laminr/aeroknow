package biz.eventually.atpl.data.network

/**
 * Created by thibault on 20/03/17.
 */
data class TopicDtoNetwork(
        val id: Int,
        val name: String,
        val questions: Int = 0,
        val follow: Int?,
        val focus: Int?,
        val mean:Double?
)