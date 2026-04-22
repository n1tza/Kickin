package com.nnita.kickin.ui.matchdetail

import androidx.lifecycle.ViewModel
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.model.TeamStatistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchDetailViewModel : ViewModel() {
    private val _fixture = MutableStateFlow<Fixture?>(null)
    val fixture: StateFlow<Fixture?> = _fixture.asStateFlow()

    fun loadFixture(fixtureId: Int) {
        // Mock data for the specific fixture
        _fixture.value = Fixture(
            id = fixtureId,
            date = "2026-04-22T19:00:00+00:00",
            homeTeam = "Real Madrid",
            awayTeam = "Barcelona",
            homeTeamLogo = "",
            awayTeamLogo = "",
            homeScore = 3,
            awayScore = 2,
            status = "FT",
            elapsed = 90,
            leagueName = "La Liga",
            leagueLogo = "",
            venueName = "Santiago Bernabéu",
            referee = "Carlos del Cerro Grande",
            statistics = listOf(
                TeamStatistics(
                    teamName = "Real Madrid",
                    shotsOnGoal = 8,
                    shotsOffGoal = 5,
                    totalShots = 15,
                    blockedShots = 2,
                    ballPossession = "45%",
                    cornerKicks = 6,
                    fouls = 12,
                    yellowCards = 2,
                    redCards = 0,
                    offsides = 3,
                    passesPercentage = "82%"
                ),
                TeamStatistics(
                    teamName = "Barcelona",
                    shotsOnGoal = 6,
                    shotsOffGoal = 7,
                    totalShots = 18,
                    blockedShots = 5,
                    ballPossession = "55%",
                    cornerKicks = 4,
                    fouls = 10,
                    yellowCards = 3,
                    redCards = 0,
                    offsides = 1,
                    passesPercentage = "88%"
                )
            )
        )
    }
}
