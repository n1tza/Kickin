package com.nnita.kickin.ui.preview

import com.nnita.kickin.model.Fixture

val previewFixtureFT = Fixture(
    id = 1001,
    date = "2026-04-22T14:00:00+00:00",
    homeTeam = "Liverpool",
    awayTeam = "Manchester United",
    homeTeamLogo = "",
    awayTeamLogo = "",
    homeScore = 3,
    awayScore = 1,
    status = "FT",
    elapsed = 90,
    leagueId = 39,
    leagueName = "Premier League",
    leagueLogo = "",
    venueName = "Anfield",
    referee = "Michael Oliver"
)

val previewFixtureLive = Fixture(
    id = 1002,
    date = "2026-04-22T14:00:00+00:00",
    homeTeam = "Manchester City",
    awayTeam = "Arsenal",
    homeTeamLogo = "",
    awayTeamLogo = "",
    homeScore = 1,
    awayScore = 1,
    status = "1H",
    elapsed = 38,
    leagueId = 39,
    leagueName = "Premier League",
    leagueLogo = "",
    venueName = "Etihad Stadium",
    referee = "Anthony Taylor"
)

val previewFixtureHT = Fixture(
    id = 1003,
    date = "2026-04-22T14:00:00+00:00",
    homeTeam = "Atalanta",
    awayTeam = "Inter",
    homeTeamLogo = "",
    awayTeamLogo = "",
    homeScore = 2,
    awayScore = 3,
    status = "2H",
    elapsed = 72,
    leagueId = 135,
    leagueName = "Serie A",
    leagueLogo = "",
    venueName = "Gewiss Stadium",
    referee = "Daniele Orsato"
)

val previewFixtureNS = Fixture(
    id = 1004,
    date = "2026-04-22T19:00:00+00:00",
    homeTeam = "Real Madrid",
    awayTeam = "Barcelona",
    homeTeamLogo = "",
    awayTeamLogo = "",
    homeScore = null,
    awayScore = null,
    status = "NS",
    elapsed = null,
    leagueId = 140,
    leagueName = "La Liga",
    leagueLogo = "",
    venueName = "Santiago Bernabéu",
    referee = "Carlos del Cerro Grande"
)

val previewFixtureNS2 = Fixture(
    id = 1005,
    date = "2026-04-22T18:45:00+00:00",
    homeTeam = "Paris Saint Germain",
    awayTeam = "Monaco",
    homeTeamLogo = "",
    awayTeamLogo = "",
    homeScore = null,
    awayScore = null,
    status = "NS",
    elapsed = null,
    leagueId = 61,
    leagueName = "Ligue 1",
    leagueLogo = "",
    venueName = "Parc des Princes",
    referee = "Clément Turpin"
)

val previewLiveFixtures = listOf(previewFixtureLive, previewFixtureHT)
val previewOtherFixtures = listOf(previewFixtureFT, previewFixtureNS, previewFixtureNS2)
val previewLeagues = listOf("All", "Premier League", "Serie A", "La Liga", "Ligue 1")
