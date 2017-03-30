package biz.eventually.atpl.network.network

/**
 * Created by thibault on 20/03/17.
 */
data class TopicNetwork(
        val id: Int,
        val name: String,
        val questions: Int,
        val follow: Int,
        val focus: Int
)