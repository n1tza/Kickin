package com.nnita.kickin.ui.home

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.model.FixtureResponse
import com.nnita.kickin.model.isLive
import com.nnita.kickin.model.toFixture
import com.nnita.kickin.ui.settings.KEY_DISPLAY_MODE
import com.nnita.kickin.ui.settings.KEY_LEAGUE
import com.nnita.kickin.ui.settings.KEY_TIME_FORMAT
import com.nnita.kickin.ui.settings.PREF_FILE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class MatchMode { TODAY, PAST }

private val leagueIdToName = mapOf(
    "39" to "Premier League",
    "140" to "La Liga",
    "135" to "Serie A",
    "78" to "Bundesliga",
    "61" to "Ligue 1"
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences =
        application.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    private val _allFixtures = MutableStateFlow<List<Fixture>>(emptyList())

    private val favName: String
        get() = leagueIdToName[prefs.getString(KEY_LEAGUE, "39")] ?: "All"

    private val _favLeagueName = MutableStateFlow(favName)
    private val _displayMode = MutableStateFlow(prefs.getString(KEY_DISPLAY_MODE, "all") ?: "all")
    private val _timeFormat = MutableStateFlow(prefs.getString(KEY_TIME_FORMAT, "24h") ?: "24h")
    private val _selectedLeague = MutableStateFlow(favName)
    private val _matchMode = MutableStateFlow(MatchMode.TODAY)
    private val _selectedDate = MutableStateFlow(LocalDate.now().minusDays(1))

    val selectedLeague: StateFlow<String> = _selectedLeague.asStateFlow()
    val matchMode: StateFlow<MatchMode> = _matchMode.asStateFlow()
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()

    val leagues: StateFlow<List<String>> = _allFixtures
        .map { fixtures -> listOf("All") + fixtures.map { it.leagueName }.distinct() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf("All"))

    private val effectiveLeague: Flow<String> =
        combine(_selectedLeague, _displayMode, _favLeagueName) { chip, dm, fav ->
            if (dm == "league") fav else chip
        }

    val liveFixtures: StateFlow<List<Fixture>> =
        combine(_allFixtures, _matchMode, effectiveLeague) { fixtures, mode, league ->
            if (mode == MatchMode.PAST) emptyList()
            else fixtures
                .filter { it.isLive() }
                .filter { league == "All" || it.leagueName == league }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val otherFixtures: StateFlow<List<Fixture>> =
        combine(_allFixtures, _matchMode, effectiveLeague, _displayMode) { fixtures, mode, league, dm ->
            if (dm == "live" && mode == MatchMode.TODAY) return@combine emptyList()
            val base = if (mode == MatchMode.PAST) {
                fixtures.filter { !it.isLive() && it.status != "NS" }
            } else {
                fixtures.filter { !it.isLive() }
            }
            base.filter { league == "All" || it.leagueName == league }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_LEAGUE -> {
                val name = leagueIdToName[prefs.getString(KEY_LEAGUE, "39")] ?: "All"
                _favLeagueName.value = name
                if (_displayMode.value == "league") _selectedLeague.value = name
            }
            KEY_DISPLAY_MODE -> {
                val dm = prefs.getString(KEY_DISPLAY_MODE, "all") ?: "all"
                _displayMode.value = dm
                if (dm == "league") _selectedLeague.value = _favLeagueName.value
            }
            KEY_TIME_FORMAT -> {
                _timeFormat.value = prefs.getString(KEY_TIME_FORMAT, "24h") ?: "24h"
            }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
        loadFixtures()
    }

    fun selectLeague(league: String) { _selectedLeague.value = league }

    fun selectMode(mode: MatchMode) {
        _matchMode.value = mode
        _selectedLeague.value = "All"
    }

    fun selectDate(date: LocalDate) { _selectedDate.value = date }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(prefsListener)
        super.onCleared()
    }

    private fun loadFixtures() {
        viewModelScope.launch(Dispatchers.IO) {
            val json = getApplication<Application>().assets
                .open("sample_fixtures.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<List<FixtureResponse>>() {}.type
            val responses: List<FixtureResponse> = Gson().fromJson(json, type)
            _allFixtures.value = responses.map { it.toFixture() }
        }
    }
}
