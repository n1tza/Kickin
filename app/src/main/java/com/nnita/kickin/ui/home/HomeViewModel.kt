package com.nnita.kickin.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.model.FixtureResponse
import com.nnita.kickin.model.toFixture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _fixtures = MutableStateFlow<List<Fixture>>(emptyList())
    val fixtures: StateFlow<List<Fixture>> = _fixtures.asStateFlow()

    init {
        loadFixtures()
    }

    private fun loadFixtures() {
        viewModelScope.launch(Dispatchers.IO) {
            val json = getApplication<Application>().assets
                .open("sample_fixtures.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<List<FixtureResponse>>() {}.type
            val responses: List<FixtureResponse> = Gson().fromJson(json, type)
            _fixtures.value = responses.map { it.toFixture() }
        }
    }
}
