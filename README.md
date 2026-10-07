# Kickin ⚽

A live football (soccer) score tracker for Android, built with Kotlin and Jetpack Compose on top of the [API-Football](https://www.api-football.com) v3 API.

Built for CSC 214 (Mobile App Development) at the University of Rochester.

## Features

- **Home** — today's live and scheduled matches with team crests, scores, and live minute / FT / NS status. Quick league chips plus a **Filter & Sort** dialog (sort by time or A–Z, multi-select country and league filters with live search).
- **Standings** — league table (position, crest, played, W/D/L, goal difference, points) for the Premier League, La Liga, Serie A, Bundesliga, and Ligue 1.
- **Match Detail** — fixture header plus match statistics (possession, shots, corners, cards, …) shown as animated progress bars.
- **Match Predictor** *(branch `feature/match-predictor`)* — estimates win probability from each team's last 5 results (W=3 / D=1 / L=0, plus a home-advantage bonus).
- **Settings** — favorite league, 12h/24h time, live auto-refresh interval, and a **data source switch** between bundled sample JSON and the live API. All screens reload immediately when it changes.
- **Info & Legal** — version/build info and third-party attributions.
- Light and dark themes, English and Spanish localization.

## Tech Stack

| Area | Library |
| --- | --- |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Networking | Retrofit 2 + OkHttp 4, Gson |
| Images | [Picasso](https://square.github.io/picasso/) |
| Stat bars | [RoundCornerProgressBar](https://github.com/akexorcist/RoundCornerProgressBar) |
| State | ViewModel + StateFlow, SharedPreferences |

Min SDK 27, target SDK 36.

## Getting Started

1. Clone the repo and open it in Android Studio.
2. Get a free API key from [api-football.com](https://dashboard.api-football.com/register) (free tier is 100 requests/day).
3. Add the key to `local.properties` in the project root (this file is git-ignored):

   ```properties
   FOOTBALL_API_KEY=your_key_here
   ```

4. Build and run.

No key? The app still works. It starts in **From File** mode, which reads the sample responses bundled in `app/src/main/assets/`. Switch to **Live API** in Settings once a key is configured.

> **Note:** The key is compiled into `BuildConfig`, so don't publish APKs built with your personal key.

## Project Structure

```
app/src/main/java/com/nnita/kickin/
├── MainActivity.kt        # NavHost, bottom navigation, screen transitions
├── Screen.kt              # Route definitions
├── model/                 # API response and domain models
├── network/               # Retrofit client and API service
└── ui/
    ├── home/              # Fixtures list + filter dialog
    ├── standings/         # League table
    ├── matchdetail/       # Stats (and predictor on the feature branch)
    ├── settings/          # Preferences
    ├── info/  legal/
    ├── components/        # PicassoImage, TeamIcon
    ├── preview/           # Fake data for @Preview functions
    └── theme/             # Colors, typography (Outfit, DM Sans)
```

## Tests

Instrumented tests live in `app/src/androidTest/`:

- `SampleDataTest`: parses `sample_fixtures.json` from assets and logs each fixture (tag `SAMPLE_TEST`).
- `LiveApiTest`: calls `GET /fixtures?date=TODAY` with your API key and asserts HTTP 200 (tag `API_TEST`). This test needs a key and uses one request from your daily quota.

```bash
./gradlew connectedAndroidTest
```

## Attribution

- Football data from [API-Football](https://www.api-football.com). Used under their terms of service.
- [Picasso](https://github.com/square/picasso) by Square, Apache 2.0.
- [RoundCornerProgressBar](https://github.com/akexorcist/RoundCornerProgressBar) by Akexorcist, Apache 2.0.
