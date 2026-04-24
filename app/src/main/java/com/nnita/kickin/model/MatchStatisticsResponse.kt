package com.nnita.kickin.model

import com.google.gson.JsonElement

data class MatchStatisticsResponse(
    val team: StatTeam,
    val statistics: List<StatEntry>
)

data class StatTeam(
    val id: Int,
    val name: String,
    val logo: String?
)

data class StatEntry(
    val type: String,
    val value: JsonElement?
)

fun MatchStatisticsResponse.toTeamStatistics() = TeamStatistics(
    teamName = team.name,
    shotsOnGoal    = statistics.intOf("Shots on Goal"),
    shotsOffGoal   = statistics.intOf("Shots off Goal"),
    totalShots     = statistics.intOf("Total Shots"),
    blockedShots   = statistics.intOf("Blocked Shots"),
    ballPossession = statistics.stringOf("Ball Possession", "0%"),
    cornerKicks    = statistics.intOf("Corner Kicks"),
    fouls          = statistics.intOf("Fouls"),
    yellowCards    = statistics.intOf("Yellow Cards"),
    redCards       = statistics.intOf("Red Cards"),
    offsides       = statistics.intOf("Offsides"),
    passesPercentage = statistics.stringOf("Passes %", "0%")
)

private fun List<StatEntry>.intOf(type: String): Int {
    val el = find { it.type == type }?.value ?: return 0
    return when {
        el.isJsonNull -> 0
        el.isJsonPrimitive && el.asJsonPrimitive.isNumber -> el.asInt
        el.isJsonPrimitive -> el.asString.replace("%", "").toIntOrNull() ?: 0
        else -> 0
    }
}

private fun List<StatEntry>.stringOf(type: String, default: String): String {
    val el = find { it.type == type }?.value ?: return default
    return if (el.isJsonNull) default else el.asString
}
