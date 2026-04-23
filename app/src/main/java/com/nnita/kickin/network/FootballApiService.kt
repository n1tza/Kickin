package com.nnita.kickin.network

import com.nnita.kickin.model.StandingsWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApiService {

    @GET("standings")
    suspend fun getStandings(
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): ApiResponse<List<StandingsWrapper>>
}
