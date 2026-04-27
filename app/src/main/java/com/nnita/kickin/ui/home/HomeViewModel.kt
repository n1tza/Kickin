package com.nnita.kickin.ui.home

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.model.FixtureResponse
import com.nnita.kickin.model.LeagueIds
import com.nnita.kickin.model.LeagueSummary
import com.nnita.kickin.model.isLive
import com.nnita.kickin.model.toFixture
import com.nnita.kickin.network.RetrofitClient
import com.nnita.kickin.ui.settings.KEY_DATA_SOURCE
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
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class MatchMode { TODAY, PAST }
enum class SortOrder { TIME_ASC, TIME_DESC, ALPHA }

private val mainLeagueIds = setOf(
    LeagueIds.PREMIER_LEAGUE,
    LeagueIds.LA_LIGA,
    LeagueIds.SERIE_A,
    LeagueIds.BUNDESLIGA,
    LeagueIds.LIGUE_1
)

private val mainLeagueNames = listOf(
    "Premier League", "La Liga", "Serie A", "Bundesliga", "Ligue 1"
)

private val leagueIdToName = mapOf(
    "39" to "Premier League",
    "140" to "La Liga",
    "135" to "Serie A",
    "78" to "Bundesliga",
    "61" to "Ligue 1"
)

private val mainLeagueNameToId = mapOf(
    "Premier League" to LeagueIds.PREMIER_LEAGUE,
    "La Liga" to LeagueIds.LA_LIGA,
    "Serie A" to LeagueIds.SERIE_A,
    "Bundesliga" to LeagueIds.BUNDESLIGA,
    "Ligue 1" to LeagueIds.LIGUE_1
)

