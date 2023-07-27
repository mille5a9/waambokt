package waambokt

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake
import waambokt.extensions.TestExtension

val devFlag = env("ENV") == "DEV" // Check if we're running in the test environment

val SERVER_ID = Snowflake(
    if (devFlag) env("DEVGUILD").toLong() else env("PRODGUILD").toLong(),
)

private val TOKEN =
    if (devFlag) env("DEVTOKEN") else env("PRODTOKEN")

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        chatCommands {
            defaultPrefix = "wb"
            enabled = true

            prefix { if (guildId == SERVER_ID) "!" else it }
        }

        extensions {
            add(::TestExtension)
        }
    }

    bot.start()
}
