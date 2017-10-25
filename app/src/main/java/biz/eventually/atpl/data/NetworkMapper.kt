package biz.eventually.atpl.data

import biz.eventually.atpl.data.db.Source
import biz.eventually.atpl.data.db.Subject
import biz.eventually.atpl.data.dto.SubjectView
import biz.eventually.atpl.data.model.Answer
import biz.eventually.atpl.data.model.Follow
import biz.eventually.atpl.data.model.Question
import biz.eventually.atpl.data.db.Topic
import biz.eventually.atpl.data.network.*
import io.realm.RealmList

/**
 * Created by thibault on 21/03/17.
 */

fun toAppSources(from: List<SourceNetwork>?) = from?.map({ Source(it.id, it.name) }) ?: listOf()

fun toAppSubjects(sourceId: Long, from: List<SubjectNetwork>?) = from?.map({ Subject(it.id, sourceId, it.name).apply { topics = toAppTopics(it.id, it.topics) } }) ?: listOf()

fun toAppSubjectViews(sourceId: Long, from: List<SubjectNetwork>?)
        = from?.map({ SubjectView(Subject(it.id, sourceId, it.name), toAppTopics(it.id, it.topics)) }) ?: listOf()

fun toAppTopic(from: TopicNetwork, subjectId: Long) = Topic(from.id, subjectId, from.name, from.questions, from.follow ?: 0, from.focus ?: 0)

fun toAppTopics(sourceId: Long = -1, from: List<TopicNetwork>?): List<Topic> {

    val list = mutableListOf<Topic>()
    from?.forEach {
        list.add(toAppTopic(it, sourceId))
    }

    return list
}

fun toAppAnswers(from: List<AnswerNetwork>?): RealmList<Answer> {
    val list = RealmList<Answer>()
    from?.forEach {
        list.add(Answer(it.id, it.value, it.good))
    }

    return list
}

fun toAppFollow(from: FollowNetwork?) = Follow(from?.good ?: 0, from?.wrong ?: 0)

fun toAppQuestion(from: QuestionNetwork) = Question(from.id, from.label, toAppAnswers(from.answers), from.img, from.focus, toAppFollow(from.follow))
fun toAppQuestions(from: List<QuestionNetwork>?): List<Question> = from?.map(::toAppQuestion)?.toList() ?: listOf()