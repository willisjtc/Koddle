package me.koddle.controllers

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import me.koddle.tools.DatabaseAccess
import me.koddle.tools.JWTHelper
import me.koddle.tools.EventBusClient
import org.koin.core.KoinComponent
import org.koin.core.inject

open class BaseController : KoinComponent {
    protected val config: JsonObject by inject()
    protected val webClient: WebClient by inject()
    protected val ebClient: EventBusClient by inject()
    protected val da: DatabaseAccess by inject()
    protected val jwtHelper: JWTHelper by inject()
}