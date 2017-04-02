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

fun toAppSource(from: SourceNetwork) = Source(from.id, from.name)

fun toAppSources(from: List<SourceNetwork>?) = from?.map(::toAppSource)

fun toAppSubject(from: SubjectNetwork) = Subject(from.id, from.name, from.topics)

fun toAppSubjects(from: List<SubjectNetwork>?) = from?.map(::toAppSubject)

fun toAppTopic(from: TopicNetwork) = Topic(from.id, from.name, from.questions, from.follow, from.focus)

fun toAppTopics(from: List<TopicNetwork>?) = from?.map(::toAppTopic)
