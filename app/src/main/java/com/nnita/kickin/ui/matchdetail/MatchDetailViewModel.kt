package com.nnita.kickin.ui.matchdetail

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.model.FixtureResponse
import com.nnita.kickin.model.TeamStatistics
import com.nnita.kickin.model.toFixture
import com.nnita.kickin.model.toTeamStatistics
import com.nnita.kickin.network.RetrofitClient
import com.nnita.kickin.ui.settings.KEY_DATA_SOURCE
import com.nnita.kickin.ui.settings.PREF_FILE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class PredictionState {
    object Idle : PredictionState()
    object Loading : PredictionState()
    data class Ready(val homeWinPct: Float, val awayWinPct: Float) : PredictionState()
    object Error : PredictionState()
}

class MatchDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    private val _fixture = MutableStateFlow<Fixture?>(null)
    val fixture: StateFlow<Fixture?> = _fixture.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _predictionState = MutableStateFlow<PredictionState>(PredictionState.Idle)
    val predictionState: StateFlow<PredictionState> = _predictionState.asStateFlow()

    fun loadFixture(fixtureId: Int) {
        _predictionState.value = PredictionState.Idle
        val source = prefs.getString(KEY_DATA_SOURCE, "file") ?: "file"
        if (source == "api") {
            loadFromApi(fixtureId)
        } else {
            loadFromFile(fixtureId)
        }
    }

    private fun loadFromApi(fixtureId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (base, stats) = withContext(Dispatchers.IO) {
                    val fixture = RetrofitClient.instance.getFixtureById(fixtureId)
                        .response
                        .firstOrNull()
                        ?.toFixture()
                    val statistics = if (fixture != null && fixture.status != "NS") {
                        try {
                            RetrofitClient.instance.getMatchStatistics(fixtureId)
                                .response
                                .map { it.toTeamStatistics() }
                                .takeIf { it.size >= 2 }
                        } catch (e: Exception) {
                            Log.w("DETAIL_API", "Stats fetch failed, using mock: ${e.message}")
                            null
                        }
                    } else null
                    Pair(fixture, statistics)
                }
                _fixture.value = base?.let {
                    if (it.status != "NS") {
                        it.copy(statistics = stats ?: mockStats(it.homeTeam, it.awayTeam))
                    } else it
                }
            } catch (e: Exception) {
                Log.e("DETAIL_API", "Failed to load fixture $fixtureId: ${e.message}", e)
                loadFromFile(fixtureId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFromFile(fixtureId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
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
            } catch (e: Exception) {
                Log.e("DETAIL_FILE", "Failed to load fixture from file: ${e.message}")
            }
        }
    }

    fun predictMatch(homeTeamId: Int, homeTeamName: String, awayTeamId: Int, awayTeamName: String) {
        val source = prefs.getString(KEY_DATA_SOURCE, "file") ?: "file"
        if (source == "api") {
            predictFromApi(homeTeamId, awayTeamId)
        } else {
            predictFromMock(homeTeamName, awayTeamName)
        }
    }

    private fun predictFromApi(homeTeamId: Int, awayTeamId: Int) {
        viewModelScope.launch {
            _predictionState.value = PredictionState.Loading
            try {
                val (homeFixtures, awayFixtures) = withContext(Dispatchers.IO) {
                    val home = RetrofitClient.instance.getTeamLastFixtures(homeTeamId, 5).response
                    val away = RetrofitClient.instance.getTeamLastFixtures(awayTeamId, 5).response
                    Pair(home, away)
                }
                val homePoints = computeFormPoints(homeFixtures, homeTeamId) + 2 // home advantage
                val awayPoints = computeFormPoints(awayFixtures, awayTeamId)
                val total = (homePoints + awayPoints).coerceAtLeast(1)
                val homeWinPct = homePoints.toFloat() / total.toFloat()
                _predictionState.value = PredictionState.Ready(homeWinPct, 1f - homeWinPct)
            } catch (e: Exception) {
                Log.e("PREDICTOR", "Failed to fetch team form: ${e.message}")
                _predictionState.value = PredictionState.Error
            }
        }
    }

    private fun predictFromMock(homeTeamName: String, awayTeamName: String) {
        viewModelScope.launch {
            _predictionState.value = PredictionState.Loading
            delay(700)
            val homePoints = mockFormPoints(homeTeamName) + 2 // home advantage
            val awayPoints = mockFormPoints(awayTeamName)
            val total = (homePoints + awayPoints).coerceAtLeast(1)
            val homeWinPct = homePoints.toFloat() / total.toFloat()
            _predictionState.value = PredictionState.Ready(homeWinPct, 1f - homeWinPct)
        }
    }

    private fun computeFormPoints(fixtures: List<com.nnita.kickin.model.FixtureResponse>, teamId: Int): Int =
        fixtures.sumOf { f ->
            val isHome = f.teams.home.id == teamId
            when (if (isHome) f.teams.home.winner else f.teams.away.winner) {
                true -> 3
                null -> 1
                false -> 0
            }
        }

    private fun mockFormPoints(teamName: String): Int {
        val hash = teamName.sumOf { it.code }
        return (hash % 13) + 3 // 3–15, consistent for same team name
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
