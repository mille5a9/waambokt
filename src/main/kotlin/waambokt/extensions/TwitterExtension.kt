package waambokt.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.common.Color
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.create.embed
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import mu.KotlinLogging

class TwitterExtension : Extension() {
    override val name = "twitter"

    override suspend fun setup() {
        event<MessageCreateEvent> {
            action {
                logger.info { "TwitterExtension: caught MessageCreateEvent" }
                val link = linkRegex.find(event.message.content)?.value ?: return@action

                // scrape community notes content
                val result = skrape(HttpFetcher) {
                    request { url = link; timeout = timeoutMs; userAgent = agent }
                    extractIt<ScrapingResult> { htmlDocument { findFirst(notesClass) }.children.getContent(it) }
                }

                event.message.reply {
                    embed {
                        author {
                            name = "Community Notes:"
                            icon = "https://abs.twimg.com/icons/apple-touch-icon-192x192.png"
                        }
                        description = result.texts.joinToString("\n")
                        color = Color(embedColor)
                    }
                }
            }
        }
    }

    private fun List<DocElement>.getContent(result: ScrapingResult) = this.forEach {
        if (it.tagName == "span") result.texts += it.children.first().text
        else if (it.tagName == "a") result.texts += it.attribute("href")
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        private val linkRegex = """https?://(?:www\.)?twitter\.com/([a-zA-Z0-9_]+)/status/([0-9]+)""".toRegex()
        private const val notesClass = ".css-901oao.r-37j5jr.r-a023e6.r-16dba41.r-rjixqe.r-bcqeeo.r-1e081e0.r-qvutc0"
        private const val agent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"
        private const val timeoutMs = 60_000
        private const val embedColor = 0x1DA0F2
    }

    data class ScrapingResult(val texts: MutableList<String> = mutableListOf())
}
