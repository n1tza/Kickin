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
    leagueName = "Premier League",
    leagueLogo = "",
    venueName = "Etihad Stadium",
    referee = "Anthony Taylor"
)

val previewFixtureNS = Fixture(
    id = 1003,
    date = "2026-04-22T19:00:00+00:00",
    homeTeam = "Real Madrid",
    awayTeam = "Barcelona",
    homeTeamLogo = "",
    awayTeamLogo = "",
    homeScore = null,
    awayScore = null,
    status = "NS",
    elapsed = null,
    leagueName = "La Liga",
    leagueLogo = "",
    venueName = "Santiago Bernabéu",
    referee = "Carlos del Cerro Grande"
)

val previewFixtures = listOf(previewFixtureFT, previewFixtureLive, previewFixtureNS)
