# Lidialer

A personal native Android dialer for Samsung phones: human-centered recents, restrained frosted glass surfaces, a readable UI, and a three-item navigation dock with a subtle held-state lens interaction.

## Current slice

This repository contains the first Compose vertical slice:

- Dark-first Recents screen with Today / Yesterday grouping
- Collapsing `Phone` hero and sticky call search bar
- Frosted section surfaces and restrained call-row metadata
- Keypad / Recents / Contacts bottom dock
- Long-press dock feedback for the lens state
- Three-dot menu shell
- `ACTION_CALL` foundation with runtime `CALL_PHONE` permission

## Build

Open the project in Android Studio or import it into any Gradle-compatible Android workflow. The app targets Android API 35, supports API 26+, and uses Kotlin + Jetpack Compose + Material 3.

> Calling requires a physical device or an emulator configured for telephony. The first call-row tap requests `CALL_PHONE`; no call is placed until permission is granted.

## Direction

The display treatment is intentionally limited to the brand hero. The main UI remains a readable native sans-serif. Glass is used as material hierarchy rather than decoration: section surfaces are frosted, dock controls are interactive, and the lens response only appears during a hold.
