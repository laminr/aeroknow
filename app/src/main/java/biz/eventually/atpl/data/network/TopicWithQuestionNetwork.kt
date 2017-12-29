package biz.eventually.atpl.data.network

/**
 * Created by Thibault de Lambilly on 20/03/17.
 */
data class TopicWithQuestionNetwork(
        val id: Int,
        val name: String,
        val questions: List<QuestionNetwork>?,
        val follow: Int?,
        val focus: Int?
)