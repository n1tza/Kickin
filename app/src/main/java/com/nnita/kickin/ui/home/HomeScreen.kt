package com.nnita.kickin.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnita.kickin.R
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.model.isLive
import com.nnita.kickin.ui.components.TeamIcon
import com.nnita.kickin.ui.preview.previewFixtureFT
import com.nnita.kickin.ui.preview.previewFixtureHT
import com.nnita.kickin.ui.preview.previewFixtureLive
import com.nnita.kickin.ui.preview.previewFixtureNS
import com.nnita.kickin.ui.preview.previewLeagues
import com.nnita.kickin.ui.preview.previewLiveFixtures
import com.nnita.kickin.ui.preview.previewOtherFixtures
import com.nnita.kickin.ui.theme.KickinTheme
import com.nnita.kickin.ui.theme.LiveRed
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val H_PAD = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    leagues: List<String>,
    selectedLeague: String,
    liveFixtures: List<Fixture>,
    otherFixtures: List<Fixture>,
    onLeagueSelected: (String) -> Unit,
    onFixtureClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                LeagueFilterRow(
                    leagues = leagues,
                    selectedLeague = selectedLeague,
                    onLeagueSelected = onLeagueSelected
                )
            }

            if (liveFixtures.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.section_live),
                        modifier = Modifier.padding(horizontal = H_PAD, vertical = 12.dp)
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = H_PAD),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(liveFixtures, key = { it.id }) { fixture ->
                            LiveMatchCard(
                                fixture = fixture,
                                onClick = { onFixtureClick(fixture.id) }
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.section_matches),
                    modifier = Modifier.padding(horizontal = H_PAD, vertical = 12.dp)
                )
            }

            if (otherFixtures.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.home_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(otherFixtures, key = { it.id }) { fixture ->
                    MatchListCard(
                        fixture = fixture,
                        onClick = { onFixtureClick(fixture.id) },
                        modifier = Modifier.padding(horizontal = H_PAD, vertical = 4.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
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
        contentPadding = PaddingValues(horizontal = H_PAD, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(leagues, key = { it }) { league ->
            val label = if (league == "All") stringResource(R.string.filter_all) else league
            FilterChip(
                selected = league == selectedLeague,
                onClick = { onLeagueSelected(league) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
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
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
fun LiveMatchCard(
    fixture: Fixture,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(215.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(LiveRed, CircleShape)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = fixture.leagueName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                LiveTimeBadge(fixture = fixture)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TeamColumn(teamName = fixture.homeTeam, iconSize = 36.dp)
                ScoreDisplay(fixture = fixture)
                TeamColumn(teamName = fixture.awayTeam, iconSize = 36.dp)
            }
        }
    }
}

@Composable
private fun TeamColumn(teamName: String, iconSize: Dp) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TeamIcon(teamName = teamName, size = iconSize)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = teamName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(iconSize * 2f)
        )
    }
}

@Composable
private fun ScoreDisplay(fixture: Fixture) {
    val scoreText = if (fixture.homeScore != null && fixture.awayScore != null) {
        "${fixture.homeScore} ${stringResource(R.string.score_separator)} ${fixture.awayScore}"
    } else {
        stringResource(R.string.score_vs)
    }
    Text(
        text = scoreText,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun LiveTimeBadge(fixture: Fixture) {
    val text = when (fixture.status) {
        "1H", "2H", "ET" -> stringResource(R.string.status_live_minute, fixture.elapsed ?: 0)
        "HT" -> stringResource(R.string.status_ht)
        else -> fixture.status
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun MatchListCard(
    fixture: Fixture,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fixture.homeTeam,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TeamIcon(teamName = fixture.homeTeam, size = 26.dp)
            }

            MatchCenterInfo(
                fixture = fixture,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamIcon(teamName = fixture.awayTeam, size = 26.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = fixture.awayTeam,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MatchCenterInfo(fixture: Fixture, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(72.dp)
    ) {
        if (fixture.status == "NS") {
            Text(
                text = formatKickoffTime(fixture.date),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            val scoreText = "${fixture.homeScore ?: stringResource(R.string.score_placeholder)}" +
                    " ${stringResource(R.string.score_separator)} " +
                    "${fixture.awayScore ?: stringResource(R.string.score_placeholder)}"
            Text(
                text = scoreText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
            val statusLabel = when (fixture.status) {
                "FT" -> stringResource(R.string.status_ft)
                "AET" -> stringResource(R.string.status_aet)
                "PEN" -> stringResource(R.string.status_pen)
                else -> fixture.status
            }
            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatKickoffTime(date: String): String {
    return try {
        val odt = OffsetDateTime.parse(date)
        val local = odt.atZoneSameInstant(ZoneId.systemDefault())
        DateTimeFormatter.ofPattern("HH:mm").format(local)
    } catch (e: Exception) {
        date
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    KickinTheme {
        HomeScreen(
            leagues = previewLeagues,
            selectedLeague = "All",
            liveFixtures = previewLiveFixtures,
            otherFixtures = previewOtherFixtures,
            onLeagueSelected = {},
            onFixtureClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreenDark() {
    KickinTheme {
        HomeScreen(
            leagues = previewLeagues,
            selectedLeague = "All",
            liveFixtures = previewLiveFixtures,
            otherFixtures = previewOtherFixtures,
            onLeagueSelected = {},
            onFixtureClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLiveMatchCard() {
    KickinTheme {
        LiveMatchCard(fixture = previewFixtureLive, onClick = {})
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewLiveMatchCardDark() {
    KickinTheme {
        LiveMatchCard(fixture = previewFixtureHT, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMatchListCardFT() {
    KickinTheme {
        MatchListCard(
            fixture = previewFixtureFT,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMatchListCardNS() {
    KickinTheme {
        MatchListCard(
            fixture = previewFixtureNS,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewMatchListCardDark() {
    KickinTheme {
        MatchListCard(
            fixture = previewFixtureFT,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
