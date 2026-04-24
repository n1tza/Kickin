package com.nnita.kickin.ui.standings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nnita.kickin.model.LeagueIds
import com.nnita.kickin.model.Standing
import com.nnita.kickin.model.StandingsWrapper
import com.nnita.kickin.model.toStanding
import com.nnita.kickin.network.ApiResponse
import com.nnita.kickin.network.RetrofitClient
import com.nnita.kickin.ui.settings.KEY_DATA_SOURCE
import com.nnita.kickin.ui.settings.PREF_FILE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StandingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    private val _selectedLeague = MutableStateFlow("Premier League")
    val selectedLeague: StateFlow<String> = _selectedLeague.asStateFlow()

    private val _standings = MutableStateFlow<List<Standing>>(emptyList())
    val standings: StateFlow<List<Standing>> = _standings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val leagues = listOf("Premier League", "Serie A", "La Liga", "Ligue 1", "Bundesliga")

    private val leagueIdMap = mapOf(
        "Premier League" to LeagueIds.PREMIER_LEAGUE,
        "La Liga" to LeagueIds.LA_LIGA,
        "Serie A" to LeagueIds.SERIE_A,
        "Ligue 1" to LeagueIds.LIGUE_1,
        "Bundesliga" to LeagueIds.BUNDESLIGA
    )

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == KEY_DATA_SOURCE) {
            loadStandings(_selectedLeague.value)
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefListener)
        loadStandings(_selectedLeague.value)
    }

    override fun onCleared() {
        super.onCleared()
        prefs.unregisterOnSharedPreferenceChangeListener(prefListener)
    }

    fun selectLeague(league: String) {
        _selectedLeague.value = league
        loadStandings(league)
    }

    private fun loadStandings(league: String) {
        val source = prefs.getString(KEY_DATA_SOURCE, "file") ?: "file"
        if (source == "api") {
            loadFromApi(league)
        } else {
            loadFromFile(league)
        }
    }

    private fun loadFromApi(league: String) {
        val leagueId = leagueIdMap[league] ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val entries = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getStandings(leagueId, currentSeason)
                        .response
                        .firstOrNull()?.league?.standings?.firstOrNull()
                        ?: emptyList()
                }
                _standings.value = entries.map { it.toStanding() }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                loadFromFile(league)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFromFile(league: String) {
        val leagueId = leagueIdMap[league] ?: LeagueIds.PREMIER_LEAGUE
        val fileName = "standings_$leagueId.json"
        viewModelScope.launch {
            try {
                val entries = withContext(Dispatchers.IO) {
                    val json = getApplication<Application>().assets
                        .open(fileName)
                        .bufferedReader()
                        .readText()
                    val type = object : TypeToken<ApiResponse<List<StandingsWrapper>>>() {}.type
                    val apiResponse: ApiResponse<List<StandingsWrapper>> =
                        Gson().fromJson(json, type)
                    apiResponse.response
                        .firstOrNull()?.league?.standings?.firstOrNull()
                        ?: emptyList()
                }
                _standings.value = entries.map { it.toStanding() }
            } catch (e: Exception) {
                _standings.value = emptyList()
            }
        }
    }

    private val currentSeason: Int
        get() {
            val now = java.time.LocalDate.now()
            return if (now.monthValue >= 8) now.year else now.year - 1
        }
}
