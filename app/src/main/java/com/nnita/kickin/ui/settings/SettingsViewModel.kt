package com.nnita.kickin.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    private val _selectedLeagueId = MutableStateFlow(
        prefs.getString(KEY_LEAGUE, "39") ?: "39"
    )
    val selectedLeagueId: StateFlow<String> = _selectedLeagueId.asStateFlow()

    private val _displayMode = MutableStateFlow(
        prefs.getString(KEY_DISPLAY_MODE, "all") ?: "all"
    )
    val displayMode: StateFlow<String> = _displayMode.asStateFlow()

    private val _timeFormat = MutableStateFlow(
        prefs.getString(KEY_TIME_FORMAT, "24h") ?: "24h"
    )
    val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()

    private val _dataSource = MutableStateFlow(
        prefs.getString(KEY_DATA_SOURCE, "file") ?: "file"
    )
    val dataSource: StateFlow<String> = _dataSource.asStateFlow()

    fun selectLeague(leagueId: String) {
        _selectedLeagueId.value = leagueId
        prefs.edit().putString(KEY_LEAGUE, leagueId).apply()
    }

    fun selectDisplayMode(mode: String) {
        _displayMode.value = mode
        prefs.edit().putString(KEY_DISPLAY_MODE, mode).apply()
    }

    fun setTimeFormat(format: String) {
        _timeFormat.value = format
        prefs.edit().putString(KEY_TIME_FORMAT, format).apply()
    }

    fun setDataSource(source: String) {
        _dataSource.value = source
        prefs.edit().putString(KEY_DATA_SOURCE, source).apply()
    }
}
