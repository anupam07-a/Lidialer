# Lidialer

A personal native Android dialer for Samsung phones: human-centered recents, restrained frosted glass surfaces, a readable UI, and a three-item navigation dock with a subtle held-state lens interaction.

## Current implementation

The Compose app now contains a functional dialer shell with familiar phone-app information architecture:

- Keypad with digit entry, formatting-safe long-number sizing, delete, long-press clear, and tactile press feedback
- Contact matching while typing a number
- Recents grouped into Today / Yesterday with incoming, outgoing, missed, duration, SIM, and call actions
- Contacts with alphabetical browsing, search, favorites filtering, contact avatars, and quick call
- Contact detail view with favorite action, call action, and call history
- Central material roles (`Clear`, `Frosted`, `Obscured`) and shared visual tokens
- Dynamic environmental background that stays behind the material system
- Responsive bottom navigation with 48dp-class touch targets and accessibility labels
- `ACTION_CALL` foundation with runtime `CALL_PHONE` permission

The Android Telecom / `InCallService` layer is intentionally the next integration boundary. The current build hands calls to the native phone service, which keeps real calling safe while the in-app dialer UI is developed incrementally.

## Build

Open the project in Android Studio or import it into any Gradle-compatible Android workflow. The app targets Android API 35, supports API 26+, and uses Kotlin + Jetpack Compose + Material 3.

> Calling requires a physical device or an emulator configured for telephony. The first call-row tap requests `CALL_PHONE`; no call is placed until permission is granted.

## Direction

The display treatment is intentionally limited to the brand hero. The main UI remains a readable native sans-serif. Glass is used as material hierarchy rather than decoration: section surfaces are frosted, dock controls are interactive, and the lens response only appears during a hold.
