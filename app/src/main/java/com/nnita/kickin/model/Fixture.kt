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
    val leagueName: String,
    val leagueLogo: String,
    val venueName: String,
    val referee: String?
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
    leagueName = league.name,
    leagueLogo = league.logo,
    venueName = fixture.venue.name,
    referee = fixture.referee
)
