package biz.eventually.atpl.network.network

/**
 * Created by thibault on 20/03/17.
 */
data class Question(
        val id: Int,
        val label: String,
        val answers: List<Answer>,
        val img: List<String>?
)