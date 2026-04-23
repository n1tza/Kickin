package com.nnita.kickin.model

import com.google.gson.annotations.SerializedName

data class StandingsWrapper(
    val league: StandingsLeague
)

data class StandingsLeague(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String?,
    val season: Int,
    val standings: List<List<StandingEntry>>
)

data class StandingEntry(
    val rank: Int,
    val team: StandingTeam,
    val points: Int,
    val goalsDiff: Int,
    val group: String?,
    val form: String?,
    val status: String?,
    val description: String?,
    val all: StandingStats,
    val update: String
)

data class StandingTeam(
    val id: Int,
    val name: String,
    val logo: String
)

data class StandingStats(
    val played: Int,
    val win: Int,
    val draw: Int,
    val lose: Int,
    val goals: StandingGoals
)

data class StandingGoals(
    @SerializedName("for") val scored: Int,
    val against: Int
)

fun StandingEntry.toStanding() = Standing(
    rank = rank,
    teamName = team.name,
    teamLogo = team.logo,
    played = all.played,
    win = all.win,
    draw = all.draw,
    loss = all.lose,
    goalsFor = all.goals.scored,
    goalsAgainst = all.goals.against,
    goalsDiff = goalsDiff,
    points = points,
    form = form ?: "",
    description = parseDescription(description)
)

private fun parseDescription(raw: String?): String? = when {
    raw == null -> null
    raw.contains("Champions League", ignoreCase = true) -> "Champions League"
    raw.contains("Europa League", ignoreCase = true) -> "Europa League"
    raw.contains("Conference League", ignoreCase = true) -> "Conference League"
    raw.contains("Relegation Play-off", ignoreCase = true) -> "Relegation Play-off"
    raw.contains("Relegation", ignoreCase = true) -> "Relegation"
    else -> null
}
