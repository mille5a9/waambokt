package waambokt.extensions

import api.nba.kotlin.ApiNbaClient
import api.nba.kotlin.enums.HostEnum
import com.ibm.icu.util.Calendar
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.stringChoice
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingInt
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.utils.capitalizeWords
import dev.kord.core.kordLogger
import dev.kord.rest.builder.message.embed
import waambokt.SERVER_ID

class NbaApiExtension : Extension() {
    override val name = "nba-api"

    override suspend fun setup() {
        ephemeralSlashCommand {
            name = "nbaStatus"
            description = "Get the status of the bot's NBA API usage"

            guild(SERVER_ID)

            action {
                val client = ApiNbaClient(HostEnum.API_SPORTS, System.getenv("NBA_API_KEY"))
                val response = client.getAccountStatus().response
                kordLogger.info { "Queried NBA API Account Status: $response" }
                respond {
                    embed {
                        title = "NBA API Status"
                        field {
                            inline = true
                            name = "Name"
                            value = response.account.firstname + " " + response.account.lastname
                        }
                        field {
                            inline = true
                            name = "Email"
                            value = response.account.email
                        }
                        field {
                            inline = true
                            name = "Requests/Limit (Day)"
                            value = "${response.requests.current}/${response.requests.limitDay}"
                        }
                        field {
                            inline = true
                            name = "Subscription Plan"
                            value = response.subscription.plan
                        }
                    }
                }
            }
        }

        publicSlashCommand(::StandingsArgs) {
            name = "nbaStandings"
            description = "Get the current NBA standings by conference and season"

            guild(SERVER_ID)

            action {
                val client = ApiNbaClient(HostEnum.API_SPORTS, System.getenv("NBA_API_KEY"))

                val response = client.getStandingsByConferenceAndSeason(arguments.conference, arguments.season).response

                kordLogger.info { "Queried NBA API Standings: $response" }
                respond {
                    embed {
                        title = "${arguments.conference.capitalizeWords()}ern Conference NBA Standings"
                        description = "Standings for the ${arguments.season}-${arguments.season + 1} season"
                        field {
                            inline = true
                            name = "Team - Overall - Home - Away"
                            value = response.sortedBy { it.conference.rank }.joinToString("\n") {
                                "${it.conference.rank}. ${it.team.name} (${it.win.total}-${it.loss.total})" +
                                    " (${it.win.home}-${it.loss.home})" + " (${it.win.away}-${it.loss.away})"
                            }
                        }
                    }
                }
            }
        }
    }

    inner class StandingsArgs : Arguments() {
        val conference by stringChoice {
            name = "conference"
            description = "The conference to get standings for"
            choices = mutableMapOf("east" to "east", "west" to "west")
        }
        val season by defaultingInt {
            name = "season"
            description = "The integer-year that the season started in. E.g. 2020 for the 2020-2021 season."
            val date = Calendar.getInstance()
            defaultValue = if (date[Calendar.MONTH] < Calendar.OCTOBER) date[Calendar.YEAR] - 1 else date[Calendar.YEAR]
        }
    }
}
