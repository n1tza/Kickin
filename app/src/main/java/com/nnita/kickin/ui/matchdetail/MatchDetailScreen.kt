package com.nnita.kickin.ui.matchdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.nnita.kickin.R
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.model.TeamStatistics
import com.nnita.kickin.ui.components.TeamIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    fixtureId: Int,
    onBackClick: () -> Unit = {},
    viewModel: MatchDetailViewModel = viewModel()
) {
    val fixture by viewModel.fixture.collectAsState()

    LaunchedEffect(fixtureId) {
        viewModel.loadFixture(fixtureId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_home)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        fixture?.let { fix ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MatchHeader(fix)
                Spacer(modifier = Modifier.height(24.dp))
                
                if (fix.statistics != null && fix.statistics.size >= 2) {
                    Text(
                        text = "Match Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    StatsSection(fix.statistics[0], fix.statistics[1])
                }
            }
        }
    }
}

@Composable
private fun MatchHeader(fixture: Fixture) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = fixture.leagueName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamDisplay(name = fixture.homeTeam)
                ScoreDisplay(homeScore = fixture.homeScore ?: 0, awayScore = fixture.awayScore ?: 0)
                TeamDisplay(name = fixture.awayTeam)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = fixture.status,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (fixture.status == "FT") Color.Gray else Color.Red
            )
        }
    }
}

@Composable
private fun TeamDisplay(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
        TeamIcon(teamName = name, size = 64.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun ScoreDisplay(homeScore: Int, awayScore: Int) {
    Text(
        text = "$homeScore - $awayScore",
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun StatsSection(homeStats: TeamStatistics, awayStats: TeamStatistics) {
    val stats = listOf(
        Triple("Shots on Goal", homeStats.shotsOnGoal, awayStats.shotsOnGoal),
        Triple("Shots off Goal", homeStats.shotsOffGoal, awayStats.shotsOffGoal),
        Triple("Total Shots", homeStats.totalShots, awayStats.totalShots),
        Triple("Blocked Shots", homeStats.blockedShots, awayStats.blockedShots),
        Triple("Corner Kicks", homeStats.cornerKicks, awayStats.cornerKicks),
        Triple("Fouls", homeStats.fouls, awayStats.fouls),
        Triple("Yellow Cards", homeStats.yellowCards, awayStats.yellowCards),
        Triple("Red Cards", homeStats.redCards, awayStats.redCards),
        Triple("Offsides", homeStats.offsides, awayStats.offsides)
    )

    stats.forEach { (label, home, away) ->
        StatRow(label, home, away)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
    }
    
    // Percentage stats
    StatRow("Ball Possession", homeStats.ballPossession, awayStats.ballPossession)
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
    StatRow("Passes %", homeStats.passesPercentage, awayStats.passesPercentage)
}

@Composable
private fun StatRow(label: String, homeValue: Any, awayValue: Any) {
    val homeNum = homeValue.toString().replace("%", "").toFloatOrNull() ?: 0f
    val awayNum = awayValue.toString().replace("%", "").toFloatOrNull() ?: 0f
    val total = homeNum + awayNum
    
    val homeProgress = if (total > 0) homeNum / total else 0.5f
    val awayProgress = if (total > 0) awayNum / total else 0.5f

    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val secondaryColor = MaterialTheme.colorScheme.secondary.toArgb()
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.toArgb()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = homeValue.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = awayValue.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Home Progress (Left, reversed)
            AndroidView(
                modifier = Modifier.weight(1f).height(10.dp),
                factory = { context ->
                    RoundCornerProgressBar(context, null).apply {
                        max = 1f
                        progressColor = primaryColor
                        progressBackgroundColor = trackColor
                        isReverse = true
                    }
                },
                update = { it.progress = homeProgress }
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            // Away Progress (Right)
            AndroidView(
                modifier = Modifier.weight(1f).height(10.dp),
                factory = { context ->
                    RoundCornerProgressBar(context, null).apply {
                        max = 1f
                        progressColor = secondaryColor
                        progressBackgroundColor = trackColor
                    }
                },
                update = { it.progress = awayProgress }
            )
        }
    }
}
