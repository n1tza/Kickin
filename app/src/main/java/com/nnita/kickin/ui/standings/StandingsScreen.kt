package com.nnita.kickin.ui.standings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nnita.kickin.R
import com.nnita.kickin.model.Standing
import com.nnita.kickin.ui.components.TeamIcon
import com.nnita.kickin.ui.theme.KickinTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandingsScreen(
    viewModel: StandingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val selectedLeague by viewModel.selectedLeague.collectAsState()
    val standings by viewModel.standings.collectAsState()
    val leagues = viewModel.leagues

    // State to track how many rows should be visible for the staggered animation
    var visibleRowsCount by remember(standings) { mutableIntStateOf(0) }

    LaunchedEffect(standings) {
        visibleRowsCount = 0
        standings.forEachIndexed { index, _ ->
            delay(50) // Delay between each row appearing
            visibleRowsCount = index + 1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_standings),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LeagueFilterRow(
                leagues = leagues,
                selectedLeague = selectedLeague,
                onLeagueSelected = viewModel::selectLeague
            )

            StandingsHeader()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(standings, key = { _, it -> it.teamName }) { index, standing ->
                    AnimatedVisibility(
                        visible = index < visibleRowsCount,
                        enter = slideInHorizontally(
                            initialOffsetX = { -100 },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeIn(animationSpec = tween(durationMillis = 300))
                    ) {
                        Column {
                            StandingRow(standing = standing)
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeagueFilterRow(
    leagues: List<String>,
    selectedLeague: String,
    onLeagueSelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(leagues, key = { _, it -> it }) { _, league ->
            val logoRes = when (league) {
                "Premier League" -> R.drawable.premierleaguelogo
                "Serie A" -> R.drawable.seriealogo
                "La Liga" -> R.drawable.laligalogo
                "Ligue 1" -> R.drawable.ligue1logo
                "Bundesliga" -> R.drawable.bundesligalogo
                else -> null
            }

            FilterChip(
                selected = league == selectedLeague,
                onClick = { onLeagueSelected(league) },
                label = {
                    Text(
                        text = league,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingIcon = logoRes?.let {
                    {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = league == selectedLeague,
                    borderColor = MaterialTheme.colorScheme.outline,
                    selectedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun StandingsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#",
            modifier = Modifier.width(28.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.standing_team),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.width(130.dp),
            horizontalArrangement = Arrangement.End
        ) {
            StandingHeaderItem(stringResource(R.string.standing_played))
            StandingHeaderItem(stringResource(R.string.standing_gd))
            StandingHeaderItem(stringResource(R.string.standing_pts))
        }
    }
}

@Composable
private fun StandingHeaderItem(text: String) {
    Text(
        text = text,
        modifier = Modifier.width(42.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun StandingRow(standing: Standing) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = standing.rank.toString(),
            modifier = Modifier.width(28.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        TeamIcon(teamName = standing.teamName, size = 26.dp)
        Text(
            text = standing.teamName,
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.width(130.dp),
            horizontalArrangement = Arrangement.End
        ) {
            StandingValueItem(standing.played.toString())
            StandingValueItem(standing.goalsDiff.toString())
            StandingValueItem(standing.points.toString(), fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun StandingValueItem(text: String, fontWeight: FontWeight = FontWeight.Normal) {
    Text(
        text = text,
        modifier = Modifier.width(42.dp),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = fontWeight,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewStandingsScreen() {
    KickinTheme { StandingsScreen() }
}