private fun Fixture.matchesLeague(league: String): Boolean {
    if (league == "All") return true
    val id = mainLeagueNameToId[league]
    return if (id != null) leagueId == id else leagueName == league
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences =
        application.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    private data class FilterState(
        val countries: Set<String>,
        val leagueIds: Set<Int>,
        val sort: SortOrder
    )

    private val _allFixtures = MutableStateFlow<List<Fixture>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedCountries = MutableStateFlow<Set<String>>(emptySet())
    private val _selectedLeagueFilterIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _sortOrder = MutableStateFlow(SortOrder.TIME_ASC)

    val selectedCountries: StateFlow<Set<String>> = _selectedCountries.asStateFlow()
    val selectedLeagueFilterIds: StateFlow<Set<Int>> = _selectedLeagueFilterIds.asStateFlow()
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val favName: String
        get() = leagueIdToName[prefs.getString(KEY_LEAGUE, "39")] ?: "All"

    private val _favLeagueName = MutableStateFlow(favName)
    private val _displayMode = MutableStateFlow("all")
    private val _timeFormat = MutableStateFlow(prefs.getString(KEY_TIME_FORMAT, "24h") ?: "24h")
    private val _selectedLeague = MutableStateFlow(favName)
    private val _matchMode = MutableStateFlow(MatchMode.TODAY)
    private val _selectedDate = MutableStateFlow(LocalDate.now().minusDays(1))

    val selectedLeague: StateFlow<String> = _selectedLeague.asStateFlow()
    val matchMode: StateFlow<MatchMode> = _matchMode.asStateFlow()
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()

    // Fixed chip list — always the same 5 leagues regardless of what's loaded
    val leagues: StateFlow<List<String>> = MutableStateFlow(listOf("All") + mainLeagueNames)

    private val effectiveLeague: Flow<String> =
        combine(_selectedLeague, _displayMode, _favLeagueName) { chip, dm, fav ->
            if (dm == "league") fav else chip
        }

    private val filterState: Flow<FilterState> =
        combine(_selectedCountries, _selectedLeagueFilterIds, _sortOrder) { countries, ids, sort ->
            FilterState(countries, ids, sort)
        }

    val liveFixtures: StateFlow<List<Fixture>> =
        combine(_allFixtures, _matchMode, effectiveLeague) { fixtures, mode, league ->
            if (mode == MatchMode.PAST) emptyList()
            else fixtures
                .filter { it.isLive() }
                .filter { it.matchesLeague(league) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val otherFixtures: StateFlow<List<Fixture>> =
        combine(_allFixtures, _matchMode, effectiveLeague, _displayMode, filterState) { fixtures, mode, league, dm, filter ->
            if (dm == "live" && mode == MatchMode.TODAY) return@combine emptyList()
            val base = if (mode == MatchMode.PAST) {
                fixtures.filter { !it.isLive() && it.status != "NS" }
            } else {
                fixtures.filter { !it.isLive() }
            }
            var result = base.filter { it.matchesLeague(league) }
            if (league == "All") {
                if (filter.countries.isNotEmpty()) result = result.filter { it.leagueCountry in filter.countries }
                if (filter.leagueIds.isNotEmpty()) result = result.filter { it.leagueId in filter.leagueIds }
            }
            when (filter.sort) {
                SortOrder.ALPHA -> result.sortedBy { it.homeTeam }
                SortOrder.TIME_DESC -> result.sortedByDescending { it.date }
                SortOrder.TIME_ASC -> result.sortedBy { it.date }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Grouped view used when "All" is selected — main leagues first (by ID), then others alphabetically
    private val mainLeagueIdOrder = listOf(
        LeagueIds.PREMIER_LEAGUE, LeagueIds.LA_LIGA, LeagueIds.SERIE_A,
        LeagueIds.BUNDESLIGA, LeagueIds.LIGUE_1
    )

    val groupedOtherFixtures: StateFlow<List<Pair<String, List<Fixture>>>> =
        combine(otherFixtures, effectiveLeague) { fixtures, league ->
            if (league != "All") return@combine emptyList()
            val grouped = fixtures.groupBy { it.leagueId }
            mainLeagueIdOrder.mapNotNull { id ->
                grouped[id]?.let { list -> list.first().leagueName to list }
            } +
                grouped.entries
                    .filter { it.key !in mainLeagueIds }
                    .map { it.value.first().leagueName to it.value }
                    .sortedBy { it.first }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Leagues not in the main 5 that have fixtures — used for the "More..." bottom sheet
    val otherLeagues: StateFlow<List<LeagueSummary>> = _allFixtures
        .map { fixtures ->
            fixtures
                .filter { it.leagueId !in mainLeagueIds }
                .distinctBy { it.leagueId }
                .map { LeagueSummary(it.leagueId, it.leagueName, it.leagueLogo, it.leagueCountry) }
                .sortedBy { it.name }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // All distinct countries with fixtures — used for the filter dialog
    val availableCountries: StateFlow<List<String>> = _allFixtures
        .map { fixtures ->
            fixtures.map { it.leagueCountry }.filter { it.isNotBlank() }.distinct().sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // All leagues with fixtures — used for the filter dialog (includes main 5)
    val availableFilterLeagues: StateFlow<List<LeagueSummary>> = _allFixtures
        .map { fixtures ->
            fixtures.distinctBy { it.leagueId }
                .map { LeagueSummary(it.leagueId, it.leagueName, it.leagueLogo, it.leagueCountry) }
                .sortedBy { it.name }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_LEAGUE -> {
                val name = leagueIdToName[prefs.getString(KEY_LEAGUE, "39")] ?: "All"
                _favLeagueName.value = name
                _selectedLeague.value = name
            }
            KEY_TIME_FORMAT -> {
                _timeFormat.value = prefs.getString(KEY_TIME_FORMAT, "24h") ?: "24h"
            }
            KEY_DATA_SOURCE -> loadFixtures()
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
        loadFixtures()
    }

    fun selectLeague(league: String) { _selectedLeague.value = league }

    fun refresh() = loadFixtures()

    fun selectMode(mode: MatchMode) {
        _matchMode.value = mode
        _selectedLeague.value = "All"
        _selectedCountries.value = emptySet()
        _selectedLeagueFilterIds.value = emptySet()
        loadFixtures()
    }

    fun setCountryFilter(countries: Set<String>) { _selectedCountries.value = countries }
    fun setLeagueFilter(ids: Set<Int>) { _selectedLeagueFilterIds.value = ids }
    fun setSortOrder(order: SortOrder) { _sortOrder.value = order }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadFixtures()
    }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(prefsListener)
        super.onCleared()
    }

    private fun loadFixtures() {
        val source = prefs.getString(KEY_DATA_SOURCE, "file") ?: "file"
        val date = if (_matchMode.value == MatchMode.TODAY) LocalDate.now() else _selectedDate.value
        if (source == "api") {
            loadFixturesFromApi(date)
        } else {
            loadFixturesFromFile()
        }
    }

    private fun loadFixturesFromApi(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                Log.d("HOME_API", "Fetching fixtures for date: $dateStr")
                val fixtures = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getFixturesByDate(dateStr)
                        .response
                        .map { it.toFixture() }
                }
                Log.d("HOME_API", "Loaded ${fixtures.size} fixtures from API")
                _allFixtures.value = fixtures
            } catch (e: Exception) {
                Log.e("HOME_API", "API call failed: ${e.javaClass.simpleName}: ${e.message}", e)
                _error.value = e.message ?: e.javaClass.simpleName
                loadFixturesFromFile()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFixturesFromFile() {
        viewModelScope.launch {
            try {
                val fixtures = withContext(Dispatchers.IO) {
                    val json = getApplication<Application>().assets
                        .open("sample_fixtures.json")
                        .bufferedReader()
                        .use { it.readText() }
                    val type = object : TypeToken<List<FixtureResponse>>() {}.type
                    val responses: List<FixtureResponse> = Gson().fromJson(json, type)
                    responses.map { it.toFixture() }
                }
                _allFixtures.value = fixtures
            } catch (e: Exception) {
                _allFixtures.value = emptyList()
            }
        }
    }
}
