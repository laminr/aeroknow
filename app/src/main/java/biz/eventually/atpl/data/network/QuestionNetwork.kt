package biz.eventually.atpl.data.network

/**
 * Created by Thibault de Lambilly on 20/03/17.
 */
data class QuestionNetwork(
        val id: Long,
        val label: String,
        val answers: List<AnswerNetwork>,
        val img: String?,
        val focus: Boolean?,
        val follow: FollowNetwork?
)