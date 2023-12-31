package waambokt.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.common.Color
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.embed
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
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
                val result = skrape(BrowserFetcher) {
                    request {
                        url = link
                        timeout = TIMEOUTMS
                        userAgent = AGENT
                        sslRelaxed = true
                    }
                    extractIt<ScrapingResult> {
                        htmlDocument {
                            findFirst("div[data-testid=\"birdwatch-pivot\"]").children.getContent(it)
                        }
                    }
                }

                event.message.reply {
                    embed {
                        author {
                            name = "Community Notes:"
                            icon = "https://abs.twimg.com/icons/apple-touch-icon-192x192.png"
                        }
                        description = result.texts.joinToString("\n")
                        color = Color(EMBEDCOLOR)
                    }
                }
            }
        }
    }

    private fun List<DocElement>.getContent(result: ScrapingResult) = this.forEach {
        if (it.tagName == "span") {
            result.texts += it.children.first().text
        } else if (it.tagName == "a") {
            result.texts += it.attribute("href")
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        private val linkRegex = """https?://(?:www\.)?twitter\.com/([a-zA-Z0-9_]+)/status/([0-9]+)""".toRegex()
        private const val AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                " Chrome/109.0.0.0 Safari/537.36"
        private const val TIMEOUTMS = 60_000
        private const val EMBEDCOLOR = 0x1DA0F2
    }

    data class ScrapingResult(val texts: MutableList<String> = mutableListOf())
}
