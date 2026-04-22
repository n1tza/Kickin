# CLAUDE.md — CSC 214 Project 3: Football Score Tracker

## Project Overview
**App name: Kickin**
Android app (Kotlin) that tracks live football/soccer scores using the API-Football API.
Built for CSC 214 Project 3. Package name: `com.nnita` (replace with your actual UR NetID).

---

## Architecture & Stack
- **Language**: Kotlin
- **Min SDK**: as per course standard
- **UI**: Jetpack Compose (no XML layouts)
- **Networking**: Retrofit + OkHttp
- **Image loading**: Picasso via `rememberPainter` bridge or `AndroidView` wrapper
- **JSON parsing**: Gson
- **Preferences**: SharedPreferences (or DataStore)
- **Charts/Stats**: MPAndroidChart via `AndroidView`
- **DI**: Manual (no Hilt/Dagger unless already set up)
- **Navigation**: `androidx.navigation:navigation-compose`

---

## App Structure — 6 Screens

### 1. Home (`HomeFragment`)
- Shows today's live/scheduled matches
- Each row: team crests (Picasso), score, match status (live minute or FT/NS)
- Pulls from `/fixtures?date=TODAY` endpoint or reads from `sample_fixtures.json`
- Refreshes on resume if in API mode

### 2. API A — Standings (`StandingsFragment`)
- League standings table for the user's selected league
- Columns: position, team crest (Picasso), team name, played, W/D/L, GD, points
- Data from `/standings` endpoint or `sample_standings.json`

### 3. API B — Match Detail (`MatchDetailFragment`)
- Launched by tapping a match on Home
- Shows: lineups, match statistics (possession, shots, corners, cards), goal scorers, timeline
- Use MPAndroidChart for stat bars (e.g., possession)
- Data from `/fixtures/statistics` and `/fixtures/lineups` or `sample_match_detail.json`

### 4. Settings (`SettingsFragment`)
- **Picker**: Favorite league (Premier League, La Liga, Serie A, Bundesliga, Ligue 1)
- **Radio buttons**: Display mode — Live Only / All Today / By League
- **Toggle/Switch**: Time format — 12hr vs 24hr
- **Switch (reserved)**: Data source — "From File" vs "Live API" (developer accommodation)
- All 4 settings persisted via SharedPreferences

### 5. Info (`InfoFragment`)
- Displays: app icon, app name, version name, version code, build date, copyright string
- Read version info programmatically from `BuildConfig`

### 6. Legal (`LegalFragment`)
- API-Football attribution and link to their TOS
- Picasso attribution (Square Open Source)
- MPAndroidChart attribution
- App icon attribution (if using third-party icon)
- ScrollView layout

---

## Compose Previews — REQUIRED FOR EVERY COMPOSABLE

**Every composable function must have a corresponding `@Preview` function directly below it. No exceptions.**

### Rules
- Every `@Composable` fun gets its own `@Preview` in the same file
- Preview function name: `Preview` + the composable name (e.g. `@Preview fun PreviewFixtureRow()`)
- Always annotate previews with both `@Preview` and `@Composable`
- Use `@Preview(showBackground = true)` as the default
- For dark mode: add a second preview with `@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)`
- Pass hardcoded fake/stub data into previews — never use ViewModels or real data sources inside a preview
- For screens that take a `NavController`, use a preview wrapper that passes `rememberNavController()`
- Group previews at the bottom of each file, after the composable they preview

### Example pattern
```kotlin
@Composable
fun FixtureRow(fixture: Fixture, onClick: () -> Unit) {
    // ...
}

@Preview(showBackground = true)
@Composable
fun PreviewFixtureRow() {
    FootballTrackerTheme {
        FixtureRow(
            fixture = Fixture(
                homeTeam = "Arsenal", awayTeam = "Chelsea",
                homeScore = 2, awayScore = 1, status = "FT"
            ),
            onClick = {}
        )
    }
}
```

### Fake data helpers
- Create a `PreviewData.kt` file in a `ui/preview/` package
- Populate it with `val previewFixture`, `val previewStandings`, `val previewMatchDetail`, etc.
- All previews import from here — keeps fake data in one place and out of production code

