package biz.eventually.atpl.data

import biz.eventually.atpl.data.model.Source
import biz.eventually.atpl.data.model.Subject
import biz.eventually.atpl.data.model.Topic
import biz.eventually.atpl.data.model.dto.TopicDto
import biz.eventually.atpl.data.network.*

/**
 * Created by thibault on 21/03/17.
 */

fun toAppSources(from: List<SourceNetwork>?) = from?.map({Source(it.id, it.name) })

fun toAppSubjects(from: List<SubjectNetwork>?) = from?.map({Subject(it.id, it.name, toAppTopicDtos(it.topics))})

fun toAppTopic(from: TopicNetwork) = Topic(from.id, from.name, toAppQuestions(from.questions), from.follow, from.focus)
fun toAppTopics(from: List<TopicNetwork>?) = from?.map(::toAppTopic) ?: listOf()


fun toAppAnswers(from: List<AnswerNetwork>?) = from?.map({ Answer(it.id, it.value, it.good) }) ?: listOf()
fun toAppQuestions(from: List<QuestionNetwork>?) = from?.map({ Question(it.id, it.label, toAppAnswers(it.answers), explodeImgRaw(it.img))}) ?: listOf()

// DTO
fun toAppTopicDto(from: TopicDtoNetwork) = TopicDto(from.id, from.name, from.questions, from.follow, from.focus)
fun toAppTopicDtos(from: List<TopicDtoNetwork>?) = from?.map(::toAppTopicDto) ?: listOf()

fun explodeImgRaw(raw: String?): List<String> = raw?.split("|")?.toList() ?: listOf()