# Copilot Instructions for ComicViewerPlugin

## Project Overview

**ComicViewerPlugin** is a Kotlin Multiplatform (KMP) project that implements a **PDF plugin** for the [ComicViewer](https://github.com/SorrowBlue/ComicViewer) app. It uses [MuPDF](https://mupdf.com/) (via the `mupdf-kmp` library) to render PDF pages.

The plugin is distributed as:
- An **Android APK** (the `:pdf:androidApp` module), which integrates with ComicViewer on Android via Android IPC (AIDL).
- A **Desktop application** (the `:pdf:desktopApp` module) using Compose Desktop, packaged with [Conveyor](https://www.hydraulic.dev/) for Windows and Linux distribution.

UI language in the app is **Japanese**.

---

## Module Structure

```
ComicViewerPlugin/
├── build-logic/                   # Convention plugins (Gradle build-logic included build)
│   └── src/main/kotlin/.../primitive/
│       ├── android-lint.gradle.kts
│       ├── detekt.gradle.kts
│       ├── git-tag-version.gradle.kts
│       └── license.gradle.kts
├── config/detekt/detekt.yml       # Detekt configuration
├── gradle/libs.versions.toml      # Version catalog (single source of truth for deps)
├── pdf/                           # Main KMP library module (:pdf)
│   ├── src/commonMain/            # Shared Compose UI (HomeScreen, etc.)
│   ├── src/androidMain/           # Android-specific: MainActivity, PdfService (AIDL), DocumentFileReader
│   ├── src/desktopMain/           # Desktop-specific: PdfPlugin, DocumentFileReader, ISeekableInputStream
│   ├── android/                   # Android submodule (:pdf:android) — AIDL interface definitions
│   ├── androidApp/                # Android app module (:pdf:androidApp) — the distributable APK
│   └── desktopApp/                # Desktop app module (:pdf:desktopApp) — Compose Desktop entry point
├── settings.gradle.kts
└── build.gradle.kts
```

---

## Key Architectural Details

### Android IPC (AIDL)
The Android plugin communicates with the ComicViewer host app via **Android Interface Definition Language (AIDL)**. The AIDL files live in `:pdf:android`:
- `IRemotePdfService.aidl` — the main service interface (exposed by `PdfService`)
- `FileReader.aidl` — page reading interface (`pageCount`, `loadPage`, `loadPageWithFortmat`, `fileSize`, `close`)
- `ISeekableInputStream.aidl` — seekable input stream provided by the host
- `CompressFormat.aidl` — enum for image compression (JPEG/PNG/WEBP)

`PdfService` (in `androidMain`) is a bound `Service` implementing `IRemotePdfService.Stub`. It creates `DocumentFileReader` instances per request, rendering PDF pages via `AndroidDrawDevice` (MuPDF) and returning file URIs via `FileProvider`.

### Desktop
The desktop path uses `PdfPlugin` / `PdfPluginImpl` (in `desktopMain`) to initialize MuPDF and create `DocumentFileReader` instances. Desktop packaging targets Windows (`.exe`) and Linux (`.deb`) using `TargetFormat.Exe`.

### Version Management
All module versions are derived from **git tags** using the custom `comicviewer.gitTagVersion` convention plugin. Running `git describe --tags --abbrev=1` yields the version at build time. If no tag is found, it defaults to `0.0.0`.

---

## Build & Toolchain

| Item | Value |
|------|-------|
| Kotlin | 2.3.21 |
| Java (JVM toolchain) | 21 (Eclipse Temurin / Adoptium) |
| Android compileSdk | 37 |
| Android minSdk | 30 |
| Android targetSdk | 37 |
| Compose Multiplatform | 1.10.3 |
| Gradle config cache | Enabled |
| Gradle build cache | Enabled |

All dependencies are declared in `gradle/libs.versions.toml` (the Gradle Version Catalog). Never add hardcoded dependency strings; always use `libs.*` references.

---

## Validation Commands

Run these to validate changes locally (in order):

```bash
# 1. Static analysis (detekt + ktlint)
./gradlew reportMerge --continue
./gradlew :build-logic:detektMain

# 2. Check version catalog consistency
./gradlew checkVersionCatalog

# 3. Android lint
./gradlew lintDebug

# 4. Build the Android APK
./gradlew :pdf:androidApp:assembleRelease

# 5. Build the desktop app (current OS)
./gradlew :pdf:desktopApp:packageDistributionForCurrentOS
```

All CI checks (detekt, lint, build) must pass before merging to `main`.

---

## Convention Plugins (build-logic)

Custom Gradle plugins live in `build-logic/src/main/kotlin/com/sorrowblue/comicviewer/plugin/primitive/`:

| Plugin alias | Purpose |
|---|---|
| `comicviewer.detekt` | Detekt + ktlint-wrapper + compose rules; SARIF output |
| `comicviewer.gitTagVersion` | Sets project `version` from `git describe` |
| `comicviewer.license` | Generates `aboutlibraries.json` for open-source license display |
| `comicviewer.lint` | Android lint configuration |

Every module applies these via `alias(libs.plugins.comicviewer.*)` in its `build.gradle.kts`.

---

## Code Style

- **Kotlin official** code style (`kotlin.code.style=official` in `gradle.properties`)
- Detekt with `autoCorrect = true` — many style issues are auto-fixed on running detekt
- Detekt config: `config/detekt/detekt.yml`
- Generated code and `buildkonfig` directories are excluded from detekt checks

---

## CI / GitHub Actions Workflows

| Workflow | Trigger | Description |
|---|---|---|
| `pr-checks.yaml` | PRs / push to `main` | Orchestrates all checks: detekt → lint → build |
| `static-code-analysis.yml` | Called by pr-checks | Runs detekt and version catalog lint |
| `lint.yml` | Called by pr-checks | Android lint (debug + release variants) |
| `build.yml` | Called by pr-checks | Assembles Android APK + desktop app |
| `production-release.yml` | Manual / tag | Production release workflow |
| `internalappsharing.yml` | Manual | Internal app sharing for Android |
| `update-licenses.yml` | Manual | Regenerates license JSON files |

Signing credentials for Android are passed via project properties (`androidSigningReleaseStoreFile`, etc.) — these are not committed.

---

## Common Errors & Workarounds

- **`git describe` fails / returns exit code non-zero**: The `gitTagVersion` plugin prints a warning and defaults to version `0.0.0`. This is expected in shallow clones. Run `git fetch --unshallow origin` followed by `git fetch --tags` to restore full tag history.
- **Signing config warnings at build time**: If release keystore properties are not set, Gradle logs `"androidSigningReleaseStoreFile not found"`. This is expected in CI for unsigned builds (debug/release without signing is still possible).
- **`RepositoriesMode.FAIL_ON_PROJECT_REPOS`**: Adding `repositories {}` blocks directly inside a module's `build.gradle.kts` will fail. All repositories must be declared in `settings.gradle.kts`.