---
- `NavHost` with `NavController` via `navigation-compose`
- Bottom navigation bar (`NavigationBar` / `NavigationBarItem`) for: Home, Standings, Settings, Info, Legal
- Match Detail pushed onto the back stack from Home via `navController.navigate("match_detail/{fixtureId}")`
- Define all routes as constants in a `Screen.kt` sealed class

---

## Networking — Retrofit Setup

### Dependencies
```kotlin
// build.gradle.kts
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

### Retrofit Client (`network/RetrofitClient.kt`)
```kotlin
object RetrofitClient {
    private const val BASE_URL = "https://v3.football.api-sports.io/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-apisports-key", BuildConfig.FOOTBALL_API_KEY)
                .build()
            val response = chain.proceed(request)
            // Log remaining quota during development
            val remaining = response.header("x-ratelimit-requests-remaining")
            Log.d("API_QUOTA", "Requests remaining today: $remaining")
            response
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val instance: FootballApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FootballApiService::class.java)
    }
}
```

### Service Interface (`network/FootballApiService.kt`)
```kotlin
interface FootballApiService {

    @GET("fixtures")
    suspend fun getFixturesByDate(
        @Query("date") date: String,           // "YYYY-MM-DD"
        @Query("timezone") timezone: String = "America/New_York"
    ): ApiResponse<List<FixtureResponse>>

    @GET("fixtures")
    suspend fun getLiveFixtures(
        @Query("live") live: String = "all"
    ): ApiResponse<List<FixtureResponse>>

    @GET("standings")
    suspend fun getStandings(
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): ApiResponse<List<StandingsResponse>>

    @GET("fixtures/statistics")
    suspend fun getMatchStatistics(
        @Query("fixture") fixtureId: Int
    ): ApiResponse<List<MatchStatisticsResponse>>

    @GET("fixtures/lineups")
    suspend fun getLineups(
        @Query("fixture") fixtureId: Int
    ): ApiResponse<List<LineupResponse>>

    @GET("fixtures/events")
    suspend fun getEvents(
        @Query("fixture") fixtureId: Int
    ): ApiResponse<List<EventResponse>>
}
```

### Response Wrapper (`network/ApiResponse.kt`)
```kotlin
data class ApiResponse<T>(
    val get: String,
    val parameters: Map<String, String>,
    val errors: List<Any>,
    val results: Int,
    val paging: Paging,
    val response: T
)

data class Paging(val current: Int, val total: Int)
```

### API key in `local.properties`
```
FOOTBALL_API_KEY=your_key_here
```
Expose via `build.gradle.kts`:
```kotlin
buildConfigField("String", "FOOTBALL_API_KEY", "\"${properties["FOOTBALL_API_KEY"]}\"")
```

### Data Source Switch
All ViewModels check the preference before making any call:
```kotlin
val source = prefs.getString(KEY_DATA_SOURCE, "file")
if (source == "api") {
    val result = RetrofitClient.instance.getFixturesByDate(today)
    // use result.response
} else {
    // parse from assets/sample_fixtures.json
}
```



### Authentication
- **Base URL**: `https://v3.football.api-sports.io/`
- **Auth header**: `x-apisports-key: YOUR_KEY` — single header, no OAuth, no token exchange
- **Alternative** (if using RapidAPI): `x-rapidapi-key: YOUR_KEY` + `x-rapidapi-host: v3.football.api-sports.io`
- Key goes in `local.properties` → exposed via `BuildConfig` field — never hardcoded in source

### Rate Limits
- Free tier: **100 requests/day**, all endpoints accessible, no paywalled features
- Response headers tell you remaining quota:
  - `x-ratelimit-requests-remaining` — daily requests left
  - `X-Ratelimit-Remaining` — per-minute cap remaining
- Check these headers in your Retrofit `Interceptor` during development
- **100/day goes fast** — use `KEY_DATA_SOURCE = "file"` during all UI work, only switch to live for tests and final demo

### Response Envelope (every endpoint uses this)
```json
{
  "get": "fixtures",
  "parameters": { "date": "2026-04-21" },
  "errors": [],
  "results": 10,
  "paging": { "current": 1, "total": 1 },
  "response": [ ... ]
}
```
Always: check `errors` first → check `paging` → read `response`.
A `200` with an empty `response` is valid (e.g. no matches today). Always handle it.

### Key Endpoints

