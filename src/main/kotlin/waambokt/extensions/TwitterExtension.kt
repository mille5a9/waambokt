package waambokt.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.create.embed
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import mu.KotlinLogging

class TwitterExtension : Extension() {
    override val name = "twitter"

    private val linkRegex = """https?://(?:www\.)?twitter\.com/([a-zA-Z0-9_]+)/status/([0-9]+)""".toRegex()

    override suspend fun setup() {
        event<MessageCreateEvent> {
            action {
                logger.info { "TwitterExtension: caught MessageCreateEvent" }
                val link = linkRegex.find(event.message.content)?.value ?: return@action

                // scrape community notes content
                val result = skrape(HttpFetcher) {
                    request {
                        url = link
                        timeout = sixtySecondsInMillis
                        userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"
                    }
                    extractIt<ScrapingResult> {
                        htmlDocument {
                            findFirst(communityNotesClassString)
                        }.children.forEach { element ->
                            if (element.tagName == "span") it.texts += element.children.first().text
                            else if (element.tagName == "a") it.texts += element.attribute("href")
                        }
                    }
                }

                event.message.reply {
                    embed {
                        title = "Readers added context they thought people might want to know"
                        description = result.texts.joinToString("\n")
                    }
                }
            }
        }
    }

    data class ScrapingResult(val texts: MutableList<String> = mutableListOf())

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val sixtySecondsInMillis = 60_000
        private const val communityNotesClassString =
            ".css-901oao.r-37j5jr.r-a023e6.r-16dba41.r-rjixqe.r-bcqeeo.r-1e081e0.r-qvutc0"
    }
}
