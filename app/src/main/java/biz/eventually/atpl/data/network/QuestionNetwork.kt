package biz.eventually.atpl.data.network

/**
 * Created by thibault on 20/03/17.
 */
data class QuestionNetwork(
        val id: Int,
        val label: String,
        val answers: List<AnswerNetwork>,
        val img: String?,
        val focus: Boolean?,
        val follow: FollowNetwork?
)