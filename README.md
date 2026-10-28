# NutriTrack

NutriTrack is a FIT2081 Mobile Application Development coursework project built with Kotlin, Jetpack Compose and Room. It contains authentication, food questionnaires, insights, NutriCoach, clinician login and optional FruityVice/Gemini integrations.

## Stack and structure

`app/src/main/java/com/fit2081/fit2081assignment1/MainActivity.kt` is the entry point and Compose navigation host. Room entities and DAOs are under `data/`; repositories are under `repository/`; remote integrations are under `data/api/`; screens are top-level Kotlin files in the application package.

## Prerequisites and commands

- Android Studio with an Android SDK 35 installation.
- JDK 11 compatible with the checked-in Gradle wrapper.

```sh
cp gradle.properties.example gradle.properties
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

Set `GEMINI_API_KEY` only in local Gradle properties if you explicitly want to test Gemini. The portfolio build leaves Gemini disabled by default and displays a safe fallback message. FruityVice is a public network integration and may be unavailable offline.

The included `app/src/main/assets/UserDetail.csv` contains synthetic `DEMO-*` records only. It is not the university dataset. Use the original authorised dataset only in a private local copy and confirm publication rights before adding any real records.

## Verification status

- The Gradle distribution download was allowed, but the build process terminated after daemon startup without producing a diagnostic result; Kotlin/Room compilation remains unverified here.
- Emulator, Room migration and network integration tests remain unverified.
- The portfolio copy removes invalid manifest activity declarations, supplies the missing logo reference through the existing launcher resource, and adds synthetic seed data so the documented local flow has a reproducible input format.

## Security and publication notes

The previously hard-coded Gemini credential was removed from this copy. Rotate that Gemini key before any publication or continued use. A key supplied through Gradle properties is still embedded in an Android APK and is not a secure production secret; a server-side proxy is required for a real deployment. Patient-style data must remain synthetic unless ownership and release permission are documented. No licence is added because coursework, starter code and persona assets may have publication restrictions.

## Preparation change log

- Created a fresh repository copy without IDE state, local.properties, Gradle caches, generated builds or the original Git history.
- Removed the embedded Gemini key and added a disabled-by-default BuildConfig configuration path.
- Reduced the manifest to the actual launcher activity.
- Added synthetic seed data, Android-focused ignore rules and configuration documentation.
- Added PBKDF2 password hashing and a regression test, removed unused OpenAI and duplicate Ktor dependencies, and added clearer NutriCoach error states.
