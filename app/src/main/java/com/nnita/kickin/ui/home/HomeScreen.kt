package com.nnita.kickin.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnita.kickin.R
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.ui.preview.previewFixtureFT
import com.nnita.kickin.ui.preview.previewFixtureLive
import com.nnita.kickin.ui.preview.previewFixtureNS
import com.nnita.kickin.ui.preview.previewFixtures
import com.nnita.kickin.ui.theme.KickinTheme
import com.nnita.kickin.ui.theme.LiveRed
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
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
    val live = isLive(fixture.status)
    val stripeColor = when {
        live -> MaterialTheme.colorScheme.primary
        fixture.status in listOf("FT", "AET", "PEN") -> MaterialTheme.colorScheme.outlineVariant
        else -> Color.Transparent
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(stripeColor)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (live) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(LiveRed, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    Text(
                        text = fixture.leagueName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (live) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(fixture = fixture)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fixture.homeTeam,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    if (fixture.status == "NS") {
                        Text(
                            text = stringResource(R.string.score_vs),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = fixture.homeScore?.toString()
                                    ?: stringResource(R.string.score_placeholder),
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = stringResource(R.string.score_separator),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            Text(
                                text = fixture.awayScore?.toString()
                                    ?: stringResource(R.string.score_placeholder),
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = fixture.awayTeam,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = fixture.venueName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(fixture: Fixture) {
    val live = isLive(fixture.status)
    val badgeText = when (fixture.status) {
        "NS" -> formatKickoffTime(fixture.date)
        "1H", "2H", "ET" -> stringResource(R.string.status_live_minute, fixture.elapsed ?: 0)
        "HT" -> stringResource(R.string.status_ht)
        "FT" -> stringResource(R.string.status_ft)
        "AET" -> stringResource(R.string.status_aet)
        "PEN" -> stringResource(R.string.status_pen)
        else -> fixture.status
    }
    val containerColor = when {
        live -> MaterialTheme.colorScheme.primary
        fixture.status == "HT" -> Color(0xFFFFB300)
        fixture.status in listOf("FT", "AET", "PEN") -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color.Transparent
    }
    val contentColor = when {
        live -> MaterialTheme.colorScheme.onPrimary
        fixture.status == "HT" -> Color(0xFF1A1000)
        fixture.status in listOf("FT", "AET", "PEN") -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = if (fixture.status == "NS")
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        else null
    ) {
        Text(
            text = badgeText,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
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

private fun isLive(status: String) =
    status in setOf("1H", "2H", "HT", "ET", "BT", "P", "SUSP", "INT")

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

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewFixtureRowFTDark() {
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

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewFixtureRowLiveDark() {
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
