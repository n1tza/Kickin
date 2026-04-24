package com.nnita.kickin.model

data class FixtureResponse(
    val fixture: FixtureInfo,
    val league: LeagueInfo,
    val teams: TeamsInfo,
    val goals: GoalsInfo
)

data class FixtureInfo(
    val id: Int,
    val date: String,
    val status: StatusInfo,
    val venue: VenueInfo?,
    val referee: String?
)

data class StatusInfo(
    val long: String,
    val short: String,
    val elapsed: Int?
)

data class VenueInfo(
    val name: String?,
    val city: String?
)

data class LeagueInfo(
    val id: Int,
    val name: String,
    val logo: String,
    val country: String
)

data class TeamsInfo(
    val home: TeamInfo,
    val away: TeamInfo
)

data class TeamInfo(
    val id: Int,
    val name: String,
    val logo: String,
    val winner: Boolean?
)

data class GoalsInfo(
    val home: Int?,
    val away: Int?
)
