package me.koddle.tools

import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.core.shareddata.impl.ClusterSerializable
import io.vertx.kotlin.core.eventbus.deliveryOptionsOf
import io.vertx.kotlin.core.eventbus.requestAwait

interface EventBusClient {
    suspend fun <T : ClusterSerializable> request(address: String, message: Any, headers: Map<String, String>?): Message<T>
}

class VertxEventBusClient(val vertx: Vertx): EventBusClient {
    suspend override fun <T : ClusterSerializable> request(address: String, message: Any, headers: Map<String, String>?): Message<T> {
        if (headers != null)
            return vertx.eventBus().requestAwait<T>(address, message, deliveryOptionsOf(headers = headers))
        return vertx.eventBus().requestAwait<T>(address, message)
    }
}