#### Fixtures (Home screen)
```
GET /fixtures?date=YYYY-MM-DD&timezone=America/New_York
GET /fixtures?live=all                        // all live matches globally
GET /fixtures?live=39-140-135                 // live, specific leagues only
GET /fixtures?ids=ID1-ID2-ID3                 // up to 20 IDs in one call
```
Each fixture object contains:
- `fixture.id`, `fixture.date`, `fixture.status.short` (NS/1H/HT/2H/FT/AET/PEN), `fixture.status.elapsed`
- `fixture.venue.name`, `fixture.referee`
- `league.id`, `league.name`, `league.logo`, `league.country`
- `teams.home.id`, `teams.home.name`, `teams.home.logo`
- `teams.away.id`, `teams.away.name`, `teams.away.logo`
- `goals.home`, `goals.away`

That's 14+ independent data points per fixture — well over the 8 required.

Status codes to know: `NS` = not started, `1H` = first half, `HT` = halftime, `2H` = second half, `FT` = full time, `AET` = after extra time, `PEN` = penalties

#### Standings (API A screen)
```
GET /standings?league=39&season=2025
```
Each entry: `rank`, `team.name`, `team.logo`, `points`, `goalsDiff`, `all.played`, `all.win`, `all.draw`, `all.lose`, `all.goals.for`, `all.goals.against`, `form`

#### Match Statistics (API B / Match Detail screen)
```
GET /fixtures/statistics?fixture=FIXTURE_ID
```
Returns per-team stats array: `Shots on Goal`, `Shots off Goal`, `Total Shots`, `Blocked Shots`, `Ball Possession` (e.g. `"45%"`), `Corner Kicks`, `Fouls`, `Yellow Cards`, `Red Cards`, `Offsides`, `Passes %`

#### Lineups (API B / Match Detail screen)
```
GET /fixtures/lineups?fixture=FIXTURE_ID
```
Returns starting XI and substitutes per team, each with `player.name`, `player.number`, `player.pos` (G/D/M/F)

#### Events (API B / Match Detail screen)
```
GET /fixtures/events?fixture=FIXTURE_ID
```
Returns timeline of goals, cards, substitutions with `time.elapsed`, `type`, `detail`, `player.name`, `assist.name`

### League IDs (hardcode these as constants)
```kotlin
object LeagueIds {
    const val PREMIER_LEAGUE = 39
    const val LA_LIGA = 140
    const val SERIE_A = 135
    const val BUNDESLIGA = 78
    const val LIGUE_1 = 61
    const val CHAMPIONS_LEAGUE = 2
}
```
Season parameter: use the **starting year** — 2025/26 season = `season=2025`

### Image URLs
- Team logos: returned in fixture response as `teams.home.logo` (CDN URLs, e.g. `https://media.api-sports.io/football/teams/33.png`)
- League logos: `league.logo` in fixture response
- **Image calls don't count toward daily quota** — but cache them, don't re-fetch on every render
- Load all images via the `PicassoImage` composable wrapper

---

## Sample Data Files
Location: `app/src/main/assets/`
- `sample_fixtures.json` — array of today's fixture objects
- `sample_standings.json` — standings response for one league
- `sample_match_detail.json` — combined stats + lineups for one fixture

All sample files must be real responses copied from API documentation or a real API call.
The data source switch in Settings must reload all UI from the correct source without restart.

---

## Data Points (8+ required by rubric)
Each fixture object exposes: team home name, team away name, home score, away score, match status, elapsed minute, league name, venue name, referee, date/time. That's 10+ independent points per fixture.

---

## Animations (3 required)
1. **Score flash**: Animate score `Text` background with `animateColorAsState` — brief yellow pulse when data refreshes
2. **Standings row slide-in**: Each standings row animates in from the left using `AnimatedVisibility` with `slideInHorizontally` + staggered `LaunchedEffect` delay per index
3. **Match detail card expand**: Stats card uses `animateContentSize()` modifier — expands from collapsed to full height on entry

Each animation composable must have its own `@Preview`. List all three in assessment package file.

---

## Localization
- Default: English (`res/values/strings.xml`)
- Additional: Spanish (`res/values-es/strings.xml`)
- All user-facing strings must be in strings.xml — no hardcoded UI text
- Scores and numbers use locale-aware formatting where applicable
- Note: API returns team names in English; show as-is (acceptable workaround per rubric)

---

