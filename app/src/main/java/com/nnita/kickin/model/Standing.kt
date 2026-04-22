package com.nnita.kickin.model

data class Standing(
    val rank: Int,
    val teamName: String,
    val teamLogo: String,
    val played: Int,
    val win: Int,
    val draw: Int,
    val loss: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalsDiff: Int,
    val points: Int,
    val form: String,
    val description: String?
)
