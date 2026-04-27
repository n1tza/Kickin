package com.nnita.kickin

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nnita.kickin.model.FixtureResponse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SampleDataTest {

    @Test
    fun readSampleFixtures() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val json = context.assets
            .open("sample_fixtures.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<FixtureResponse>>() {}.type
        val fixtures: List<FixtureResponse> = Gson().fromJson(json, type)

        assertTrue("sample_fixtures.json must contain at least one fixture", fixtures.isNotEmpty())

        Log.d("SAMPLE_TEST", "Parsed ${fixtures.size} fixtures from sample_fixtures.json")
        fixtures.forEach { f ->
            val home  = f.teams.home.name
            val away  = f.teams.away.name
            val hs    = f.goals.home?.toString() ?: "-"
            val as_   = f.goals.away?.toString() ?: "-"
            val status = f.fixture.status.short
            Log.d("SAMPLE_TEST", "$home $hs – $as_ $away  [$status]")
        }
    }
}
