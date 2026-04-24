package com.nnita.kickin.network

import com.nnita.kickin.model.FixtureResponse
import com.nnita.kickin.model.MatchStatisticsResponse
import com.nnita.kickin.model.StandingsWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApiService {

    @GET("fixtures")
    suspend fun getFixturesByDate(
        @Query("date") date: String,
        @Query("timezone") timezone: String = "America/New_York"
    ): ApiResponse<List<FixtureResponse>>

    @GET("fixtures")
    suspend fun getFixtureById(
        @Query("ids") fixtureId: Int
    ): ApiResponse<List<FixtureResponse>>

    @GET("fixtures/statistics")
    suspend fun getMatchStatistics(
        @Query("fixture") fixtureId: Int
    ): ApiResponse<List<MatchStatisticsResponse>>

    @GET("standings")
    suspend fun getStandings(
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): ApiResponse<List<StandingsWrapper>>
}
