package biz.eventually.atpl.common

/**
 * Created by laminr on 28/03/2017.
 */
interface IntentIdentifier {
    companion object {
        val SOURCE_LIST = "intent.source.list"
        val SOURCE_ID = "intent.source.id"
        val SOURCE_NAME = "intent.source.name"
        val TOPIC = "intent.object.topic"
    }
}