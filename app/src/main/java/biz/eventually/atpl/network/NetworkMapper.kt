package biz.eventually.atpl.network

import biz.eventually.atpl.network.model.Source
import biz.eventually.atpl.network.model.Subject
import biz.eventually.atpl.network.model.Topic
import biz.eventually.atpl.network.network.SourceNetwork
import biz.eventually.atpl.network.network.SubjectNetwork
import biz.eventually.atpl.network.network.TopicNetwork

/**
 * Created by thibault on 21/03/17.
 */

//interface NetworkMapper {

fun toAppSource(from: SourceNetwork): Source {
    return Source(from.id, from.name)
}

fun toAppSources(from: List<SourceNetwork>?): List<Source>? {
    return from?.map(::toAppSource)
}

fun toAppSubject(from: SubjectNetwork): Subject {
    return Subject(from.id, from.name, from.topics)
}

fun toAppSubjects(from: List<SubjectNetwork>?): List<Subject>? {
    return from?.map(::toAppSubject)
}

fun toAppTopic(from: TopicNetwork): Topic {
    return Topic(from.id, from.name, from.questions, from.follow, from.focus)
}

fun toAppTopics(from: List<TopicNetwork>?): List<Topic>? {
    return from?.map(::toAppTopic)
}
//}
