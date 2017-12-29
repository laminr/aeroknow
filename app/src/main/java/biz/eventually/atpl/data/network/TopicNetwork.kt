package biz.eventually.atpl.data.network

/**
 * Created by Thibault de Lambilly on 20/03/17.
 */
data class TopicNetwork(
        val id: Long,
        val name: String,
        val questions: Int = 0,
        val follow: Int?,
        val focus: Int?,
        val mean:Double?
)