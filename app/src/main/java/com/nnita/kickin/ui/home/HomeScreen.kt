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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnita.kickin.R
import com.nnita.kickin.model.Fixture
import com.nnita.kickin.model.LeagueSummary
import com.nnita.kickin.ui.components.PicassoImage
import com.nnita.kickin.ui.components.TeamIcon
import com.nnita.kickin.ui.preview.previewFixtureFT
import com.nnita.kickin.ui.preview.previewFixtureHT
import com.nnita.kickin.ui.preview.previewFixtureLive
import com.nnita.kickin.ui.preview.previewFixtureNS
import com.nnita.kickin.ui.preview.previewLeagueSummaries
import com.nnita.kickin.ui.preview.previewLeagues
import com.nnita.kickin.ui.preview.previewLiveFixtures
import com.nnita.kickin.ui.preview.previewOtherFixtures
import com.nnita.kickin.ui.theme.CharcoalBlack
import com.nnita.kickin.ui.theme.KickinTheme
import com.nnita.kickin.ui.theme.LiveRed
import com.nnita.kickin.ui.theme.NeonGreen
import com.nnita.kickin.ui.theme.White
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    groupedOtherFixtures: List<Pair<String, List<Fixture>>> = emptyList(),
    otherLeagues: List<LeagueSummary> = emptyList(),
    selectedCountries: Set<String> = emptySet(),
    selectedLeagueFilterIds: Set<Int> = emptySet(),
    sortOrder: SortOrder = SortOrder.TIME_ASC,
    availableCountries: List<String> = emptyList(),
    availableFilterLeagues: List<LeagueSummary> = emptyList(),
    onCountriesChanged: (Set<String>) -> Unit = {},
    onLeagueFilterIdsChanged: (Set<Int>) -> Unit = {},
    onSortOrderChanged: (SortOrder) -> Unit = {},
    timeFormat: String = "24h",
    isLoading: Boolean = false,
    apiError: String? = null,
    liveInterval: String = "manual",
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(liveInterval, lifecycleOwner) {
        val delayMs = liveInterval.toLongOrNull()?.times(1_000L) ?: return@LaunchedEffect
        lifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
            while (true) {
                delay(delayMs)
                onRefresh()
            }
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showMoreSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

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

    if (showFilterSheet) {
        FixtureFilterDialog(
            selectedLeague = selectedLeague,
            selectedCountries = selectedCountries,
            selectedLeagueIds = selectedLeagueFilterIds,
            sortOrder = sortOrder,
            availableCountries = availableCountries,
            availableLeagues = availableFilterLeagues,
            onCountriesChanged = onCountriesChanged,
            onLeagueIdsChanged = onLeagueFilterIdsChanged,
            onSortOrderChanged = onSortOrderChanged,
            onDismiss = { showFilterSheet = false }
        )
    }

    if (showMoreSheet) {
        MoreLeaguesBottomSheet(
            leagues = otherLeagues,
            sheetState = sheetState,
            onLeagueSelected = { league ->
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showMoreSheet = false
                    onLeagueSelected(league)
                }
            },
            onDismiss = { showMoreSheet = false }
        )
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
        Box(modifier = Modifier.fillMaxSize()) {
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
                    onLeagueSelected = onLeagueSelected,
                    onMoreClick = { showMoreSheet = true }
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
                item { Spacer(modifier = Modifier.height(4.dp)) }
            }

            val useGrouped = selectedLeague == "All" && groupedOtherFixtures.isNotEmpty()
            val activeFilterCount = selectedCountries.size + selectedLeagueFilterIds.size

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = H_PAD)
                        .padding(top = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.section_matches).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (matchMode == MatchMode.TODAY) {
                            Text(
                                text = stringResource(R.string.section_matches_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    FilterSortButton(
                        activeFilterCount = activeFilterCount,
                        isSortNonDefault = sortOrder != SortOrder.TIME_ASC,
                        onClick = { showFilterSheet = true }
                    )
                }
            }

            if (useGrouped) {
                groupedOtherFixtures.forEach { (leagueName, fixtures) ->
                    val logo = fixtures.firstOrNull()?.leagueLogo ?: ""
                    val leagueId = fixtures.firstOrNull()?.leagueId ?: leagueName.hashCode()
                    item(key = "header_$leagueId") {
                        LeagueSectionHeader(
                            leagueName = leagueName,
                            leagueLogo = logo,
                            modifier = Modifier.padding(horizontal = H_PAD).padding(top = 16.dp, bottom = 4.dp)
                        )
                    }
                    items(fixtures, key = { it.id }) { fixture ->
                        MatchListCard(
                            fixture = fixture,
                            onClick = { onFixtureClick(fixture.id) },
                            use12h = timeFormat == "12h",
                            modifier = Modifier.padding(horizontal = H_PAD, vertical = 4.dp)
                        )
                    }
                }
            } else if (otherFixtures.isEmpty()) {
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

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (apiError != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = "API Error: $apiError",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        } // Box
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
    onLeagueSelected: (String) -> Unit,
    onMoreClick: () -> Unit
) {
    val isOtherSelected = selectedLeague !in leagues && selectedLeague != "All"

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

        // "More..." chip — selected when an other-league is active
        item(key = "more") {
            FilterChip(
                selected = isOtherSelected,
                onClick = onMoreClick,
                label = {
                    Text(
                        text = if (isOtherSelected) selectedLeague.uppercase()
                               else stringResource(R.string.filter_more).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isOtherSelected,
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
                    teamLogo = fixture.homeTeamLogo,
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
                    teamLogo = fixture.awayTeamLogo,
                    iconSize = iconSize,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TeamColumn(teamName: String, teamLogo: String, iconSize: Dp, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        PicassoImage(url = teamLogo, teamName = teamName, size = iconSize)
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
                PicassoImage(url = fixture.homeTeamLogo, teamName = fixture.homeTeam, size = 26.dp)
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
                PicassoImage(url = fixture.awayTeamLogo, teamName = fixture.awayTeam, size = 26.dp)
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

@Composable
private fun FilterSortButton(
    activeFilterCount: Int,
    isSortNonDefault: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = activeFilterCount > 0 || isSortNonDefault
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = stringResource(R.string.filter_matches_desc),
            modifier = Modifier.size(16.dp),
            tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.filter_and_sort),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (activeFilterCount > 0) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = stringResource(R.string.filter_active_count, activeFilterCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun FixtureFilterDialog(
    selectedLeague: String,
    selectedCountries: Set<String>,
    selectedLeagueIds: Set<Int>,
    sortOrder: SortOrder,
    availableCountries: List<String>,
    availableLeagues: List<LeagueSummary>,
    onCountriesChanged: (Set<String>) -> Unit,
    onLeagueIdsChanged: (Set<Int>) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit,
    onDismiss: () -> Unit
) {
    val chipLeagueActive = selectedLeague != "All"
    var countriesExpanded by remember { mutableStateOf(selectedCountries.isNotEmpty()) }
    var leaguesExpanded by remember { mutableStateOf(selectedLeagueIds.isNotEmpty()) }
    var countryQuery by remember { mutableStateOf("") }
    var leagueQuery by remember { mutableStateOf("") }

    val filteredCountries = remember(availableCountries, countryQuery) {
        if (countryQuery.isBlank()) availableCountries
        else availableCountries.filter { it.contains(countryQuery, ignoreCase = true) }
    }
    val filteredLeagues = remember(availableLeagues, leagueQuery) {
        if (leagueQuery.isBlank()) availableLeagues
        else availableLeagues.filter {
            it.name.contains(leagueQuery, ignoreCase = true) ||
            it.country.contains(leagueQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filter_dialog_title).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )
                if (!chipLeagueActive && (selectedCountries.isNotEmpty() || selectedLeagueIds.isNotEmpty())) {
                    TextButton(
                        onClick = {
                            onCountriesChanged(emptySet())
                            onLeagueIdsChanged(emptySet())
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.filter_clear_all),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Sort — always visible, no toggle
                FilterDialogSectionHeader(title = stringResource(R.string.filter_sort_by))
                listOf(
                    SortOrder.TIME_ASC  to R.string.filter_sort_time_asc,
                    SortOrder.TIME_DESC to R.string.filter_sort_time_desc,
                    SortOrder.ALPHA     to R.string.filter_sort_alpha
                ).forEach { (order, labelRes) ->
                    FilterRadioRow(
                        label = stringResource(labelRes),
                        selected = sortOrder == order,
                        onClick = { onSortOrderChanged(order) }
                    )
                }

                if (chipLeagueActive) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.filter_league_override_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Country section — expandable
                    FilterExpandableHeader(
                        title = stringResource(R.string.filter_by_country),
                        selectedCount = selectedCountries.size,
                        expanded = countriesExpanded,
                        onToggle = { countriesExpanded = !countriesExpanded },
                        onClear = { onCountriesChanged(emptySet()) }
                    )
                    if (countriesExpanded) {
                        FilterSearchField(
                            query = countryQuery,
                            onQueryChange = { countryQuery = it },
                            placeholder = stringResource(R.string.search_countries_hint)
                        )
                        if (filteredCountries.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_results_found),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        filteredCountries.forEach { country ->
                            FilterCheckboxRow(
                                label = "${countryFlagEmoji(country)} $country",
                                checked = country in selectedCountries,
                                onToggle = {
                                    onCountriesChanged(
                                        if (country in selectedCountries) selectedCountries - country
                                        else selectedCountries + country
                                    )
                                }
                            )
                        }
                    }

                    // League section — expandable
                    FilterExpandableHeader(
                        title = stringResource(R.string.filter_by_league),
                        selectedCount = selectedLeagueIds.size,
                        expanded = leaguesExpanded,
                        onToggle = { leaguesExpanded = !leaguesExpanded },
                        onClear = { onLeagueIdsChanged(emptySet()) }
                    )
                    if (leaguesExpanded) {
                        FilterSearchField(
                            query = leagueQuery,
                            onQueryChange = { leagueQuery = it },
                            placeholder = stringResource(R.string.search_filter_leagues_hint)
                        )
                        if (filteredLeagues.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_results_found),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        filteredLeagues.forEach { league ->
                            FilterCheckboxRow(
                                label = league.name,
                                checked = league.id in selectedLeagueIds,
                                onToggle = {
                                    onLeagueIdsChanged(
                                        if (league.id in selectedLeagueIds) selectedLeagueIds - league.id
                                        else selectedLeagueIds + league.id
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.filter_done))
            }
        }
    )
}

@Composable
private fun FilterExpandableHeader(
    title: String,
    selectedCount: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(top = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        if (selectedCount > 0) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "$selectedCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            TextButton(
                onClick = onClear,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.filter_clear),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Icon(
            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun FilterSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
private fun FilterDialogSectionHeader(
    title: String,
    showClear: Boolean = false,
    onClear: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (showClear) {
            TextButton(
                onClick = onClear,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.filter_clear),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun FilterRadioRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FilterCheckboxRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Checkbox(checked = checked, onCheckedChange = { onToggle() })
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LeagueSectionHeader(
    leagueName: String,
    leagueLogo: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PicassoImage(url = leagueLogo, teamName = leagueName, size = 20.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = leagueName.uppercase(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreLeaguesBottomSheet(
    leagues: List<LeagueSummary>,
    sheetState: androidx.compose.material3.SheetState,
    onLeagueSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(leagues, query) {
        if (query.isBlank()) leagues
        else leagues.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.country.contains(query, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.more_leagues_title).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = H_PAD, vertical = 8.dp)
            )
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(stringResource(R.string.search_leagues_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = H_PAD, vertical = 8.dp)
            )
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_leagues_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(filtered, key = { it.id }) { league ->
                        LeagueSheetRow(
                            league = league,
                            onClick = { onLeagueSelected(league.name) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = H_PAD),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeagueSheetRow(league: LeagueSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = H_PAD, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PicassoImage(url = league.logo, teamName = league.name, size = 32.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = league.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${countryFlagEmoji(league.country)} ${league.country}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun countryFlagEmoji(country: String): String {
    val codes = mapOf(
        "England" to "GB", "Scotland" to "GB", "Wales" to "GB", "Northern Ireland" to "GB",
        "Spain" to "ES", "France" to "FR", "Germany" to "DE", "Italy" to "IT",
        "Portugal" to "PT", "Netherlands" to "NL", "Belgium" to "BE", "Turkey" to "TR",
        "Greece" to "GR", "Sweden" to "SE", "Norway" to "NO", "Denmark" to "DK",
        "Switzerland" to "CH", "Austria" to "AT", "Russia" to "RU", "Ukraine" to "UA",
        "Poland" to "PL", "Czech Republic" to "CZ", "Romania" to "RO", "Croatia" to "HR",
        "Serbia" to "RS", "Hungary" to "HU", "Slovakia" to "SK", "Slovenia" to "SI",
        "Ireland" to "IE", "Finland" to "FI", "Israel" to "IL", "Cyprus" to "CY",
        "Albania" to "AL", "Bulgaria" to "BG", "Georgia" to "GE", "Azerbaijan" to "AZ",
        "Kazakhstan" to "KZ", "Belarus" to "BY", "Moldova" to "MD", "Armenia" to "AM",
        "Brazil" to "BR", "Argentina" to "AR", "Mexico" to "MX", "Colombia" to "CO",
        "Chile" to "CL", "Uruguay" to "UY", "Peru" to "PE", "Ecuador" to "EC",
        "Bolivia" to "BO", "Paraguay" to "PY", "Venezuela" to "VE", "Costa Rica" to "CR",
        "Honduras" to "HN", "Guatemala" to "GT", "Panama" to "PA", "USA" to "US",
        "Canada" to "CA", "Japan" to "JP", "South Korea" to "KR", "China" to "CN",
        "Australia" to "AU", "Saudi Arabia" to "SA", "UAE" to "AE", "Qatar" to "QA",
        "Egypt" to "EG", "Nigeria" to "NG", "Morocco" to "MA", "South Africa" to "ZA",
        "Tunisia" to "TN", "Algeria" to "DZ", "Ghana" to "GH", "Senegal" to "SN",
        "Thailand" to "TH", "Indonesia" to "ID", "Vietnam" to "VN", "India" to "IN"
    )
    val iso = codes[country] ?: return ""
    return iso.map { char -> String(Character.toChars(0x1F1E6 + (char - 'A'))) }.joinToString("")
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun PreviewFilterSortButtonInactive() {
    KickinTheme {
        FilterSortButton(activeFilterCount = 0, isSortNonDefault = false, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterSortButtonActive() {
    KickinTheme {
        FilterSortButton(activeFilterCount = 3, isSortNonDefault = true, onClick = {})
    }
}

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

@Preview(showBackground = true)
@Composable
fun PreviewLeagueFilterRow() {
    KickinTheme {
        LeagueFilterRow(
            leagues = previewLeagues,
            selectedLeague = "Premier League",
            onLeagueSelected = {},
            onMoreClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSectionHeader() {
    KickinTheme {
        SectionHeader(
            title = "LIVE MATCHES",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTeamColumn() {
    KickinTheme {
        TeamColumn(
            teamName = "Arsenal",
            teamLogo = "",
            iconSize = 44.dp,
            modifier = Modifier.width(100.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScoreDisplayLive() {
    KickinTheme {
        ScoreDisplay(fixture = previewFixtureLive)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScoreDisplayNS() {
    KickinTheme {
        ScoreDisplay(fixture = previewFixtureNS)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLiveTimeBadge() {
    KickinTheme {
        LiveTimeBadge(fixture = previewFixtureLive)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMatchCenterInfoFT() {
    KickinTheme {
        MatchCenterInfo(fixture = previewFixtureFT)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMatchCenterInfoNS() {
    KickinTheme {
        MatchCenterInfo(fixture = previewFixtureNS)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFixtureFilterDialog() {
    KickinTheme {
        FixtureFilterDialog(
            selectedLeague = "All",
            selectedCountries = emptySet(),
            selectedLeagueIds = emptySet(),
            sortOrder = SortOrder.TIME_ASC,
            availableCountries = listOf("England", "Spain", "Italy", "France", "Germany"),
            availableLeagues = previewLeagueSummaries,
            onCountriesChanged = {},
            onLeagueIdsChanged = {},
            onSortOrderChanged = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterExpandableHeaderCollapsed() {
    KickinTheme {
        FilterExpandableHeader(
            title = "Filter by Country",
            selectedCount = 0,
            expanded = false,
            onToggle = {},
            onClear = {},
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterExpandableHeaderExpanded() {
    KickinTheme {
        FilterExpandableHeader(
            title = "Filter by League",
            selectedCount = 2,
            expanded = true,
            onToggle = {},
            onClear = {},
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterSearchField() {
    KickinTheme {
        FilterSearchField(
            query = "",
            onQueryChange = {},
            placeholder = "Search countries",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterDialogSectionHeader() {
    KickinTheme {
        FilterDialogSectionHeader(
            title = "Sort By",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterRadioRow() {
    KickinTheme {
        FilterRadioRow(
            label = "Time (Earliest First)",
            selected = true,
            onClick = {},
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterCheckboxRow() {
    KickinTheme {
        FilterCheckboxRow(
            label = "🏴󠁧󠁢󠁥󠁮󠁧󠁿 England",
            checked = true,
            onToggle = {},
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueSectionHeader() {
    KickinTheme {
        LeagueSectionHeader(
            leagueName = "Premier League",
            leagueLogo = "",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLeagueSheetRow() {
    KickinTheme {
        LeagueSheetRow(
            league = LeagueSummary(id = 94, name = "Primeira Liga", logo = "", country = "Portugal"),
            onClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewMoreLeaguesBottomSheet() {
    KickinTheme {
        MoreLeaguesBottomSheet(
            leagues = previewLeagueSummaries,
            sheetState = rememberModalBottomSheetState(),
            onLeagueSelected = {},
            onDismiss = {}
        )
    }
}
