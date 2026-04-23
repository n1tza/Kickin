package com.nnita.kickin.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnita.kickin.R
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.ui.components.TeamIcon
import com.nnita.kickin.ui.preview.previewFixtureFT
import com.nnita.kickin.ui.preview.previewFixtureHT
import com.nnita.kickin.ui.preview.previewFixtureLive
import com.nnita.kickin.ui.preview.previewFixtureNS
import com.nnita.kickin.ui.preview.previewLeagues
import com.nnita.kickin.ui.preview.previewLiveFixtures
import com.nnita.kickin.ui.preview.previewOtherFixtures
import com.nnita.kickin.ui.theme.CharcoalBlack
import com.nnita.kickin.ui.theme.KickinTheme
import com.nnita.kickin.ui.theme.LiveRed
import com.nnita.kickin.ui.theme.NeonGreen
import com.nnita.kickin.ui.theme.White
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val H_PAD = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    leagues: List<String>,
    selectedLeague: String,
    matchMode: MatchMode,
    selectedDate: LocalDate,
    liveFixtures: List<Fixture>,
    otherFixtures: List<Fixture>,
    onLeagueSelected: (String) -> Unit,
    onModeChanged: (MatchMode) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onFixtureClick: (Int) -> Unit,
    timeFormat: String = "24h",
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val todayStart = LocalDate.now()
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant()
                        .toEpochMilli()
                    return utcTimeMillis < todayStart
                }
            }
        )
        val darkTheme = isSystemInDarkTheme()
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = if (darkTheme) MaterialTheme.colorScheme.primary else CharcoalBlack,
                onPrimary = if (darkTheme) MaterialTheme.colorScheme.onPrimary else White
            )
        ) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(
                                Instant.ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                            )
                        }
                        showDatePicker = false
                    }) {
                        Text(stringResource(R.string.date_picker_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(R.string.date_picker_cancel))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = (if (matchMode == MatchMode.TODAY)
                            stringResource(R.string.home_title)
                        else
                            stringResource(R.string.past_title)).uppercase(),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                MatchModeToggle(
                    mode = matchMode,
                    onModeChanged = onModeChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = H_PAD, vertical = 8.dp)
                )
            }

            item {
                LeagueFilterRow(
                    leagues = leagues,
                    selectedLeague = selectedLeague,
                    onLeagueSelected = onLeagueSelected
                )
            }

            if (matchMode == MatchMode.PAST) {
                item {
                    DateSelectorRow(
                        selectedDate = selectedDate,
                        onDateSelected = onDateSelected,
                        onPickDateClick = { showDatePicker = true }
                    )
                }
            }

            if (matchMode == MatchMode.TODAY && liveFixtures.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.section_live).uppercase(),
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
                    title = stringResource(R.string.section_matches).uppercase(),
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
                            text = stringResource(
                                if (matchMode == MatchMode.PAST) R.string.home_empty_past
                                else R.string.home_empty
                            ),
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
                        use12h = timeFormat == "12h",
                        modifier = Modifier.padding(horizontal = H_PAD, vertical = 4.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun MatchModeToggle(
    mode: MatchMode,
    onModeChanged: (MatchMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            listOf(MatchMode.TODAY, MatchMode.PAST).forEach { m ->
                val selected = m == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(50.dp)
                        )
                        .clickable { onModeChanged(m) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(
                            if (m == MatchMode.TODAY) R.string.mode_today else R.string.mode_past
                        ).uppercase(),
                        color = if (selected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun DateSelectorRow(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onPickDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canGoNext = selectedDate.isBefore(LocalDate.now().minusDays(1))
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = H_PAD, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { onDateSelected(selectedDate.minusDays(1)) }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.content_desc_prev_day)
            )
        }
        TextButton(
            onClick = onPickDateClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = stringResource(R.string.content_desc_calendar),
                modifier = Modifier.size(16.dp)
            )
        }
        IconButton(
            onClick = { onDateSelected(selectedDate.plusDays(1)) },
            enabled = canGoNext
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = stringResource(R.string.content_desc_next_day)
            )
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
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
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
            .width(280.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = NeonGreen,
            contentColor = CharcoalBlack
        ),
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
                    color = CharcoalBlack.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                LiveTimeBadge(fixture = fixture)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                val iconSize = 44.dp
                
                TeamColumn(
                    teamName = fixture.homeTeam,
                    iconSize = iconSize,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .height(iconSize)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ScoreDisplay(fixture = fixture)
                }

                TeamColumn(
                    teamName = fixture.awayTeam,
                    iconSize = iconSize,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TeamColumn(teamName: String, iconSize: Dp, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        TeamIcon(teamName = teamName, size = iconSize)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = teamName.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Visible
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
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold,
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
        color = CharcoalBlack
    ) {
        Text(
            text = text,
            color = NeonGreen,
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
    use12h: Boolean = false,
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
                    text = fixture.homeTeam.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TeamIcon(teamName = fixture.homeTeam, size = 26.dp)
            }

            MatchCenterInfo(
                fixture = fixture,
                use12h = use12h,
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
                    text = fixture.awayTeam.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MatchCenterInfo(fixture: Fixture, use12h: Boolean = false, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(72.dp)
    ) {
        if (fixture.status == "NS") {
            Text(
                text = formatKickoffTime(fixture.date, use12h).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            val scoreText = "${fixture.homeScore ?: stringResource(R.string.score_placeholder)}" +
                    " ${stringResource(R.string.score_separator)} " +
                    "${fixture.awayScore ?: stringResource(R.string.score_placeholder)}"
            Text(
                text = scoreText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            val statusLabel = when (fixture.status) {
                "FT" -> stringResource(R.string.status_ft)
                "AET" -> stringResource(R.string.status_aet)
                "PEN" -> stringResource(R.string.status_pen)
                else -> fixture.status
            }
            Text(
                text = statusLabel.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatKickoffTime(date: String, use12h: Boolean): String {
    return try {
        val odt = OffsetDateTime.parse(date)
        val local = odt.atZoneSameInstant(ZoneId.systemDefault())
        val pattern = if (use12h) "h:mm a" else "HH:mm"
        DateTimeFormatter.ofPattern(pattern).format(local)
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
            matchMode = MatchMode.TODAY,
            selectedDate = LocalDate.now().minusDays(1),
            liveFixtures = previewLiveFixtures,
            otherFixtures = previewOtherFixtures,
            onLeagueSelected = {},
            onModeChanged = {},
            onDateSelected = {},
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
            matchMode = MatchMode.TODAY,
            selectedDate = LocalDate.now().minusDays(1),
            liveFixtures = previewLiveFixtures,
            otherFixtures = previewOtherFixtures,
            onLeagueSelected = {},
            onModeChanged = {},
            onDateSelected = {},
            onFixtureClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreenPast() {
    KickinTheme {
        HomeScreen(
            leagues = previewLeagues,
            selectedLeague = "All",
            matchMode = MatchMode.PAST,
            selectedDate = LocalDate.now().minusDays(1),
            liveFixtures = emptyList(),
            otherFixtures = previewOtherFixtures.filter { it.status != "NS" },
            onLeagueSelected = {},
            onModeChanged = {},
            onDateSelected = {},
            onFixtureClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMatchModeToggleToday() {
    KickinTheme {
        MatchModeToggle(
            mode = MatchMode.TODAY,
            onModeChanged = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewMatchModeTogglePast() {
    KickinTheme {
        MatchModeToggle(
            mode = MatchMode.PAST,
            onModeChanged = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDateSelectorRow() {
    KickinTheme {
        DateSelectorRow(
            selectedDate = LocalDate.now().minusDays(1),
            onDateSelected = {},
            onPickDateClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewDateSelectorRowDark() {
    KickinTheme {
        DateSelectorRow(
            selectedDate = LocalDate.now().minusDays(3),
            onDateSelected = {},
            onPickDateClick = {}
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
