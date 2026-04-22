package com.nnita.kickin.ui.matchdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.model.FixtureResponse
import com.nnita.kickin.model.TeamStatistics
import com.nnita.kickin.model.toFixture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MatchDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _fixture = MutableStateFlow<Fixture?>(null)
    val fixture: StateFlow<Fixture?> = _fixture.asStateFlow()

    fun loadFixture(fixtureId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val json = getApplication<Application>().assets
                .open("sample_fixtures.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<List<FixtureResponse>>() {}.type
            val responses: List<FixtureResponse> = Gson().fromJson(json, type)
            val base = responses.map { it.toFixture() }.find { it.id == fixtureId }
                ?: return@launch
            _fixture.value = if (base.status != "NS") {
                base.copy(statistics = mockStats(base.homeTeam, base.awayTeam))
            } else {
                base
            }
        }
    }

    private fun mockStats(homeTeam: String, awayTeam: String) = listOf(
        TeamStatistics(
            teamName = homeTeam,
            shotsOnGoal = 7, shotsOffGoal = 4, totalShots = 13, blockedShots = 2,
            ballPossession = "52%", cornerKicks = 5, fouls = 11,
            yellowCards = 1, redCards = 0, offsides = 2, passesPercentage = "84%"
        ),
        TeamStatistics(
            teamName = awayTeam,
            shotsOnGoal = 5, shotsOffGoal = 6, totalShots = 14, blockedShots = 3,
            ballPossession = "48%", cornerKicks = 3, fouls = 9,
            yellowCards = 2, redCards = 0, offsides = 1, passesPercentage = "81%"
        )
    )
}
