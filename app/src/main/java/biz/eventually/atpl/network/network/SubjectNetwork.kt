package biz.eventually.atpl.network.network

import biz.eventually.atpl.network.model.Topic

/**
 * Created by thibault on 21/03/17.
 */
data class SubjectNetwork(val id: Int, val name: String, val topics: List<Topic>)