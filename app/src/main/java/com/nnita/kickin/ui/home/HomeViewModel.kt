package com.nnita.kickin.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.model.FixtureResponse
import com.nnita.kickin.model.isLive
import com.nnita.kickin.model.toFixture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _allFixtures = MutableStateFlow<List<Fixture>>(emptyList())

    private val _selectedLeague = MutableStateFlow("All")
    val selectedLeague: StateFlow<String> = _selectedLeague.asStateFlow()

    val leagues: StateFlow<List<String>> = _allFixtures
        .map { fixtures -> listOf("All") + fixtures.map { it.leagueName }.distinct() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf("All"))

    val liveFixtures: StateFlow<List<Fixture>> =
        combine(_allFixtures, _selectedLeague) { fixtures, league ->
            fixtures
                .filter { it.isLive() }
                .filter { league == "All" || it.leagueName == league }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val otherFixtures: StateFlow<List<Fixture>> =
        combine(_allFixtures, _selectedLeague) { fixtures, league ->
            fixtures
                .filter { !it.isLive() }
                .filter { league == "All" || it.leagueName == league }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init { loadFixtures() }

    fun selectLeague(league: String) {
        _selectedLeague.value = league
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
