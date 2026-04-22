package com.nnita.kickin.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nnita.kickin.R
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.ui.preview.previewFixtureFT
import com.nnita.kickin.ui.preview.previewFixtureLive
import com.nnita.kickin.ui.preview.previewFixtureNS
import com.nnita.kickin.ui.preview.previewFixtures
import com.nnita.kickin.ui.theme.KickinTheme
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    fixtures: List<Fixture>,
    onFixtureClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.home_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (fixtures.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.home_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }
                items(fixtures, key = { it.id }) { fixture ->
                    FixtureRow(fixture = fixture, onClick = { onFixtureClick(fixture.id) })
                }
                item { Spacer(modifier = Modifier.height(4.dp)) }
            }
        }
    }
}

@Composable
fun FixtureRow(
    fixture: Fixture,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = fixture.leagueName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fixture.homeTeam,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = fixture.homeScore?.toString() ?: stringResource(R.string.score_placeholder),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        Text(
                            text = stringResource(R.string.score_separator),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = fixture.awayScore?.toString() ?: stringResource(R.string.score_placeholder),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                    val statusText = when (fixture.status) {
                        "NS" -> formatKickoffTime(fixture.date)
                        "1H", "2H", "ET" -> stringResource(R.string.status_live_minute, fixture.elapsed ?: 0)
                        "HT" -> stringResource(R.string.status_ht)
                        "FT" -> stringResource(R.string.status_ft)
                        "AET" -> stringResource(R.string.status_aet)
                        "PEN" -> stringResource(R.string.status_pen)
                        else -> fixture.status
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor(fixture.status),
                        fontWeight = if (isLive(fixture.status)) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Text(
                    text = fixture.awayTeam,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = fixture.venueName,
                style = MaterialTheme.typography.labelSmall,
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

private fun isLive(status: String) = status in setOf("1H", "2H", "HT", "ET", "BT", "P", "SUSP", "INT")

@Composable
private fun statusColor(status: String): Color {
    return if (isLive(status)) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    KickinTheme {
        HomeScreen(fixtures = previewFixtures, onFixtureClick = {})
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreenDark() {
    KickinTheme {
        HomeScreen(fixtures = previewFixtures, onFixtureClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFixtureRowFT() {
    KickinTheme {
        FixtureRow(fixture = previewFixtureFT, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFixtureRowLive() {
    KickinTheme {
        FixtureRow(fixture = previewFixtureLive, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFixtureRowNS() {
    KickinTheme {
        FixtureRow(fixture = previewFixtureNS, onClick = {})
    }
}
