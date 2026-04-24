package com.nnita.kickin.model

data class Fixture(
    val id: Int,
    val date: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamLogo: String,
    val awayTeamLogo: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val status: String,
    val elapsed: Int?,
    val leagueId: Int,
    val leagueName: String,
    val leagueLogo: String,
    val venueName: String?,
    val referee: String?,
    val statistics: List<TeamStatistics>? = null
)

data class TeamStatistics(
    val teamName: String,
    val shotsOnGoal: Int,
    val shotsOffGoal: Int,
    val totalShots: Int,
    val blockedShots: Int,
    val ballPossession: String, // e.g. "45%"
    val cornerKicks: Int,
    val fouls: Int,
    val yellowCards: Int,
    val redCards: Int,
    val offsides: Int,
    val passesPercentage: String // e.g. "82%"
)

fun Fixture.isLive() = status in setOf("1H", "2H", "HT", "ET", "BT", "P", "SUSP", "INT")

fun FixtureResponse.toFixture() = Fixture(
    id = fixture.id,
    date = fixture.date,
    homeTeam = teams.home.name,
    awayTeam = teams.away.name,
    homeTeamLogo = teams.home.logo,
    awayTeamLogo = teams.away.logo,
    homeScore = goals.home,
    awayScore = goals.away,
    status = fixture.status.short,
    elapsed = fixture.status.elapsed,
    leagueId = league.id,
    leagueName = league.name,
    leagueLogo = league.logo,
    venueName = fixture.venue?.name,
    referee = fixture.referee
)
