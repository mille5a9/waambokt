package waambokt

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake
import mu.KotlinLogging
import waambokt.extensions.NbaApiExtension
import waambokt.extensions.TestExtension
import waambokt.extensions.TwitterExtension

val devFlag = env("ENV") == "DEV" // Check if we're running in the test environment
val SERVER_ID = Snowflake(if (devFlag) env("DEVGUILD").toLong() else env("PRODGUILD").toLong())
private val TOKEN = if (devFlag) env("DEVTOKEN") else env("PRODTOKEN")

suspend fun main() {
    val logger = KotlinLogging.logger {}

    val bot = ExtensibleBot(TOKEN) {
        chatCommands {
            defaultPrefix = "wb "
            enabled = true
        }

        presence { this.listening("your commands") }

        extensions {
            add(::TestExtension)
            add(::TwitterExtension)
            add(::NbaApiExtension)
        }
    }

    logger.info { "Starting bot" }

    bot.start()
}
