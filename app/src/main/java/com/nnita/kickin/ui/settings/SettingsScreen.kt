package com.nnita.kickin.ui.settings

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnita.kickin.R
import com.nnita.kickin.ui.theme.KickinTheme

private val H_PAD = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    selectedLeagueId: String,
    liveInterval: String,
    timeFormat: String,
    dataSource: String,
    onLeagueSelected: (String) -> Unit,
    onLiveIntervalChanged: (String) -> Unit,
    onTimeFormatChanged: (String) -> Unit,
    onDataSourceChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_settings),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = H_PAD)
        ) {
            // ── Favorite League ───────────────────────────────────────────────
            SettingsSectionHeader(stringResource(R.string.settings_league_label))
            Spacer(modifier = Modifier.height(8.dp))
            LeaguePicker(
                selectedLeagueId = selectedLeagueId,
                onLeagueSelected = onLeagueSelected
            )

            SettingsDivider()

            // ── Live Update Interval ──────────────────────────────────────────
            SettingsSectionHeader(stringResource(R.string.settings_live_update_label))
            Spacer(modifier = Modifier.height(4.dp))
            LiveIntervalSelector(
                liveInterval = liveInterval,
                onLiveIntervalChanged = onLiveIntervalChanged
            )

            SettingsDivider()

            // ── Time Format ───────────────────────────────────────────────────
            val timeLabel = if (timeFormat == "12h")
                stringResource(R.string.settings_time_12h)
            else
                stringResource(R.string.settings_time_24h)
            SettingsSwitchRow(
                label = stringResource(R.string.settings_time_format_label),
                valueLabel = timeLabel,
                checked = timeFormat == "12h",
                onCheckedChange = { checked ->
                    onTimeFormatChanged(if (checked) "12h" else "24h")
                }
            )

            SettingsDivider()

            // ── Data Source ───────────────────────────────────────────────────
            val sourceLabel = if (dataSource == "api")
                stringResource(R.string.settings_data_api)
            else
                stringResource(R.string.settings_data_file)
            SettingsSwitchRow(
                label = stringResource(R.string.settings_data_source_label),
                valueLabel = sourceLabel,
                checked = dataSource == "api",
                onCheckedChange = { checked ->
                    onDataSourceChanged(if (checked) "api" else "file")
                },
                developerTag = true
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        letterSpacing = 0.8.sp,
        modifier = modifier.padding(top = 20.dp, bottom = 2.dp)
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 4.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaguePicker(
    selectedLeagueId: String,
    onLeagueSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val leagues = listOf(
        "none" to stringResource(R.string.settings_league_none),
        "39" to stringResource(R.string.league_premier_league),
        "140" to stringResource(R.string.league_la_liga),
        "135" to stringResource(R.string.league_serie_a),
        "78" to stringResource(R.string.league_bundesliga),
        "61" to stringResource(R.string.league_ligue_1)
    )
    val selectedName = leagues.find { it.first == selectedLeagueId }?.second
        ?: stringResource(R.string.settings_league_none)

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            leagues.forEach { (id, name) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (id == selectedLeagueId) FontWeight.Bold
                                         else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onLeagueSelected(id)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
private fun LiveIntervalSelector(
    liveInterval: String,
    onLiveIntervalChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        "manual" to stringResource(R.string.settings_live_interval_manual),
        "15"     to stringResource(R.string.settings_live_interval_15s),
        "30"     to stringResource(R.string.settings_live_interval_30s),
        "60"     to stringResource(R.string.settings_live_interval_1m),
        "300"    to stringResource(R.string.settings_live_interval_5m),
        "600"    to stringResource(R.string.settings_live_interval_10m)
    )
    Column(modifier = modifier) {
        options.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLiveIntervalChanged(value) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = liveInterval == value,
                    onClick = { onLiveIntervalChanged(value) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (liveInterval == value) FontWeight.SemiBold
                                 else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    valueLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    developerTag: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                if (developerTag) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = stringResource(R.string.settings_developer),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    KickinTheme {
        SettingsScreen(
            selectedLeagueId = "39",
            liveInterval = "manual",
            timeFormat = "24h",
            dataSource = "file",
            onLeagueSelected = {},
            onLiveIntervalChanged = {},
            onTimeFormatChanged = {},
            onDataSourceChanged = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewSettingsScreenDark() {
    KickinTheme {
        SettingsScreen(
            selectedLeagueId = "140",
            liveInterval = "60",
            timeFormat = "12h",
            dataSource = "api",
            onLeagueSelected = {},
            onLiveIntervalChanged = {},
            onTimeFormatChanged = {},
            onDataSourceChanged = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLeaguePicker() {
    KickinTheme {
        Column(modifier = Modifier.padding(H_PAD)) {
            LeaguePicker(
                selectedLeagueId = "39",
                onLeagueSelected = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLiveIntervalSelector() {
    KickinTheme {
        LiveIntervalSelector(
            liveInterval = "manual",
            onLiveIntervalChanged = {},
            modifier = Modifier.padding(H_PAD)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewLiveIntervalSelectorDark() {
    KickinTheme {
        LiveIntervalSelector(
            liveInterval = "60",
            onLiveIntervalChanged = {},
            modifier = Modifier.padding(H_PAD)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsSwitchRowTime() {
    KickinTheme {
        SettingsSwitchRow(
            label = "Time Format",
            valueLabel = "24-hour",
            checked = false,
            onCheckedChange = {},
            modifier = Modifier.padding(H_PAD)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewSettingsSwitchRowDeveloper() {
    KickinTheme {
        SettingsSwitchRow(
            label = "Data Source",
            valueLabel = "Live API",
            checked = true,
            onCheckedChange = {},
            developerTag = true,
            modifier = Modifier.padding(H_PAD)
        )
    }
}