## SharedPreferences
Key names (use constants, not raw strings):
```kotlin
const val PREF_FILE = "football_tracker_prefs"
const val KEY_LEAGUE = "pref_league"          // String, default "39" (Premier League)
const val KEY_DISPLAY_MODE = "pref_display"   // String: "live" | "all" | "league"
const val KEY_TIME_FORMAT = "pref_time_fmt"   // String: "12h" | "24h"
const val KEY_DATA_SOURCE = "pref_source"     // String: "file" | "api"
```
Load prefs in each screen's `ViewModel` via `init` block and expose as `StateFlow`. Screens collect with `collectAsStateWithLifecycle()`.

---

## Dialog
- **Type**: Match filter dialog (AlertDialog with 3 options)
- **Trigger**: Floating action button or menu item on Home screen
- **Options**: "Today" / "Tomorrow" / "This Weekend"
- **Effect**: Updates the date parameter used for fixture fetch and refreshes Home list
- List in assessment package file.

---

## Tests (Instrumented)

### Test 1 — Sample Data Reader
```
reads sample_fixtures.json from assets
parses into Fixture data class list
logs each fixture (teams + score) to logcat
tag: "SAMPLE_TEST"
```

### Test 2 — Live API
```
sends GET /fixtures?date=TODAY with real API key
logs raw JSON response to logcat
tag: "API_TEST"
assert response code 200
```

Both tests live in `app/src/androidTest/`.

---

## Libraries
1. **Picasso** (`com.squareup.picasso:picasso:2.8`) — team crest and league logo image loading
2. **MPAndroidChart** (`com.github.PhilJay:MPAndroidChart:v3.1.0`) — horizontal bar chart for match statistics (possession, shots, etc.)

Both attributed in Legal screen.

---

## Custom Feature (Branch: `feature/match-predictor`)
**Form-based match predictor**: On the Match Detail screen, add a "Predict" button that computes a simple win probability based on the two teams' last 5 results (fetched from `/fixtures?team=ID&last=5`). Display result as a percentage bar for each team. Branch off `main` after all other features are complete.

---

## Git Commit Plan (12 real commits minimum)
```
1.  init: project setup, package name, dependencies
2.  feat: app icon, custom fonts, color theme
3.  feat: bottom navigation + 6 fragment stubs
4.  feat: Home screen with sample fixture data
5.  feat: Standings screen with sample data
6.  feat: Match Detail screen with sample data
7.  feat: Settings screen with SharedPreferences
8.  feat: Info and Legal screens
9.  feat: 3 animations (score flash, slide-in, card expand)
10. feat: localization ES strings
11. feat: instrumented tests (sample + live API)
12. feat: API live switch + UI reload
13. feat: Picasso + MPAndroidChart integration
14. feat: match filter dialog
(branch) feat: match predictor custom feature
```

---

## Assessment Sheet Checklist
- [ ] API key listed
- [ ] Color theme defined (primary, secondary, accent hex values)
- [ ] 3 animations listed by name
- [ ] Dialog described
- [ ] Custom feature described
- [ ] Package name: `com.nnita`
- [ ] Upload folder: `nnita-p-3`
- [ ] Signed and dated

---

## Notes for Claude Code
- **Never hardcode text in composables** — every string in any UI element must reference `stringResource(R.string....)`. No raw string literals anywhere in composables, not even punctuation or labels that seem trivial. If it's visible to the user, it goes in `strings.xml` (and `strings-es.xml`)
- Create `ui/preview/PreviewData.kt` early and keep all fake preview data there
- Always check `KEY_DATA_SOURCE` preference before any network call — if `"file"`, read from assets instead
- Keep Retrofit service interface in `api/FootballApiService.kt`
- Keep all sample JSON in `assets/` (easier to read as InputStream than `res/raw/`)
- Use `viewModelScope.launch` for coroutines in ViewModels
- Screens are stateless composables — all state lives in ViewModels, passed down as parameters
- Picasso requires an `AndroidView` wrapper inside Compose — use a helper composable `PicassoImage(url, modifier)` so it's reusable and previewable (preview with placeholder drawable)
- MPAndroidChart requires `AndroidView` — wrap in a `MatchStatChart(stats, modifier)` composable with its own preview
- Never commit API key to git — use `local.properties` and `BuildConfig` field
