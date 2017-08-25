package biz.eventually.atpl.data

import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.data.model.*
import biz.eventually.atpl.data.network.*
import io.realm.RealmList

/**
 * Created by thibault on 21/03/17.
 */

fun toAppSources(from: List<SourceNetwork>?) = from?.map({ Source(it.id, it.name) }) ?: listOf()
fun toAppSubjects(sourceId: Int, from: List<SubjectNetwork>?) = from?.map({ Subject(sourceId, it.id, it.name, toAppTopics(it.topics))}) ?: listOf()

fun toAppTopic(from: TopicNetwork) = Topic(from.id, from.name, from.questions, from.follow, from.focus)

fun toAppTopics(from: List<TopicNetwork>?) : RealmList<Topic> {

    val list = RealmList<Topic>()
    from?.forEach {
        list.add(toAppTopic(it))
    }

    return  list
}

fun toAppAnswers(from: List<AnswerNetwork>?): RealmList<Answer> {
    val list = RealmList<Answer>()
    from?.forEach {
        list.add(Answer(it.id, it.value, it.good))
    }

    return  list
}

fun toAppFollow(from: FollowNetwork?) = Follow(from?.good ?: 0, from?.wrong ?: 0)

fun toAppQuestion(from: QuestionNetwork) = Question(from.id, from.label, toAppAnswers(from.answers), from.img, from.focus, toAppFollow(from.follow))
fun toAppQuestions(from: List<QuestionNetwork>?): List<Question> = from?.map(::toAppQuestion)?.toList() ?: listOf()