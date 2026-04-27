package com.nnita.kickin

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nnita.kickin.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class LiveApiTest {

    @Test
    fun fetchFixturesByDate() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val url = "https://v3.football.api-sports.io/fixtures" +
                "?date=$today&timezone=America/New_York"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("x-apisports-key", BuildConfig.FOOTBALL_API_KEY)
            .build()

        val response = client.newCall(request).execute()

        assertEquals("Expected HTTP 200", 200, response.code)

        val body = response.body?.string() ?: ""
        assertTrue("Response body should not be empty", body.isNotEmpty())

        Log.d("API_TEST", "Fetched fixtures for date: $today")
        Log.d("API_TEST", body)
    }
}
