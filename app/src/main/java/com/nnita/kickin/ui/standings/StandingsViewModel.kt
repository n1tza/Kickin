package com.nnita.kickin.ui.standings

import androidx.lifecycle.ViewModel
import com.nnita.kickin.model.Standing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StandingsViewModel : ViewModel() {

    private val _selectedLeague = MutableStateFlow("Premier League")
    val selectedLeague: StateFlow<String> = _selectedLeague.asStateFlow()

    private val _standings = MutableStateFlow<List<Standing>>(emptyList())
    val standings: StateFlow<List<Standing>> = _standings.asStateFlow()

    val leagues = listOf("Premier League", "Serie A", "La Liga", "Ligue 1", "Bundesliga")

    init {
        loadStandings(_selectedLeague.value)
    }

    fun selectLeague(league: String) {
        _selectedLeague.value = league
        loadStandings(league)
    }

    private fun loadStandings(league: String) {
        val mockStandings = when (league) {
            "Premier League" -> listOf(
                Standing(1, "Arsenal", "", 34, 24, 5, 5, 82, 26, 56, 77, "WWWLW", "Champions League"),
                Standing(2, "Manchester City", "", 33, 23, 7, 3, 82, 32, 50, 76, "WWWWD", "Champions League"),
                Standing(3, "Liverpool", "", 34, 22, 8, 4, 75, 34, 41, 74, "LDWDW", "Champions League"),
                Standing(4, "Aston Villa", "", 34, 20, 6, 8, 71, 50, 21, 66, "WDWLW", "Champions League"),
                Standing(5, "Tottenham", "", 32, 18, 6, 8, 65, 49, 16, 60, "LWDLW", "Europa League"),
                Standing(6, "Newcastle", "", 33, 15, 5, 13, 74, 54, 20, 50, "WWDWL", null),
                Standing(7, "Manchester United", "", 33, 15, 5, 13, 47, 48, -1, 50, "DDWLD", null),
                Standing(8, "West Ham", "", 34, 13, 9, 12, 54, 63, -9, 48, "LLDWL", null),
                Standing(9, "Chelsea", "", 32, 13, 8, 11, 61, 57, 4, 47, "WDWWD", null),
                Standing(10, "Bournemouth", "", 34, 12, 9, 13, 49, 60, -11, 45, "WLLDW", null),
                Standing(11, "Wolves", "", 34, 12, 7, 15, 46, 54, -8, 43, "LLDLD", null),
                Standing(12, "Fulham", "", 34, 12, 6, 16, 50, 54, -4, 42, "LWLLD", null),
                Standing(13, "Brighton", "", 32, 11, 11, 10, 52, 50, 2, 44, "LDDLW", null),
                Standing(14, "Crystal Palace", "", 34, 10, 9, 15, 44, 56, -12, 39, "WWWLD", null),
                Standing(15, "Everton", "", 34, 10, 8, 16, 34, 48, -14, 33, "WWLWL", null),
                Standing(16, "Brentford", "", 34, 9, 8, 17, 52, 60, -8, 35, "WWDDL", null),
                Standing(17, "Nottingham Forest", "", 34, 7, 9, 18, 42, 60, -18, 26, "LLDWL", null),
                Standing(18, "Luton", "", 34, 6, 7, 21, 47, 75, -28, 25, "LLLWL", "Relegation"),
                Standing(19, "Burnley", "", 34, 5, 8, 21, 37, 69, -32, 23, "WDLWD", "Relegation"),
                Standing(20, "Sheffield Utd", "", 34, 3, 7, 24, 33, 92, -59, 16, "LLLDL", "Relegation")
            )
            "La Liga" -> listOf(
                Standing(1, "Real Madrid", "", 33, 26, 6, 1, 71, 22, 49, 84, "WWWWW", "Champions League"),
                Standing(2, "Barcelona", "", 33, 22, 7, 4, 68, 39, 29, 73, "WLWWW", "Champions League"),
                Standing(3, "Girona", "", 33, 22, 5, 6, 69, 40, 29, 71, "WLLWL", "Champions League"),
                Standing(4, "Atletico Madrid", "", 33, 20, 4, 9, 62, 39, 23, 64, "WLWWW", "Champions League"),
                Standing(5, "Athletic Club", "", 33, 16, 10, 7, 53, 33, 20, 58, "DLDWW", "Europa League"),
                Standing(6, "Real Sociedad", "", 33, 13, 12, 8, 46, 35, 11, 51, "DDWWW", null),
                Standing(7, "Real Betis", "", 33, 12, 13, 8, 41, 39, 2, 49, "WWDLL", null),
                Standing(8, "Valencia", "", 33, 13, 8, 12, 37, 35, 2, 47, "LLWWD", null),
                Standing(9, "Villarreal", "", 33, 12, 9, 12, 54, 55, -1, 45, "WWLDW", null),
                Standing(10, "Getafe", "", 33, 10, 13, 10, 41, 45, -4, 43, "WDLWD", null),
                Standing(11, "Osasuna", "", 33, 11, 6, 16, 37, 49, -12, 39, "LLLLW", null),
                Standing(12, "Alaves", "", 33, 10, 8, 15, 31, 38, -7, 38, "WWLLL", null),
                Standing(13, "Sevilla", "", 33, 9, 11, 13, 42, 46, -4, 38, "DWWW L", null),
                Standing(14, "Las Palmas", "", 33, 10, 7, 16, 29, 41, -12, 37, "LLLLL", null),
                Standing(15, "Rayo Vallecano", "", 33, 7, 13, 13, 27, 42, -15, 34, "LDWDL", null),
                Standing(16, "Mallorca", "", 33, 6, 14, 13, 27, 39, -12, 32, "DLLWL", null),
                Standing(17, "Celta Vigo", "", 33, 7, 10, 16, 37, 50, -13, 31, "LWDLW", null),
                Standing(18, "Cadiz", "", 33, 4, 14, 15, 23, 46, -23, 26, "DLLOW", "Relegation"),
                Standing(19, "Granada", "", 33, 3, 9, 21, 36, 61, -25, 18, "WWDLL", "Relegation"),
                Standing(20, "Almeria", "", 33, 1, 11, 21, 32, 67, -35, 14, "LLDLW", "Relegation")
            )
            "Serie A" -> listOf(
                Standing(1, "Inter", "", 34, 28, 5, 1, 81, 18, 63, 89, "WWWDW", "Champions League"),
                Standing(2, "AC Milan", "", 34, 21, 7, 6, 64, 39, 25, 70, "DLDWW", "Champions League"),
                Standing(3, "Juventus", "", 34, 18, 11, 5, 47, 26, 21, 65, "DDDWL", "Champions League"),
                Standing(4, "Bologna", "", 34, 17, 12, 5, 49, 27, 22, 63, "DDWWD", "Champions League"),
                Standing(5, "AS Roma", "", 34, 17, 8, 9, 61, 41, 20, 59, "DLWWD", "Champions League"),
                Standing(6, "Atalanta", "", 33, 17, 6, 10, 61, 37, 24, 57, "WWDLW", null),
                Standing(7, "Lazio", "", 34, 17, 4, 13, 43, 35, 8, 55, "WWWLW", null),
                Standing(8, "Fiorentina", "", 33, 14, 8, 11, 50, 37, 13, 50, "WWLDD", null),
                Standing(9, "Napoli", "", 34, 13, 11, 10, 52, 43, 9, 50, "DDWLD", null),
                Standing(10, "Torino", "", 34, 11, 13, 10, 31, 31, 0, 46, "DDLWW", null),
                Standing(11, "Monza", "", 34, 11, 11, 12, 35, 44, -9, 44, "D LLLD", null),
                Standing(12, "Genoa", "", 34, 10, 12, 12, 38, 40, -2, 42, "WDLDW", null),
                Standing(13, "Lecce", "", 34, 8, 12, 14, 31, 49, -18, 36, "DWWLD", null),
                Standing(14, "Cagliari", "", 34, 7, 11, 16, 36, 59, -23, 32, "LDDWW", null),
                Standing(15, "Verona", "", 34, 7, 10, 17, 31, 45, -14, 31, "LWLDD", null),
                Standing(16, "Empoli", "", 34, 8, 7, 19, 26, 50, -24, 31, "LWL LL", null),
                Standing(17, "Frosinone", "", 34, 7, 10, 17, 43, 63, -20, 31, "WDDDD", null),
                Standing(18, "Udinese", "", 33, 4, 16, 13, 31, 50, -19, 28, "LDLLL", "Relegation"),
                Standing(19, "Sassuolo", "", 34, 6, 8, 20, 40, 70, -30, 26, "LLD D L", "Relegation"),
                Standing(20, "Salernitana", "", 34, 2, 9, 23, 26, 73, -47, 15, "LLDLL", "Relegation")
            )
            "Ligue 1" -> listOf(
                Standing(1, "Paris Saint Germain", "", 31, 20, 10, 1, 76, 29, 47, 70, "DWWWD", "Champions League"),
                Standing(2, "Monaco", "", 31, 17, 7, 7, 61, 42, 19, 58, "LWWWW", "Champions League"),
                Standing(3, "Brest", "", 31, 16, 8, 7, 49, 33, 16, 56, "WLLWW", "Champions League"),
                Standing(4, "Lille", "", 31, 15, 10, 6, 45, 27, 18, 55, "LWWDD", "Champions League"),
                Standing(5, "Nice", "", 31, 14, 9, 8, 36, 25, 11, 51, "DWWLW", "Europa League"),
                Standing(6, "Lens", "", 31, 13, 7, 11, 39, 34, 5, 46, "WLDLL", null),
                Standing(7, "Lyon", "", 31, 13, 5, 13, 42, 51, -9, 44, "WLWWW", null),
                Standing(8, "Marseille", "", 31, 11, 11, 9, 47, 39, 8, 44, "WDDLL", null),
                Standing(9, "Rennes", "", 31, 12, 6, 13, 48, 41, 7, 42, "WLLLW", null),
                Standing(10, "Reims", "", 31, 11, 7, 13, 38, 45, -7, 40, "LLLDD", null),
                Standing(11, "Toulouse", "", 31, 10, 10, 11, 38, 40, -2, 40, "WDWWD", null),
                Standing(12, "Montpellier", "", 31, 9, 11, 11, 39, 44, -5, 37, "DWWWD", null),
                Standing(13, "Strasbourg", "", 31, 9, 9, 13, 34, 44, -10, 36, "LLWWD", null),
                Standing(14, "Nantes", "", 31, 9, 5, 17, 29, 49, -20, 32, "DLWLW", null),
                Standing(15, "Le Havre", "", 31, 6, 11, 14, 30, 41, -11, 29, "DLLDL", null),
                Standing(16, "Metz", "", 31, 8, 5, 18, 32, 51, -19, 29, "LLWWL", "Relegation Play-off"),
                Standing(17, "Lorient", "", 31, 6, 8, 17, 36, 61, -25, 26, "LLLLL", "Relegation"),
                Standing(18, "Clermont", "", 31, 5, 10, 16, 25, 52, -27, 25, "WLDLD", "Relegation")
            )
            "Bundesliga" -> listOf(
                Standing(1, "Bayer Leverkusen", "", 31, 25, 6, 0, 77, 22, 55, 81, "DDWWW", "Champions League"),
                Standing(2, "Bayern Munich", "", 31, 22, 3, 6, 89, 38, 51, 69, "WWWLL", "Champions League"),
                Standing(3, "VfB Stuttgart", "", 31, 20, 4, 7, 70, 38, 32, 64, "LWWWD", "Champions League"),
                Standing(4, "RB Leipzig", "", 31, 19, 5, 7, 73, 35, 38, 62, "WWWWW", "Champions League"),
                Standing(5, "Borussia Dortmund", "", 31, 16, 9, 6, 59, 39, 20, 57, "LDWDL", "Champions League"),
                Standing(6, "Eintracht Frankfurt", "", 31, 11, 12, 8, 47, 44, 3, 45, "LWLDD", null),
                Standing(7, "SC Freiburg", "", 31, 11, 7, 13, 43, 55, -12, 40, "DLWLW", null),
                Standing(8, "Augsburg", "", 31, 10, 9, 12, 48, 52, -4, 39, "LLLWD", null),
                Standing(9, "Hoffenheim", "", 31, 11, 6, 14, 55, 63, -8, 39, "LWLLW", null),
                Standing(10, "Werder Bremen", "", 31, 10, 7, 14, 41, 50, -9, 37, "WWLLL", null),
                Standing(11, "Heidenheim", "", 31, 9, 10, 12, 45, 52, -7, 37, "WLDDW", null),
                Standing(12, "Wolfsburg", "", 31, 9, 7, 15, 37, 50, -13, 34, "WWLWL", null),
                Standing(13, "Borussia M.Gladbach", "", 31, 7, 11, 13, 53, 60, -7, 32, "DLLWL", null),
                Standing(14, "Union Berlin", "", 31, 8, 6, 17, 26, 50, -24, 30, "LDLLL", null),
                Standing(15, "Bochum", "", 31, 7, 9, 15, 40, 62, -22, 30, "WLDLL", null),
                Standing(16, "Mainz 05", "", 31, 5, 13, 13, 32, 48, -16, 28, "DDWWW", "Relegation Play-off"),
                Standing(17, "FC Koln", "", 31, 4, 11, 16, 24, 54, -30, 23, "DLDWL", "Relegation"),
                Standing(18, "Darmstadt", "", 31, 3, 8, 20, 30, 73, -43, 17, "LWLLL", "Relegation")
            )
            else -> emptyList()
        }
        _standings.value = mockStandings
    }
}
