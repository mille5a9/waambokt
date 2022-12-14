package org.waambokt.service.odds

import org.waambokt.common.WaamboktGrpcServer
import org.waambokt.common.constants.Env
import org.waambokt.common.constants.Environment

fun main() {
    val env = Environment(Env.PORT, Env.ODDS, Env.MONGO_CONNECTION_STRING, Env.ISPROD)
    val port = env["PORT"].toInt()
    val server = WaamboktGrpcServer(
        port,
        OddsService(env)
    )
    server.start()
    server.blockUntilShutdown()
}
