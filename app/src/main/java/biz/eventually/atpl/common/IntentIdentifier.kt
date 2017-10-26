package biz.eventually.atpl.common

/**
 * Created by Thibault de Lambilly on 28/03/2017.
 */
interface IntentIdentifier {
    companion object {
        val SOURCE_LIST = "intent.source.list"
        val SOURCE_ID = "intent.source.idWeb"
        val SOURCE_NAME = "intent.source.name"
        val DATA_FROM_DB = "intent.data.from.db"

        val TOPIC = "intent.object.topic"
        val TOPIC_STARRED = "intent.object.topic.starred"

        val NETWORK_ERROR = "intent.network.error"

        val REFRESH_SUBJECT = 10
    }
}