package com.nnita.kickin.ui.legal

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnita.kickin.R
import com.nnita.kickin.ui.theme.KickinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_legal).uppercase(),
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            LegalSectionLabel(stringResource(R.string.legal_section_data))

            LegalCard(
                name = stringResource(R.string.legal_api_name),
                version = stringResource(R.string.legal_api_version),
                author = stringResource(R.string.legal_api_author),
                license = stringResource(R.string.legal_api_license),
                description = stringResource(R.string.legal_api_desc),
                urlLabel = stringResource(R.string.legal_api_url_label),
                url = stringResource(R.string.legal_api_url)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LegalSectionLabel(stringResource(R.string.legal_section_libraries))

            LegalCard(
                name = stringResource(R.string.legal_picasso_name),
                version = stringResource(R.string.legal_picasso_version),
                author = stringResource(R.string.legal_picasso_author),
                license = stringResource(R.string.legal_picasso_license),
                description = stringResource(R.string.legal_picasso_desc),
                urlLabel = stringResource(R.string.legal_picasso_url_label),
                url = stringResource(R.string.legal_picasso_url)
            )

            LegalCard(
                name = stringResource(R.string.legal_rcpb_name),
                version = stringResource(R.string.legal_rcpb_version),
                author = stringResource(R.string.legal_rcpb_author),
                license = stringResource(R.string.legal_rcpb_license),
                description = stringResource(R.string.legal_rcpb_desc),
                urlLabel = stringResource(R.string.legal_rcpb_url_label),
                url = stringResource(R.string.legal_rcpb_url)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.info_copyright),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LegalSectionLabel(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

@Composable
private fun LegalCard(
    name: String,
    version: String,
    author: String,
    license: String,
    description: String,
    urlLabel: String,
    url: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = version,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = license,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { uriHandler.openUri(url) }) {
                    Text(
                        text = urlLabel,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun PreviewLegalScreen() {
    KickinTheme { LegalScreen() }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewLegalScreenDark() {
    KickinTheme { LegalScreen() }
}

@Preview(showBackground = true)
@Composable
fun PreviewLegalCard() {
    KickinTheme {
        LegalCard(
            name = "Picasso",
            version = "2.8",
            author = "Square, Inc.",
            license = "Apache License 2.0",
            description = "Image downloading and caching library for Android.",
            urlLabel = "View License",
            url = "https://github.com/square/picasso/blob/master/LICENSE",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewLegalCardDark() {
    KickinTheme {
        LegalCard(
            name = "API-Football",
            version = "v3",
            author = "API-Sports",
            license = "API-Sports License",
            description = "Provides live football scores, fixtures, standings, statistics, and lineups.",
            urlLabel = "View Terms",
            url = "https://www.api-football.com",
            modifier = Modifier.padding(16.dp)
        )
    }
}
