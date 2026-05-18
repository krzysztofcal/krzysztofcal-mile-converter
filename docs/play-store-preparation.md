# Google Play Store Preparation Checklist

This document outlines the steps required to publish Mile Converter on the Google Play Store.

## About This App

Mile Converter performs unit conversion locally on the device. It does **not** collect data, use analytics, display ads, or require network access.

---

## Checklist

### Account & Developer Setup

- [ ] Verify Google Play developer account (one-time $25 USD registration fee)
- [ ] Accept the latest Google Play Developer Distribution Agreement

### Signing

- [ ] Create an upload keystore:
  ```
  keytool -genkey -v -keystore upload-keystore.jks \
    -alias upload -keyalg RSA -keysize 2048 -validity 10000
  ```
- [ ] Store the keystore **securely outside** the repository (never commit it)
- [ ] Add the following secrets to the GitHub repository (Settings → Secrets → Actions):
  - `KEYSTORE_BASE64` — base64-encoded keystore file
  - `KEY_ALIAS` — key alias used during keystore generation
  - `KEY_PASSWORD` — key password
  - `STORE_PASSWORD` — keystore password
- [ ] Configure signing in `app/build.gradle.kts` using the secrets above
- [ ] Update the release workflow to sign the AAB before upload

### Build

- [ ] Build a signed release AAB:
  ```
  ./gradlew bundleRelease
  ```
- [ ] Verify the AAB is signed with `jarsigner -verify`

### Play Console Setup

- [ ] Create a new app in [Google Play Console](https://play.google.com/console)
- [ ] Choose "App" type and "Free" pricing
- [ ] Set the default language and app title: **Mile Converter**

### Store Listing Assets

- [ ] Prepare at least 2 screenshots per required screen size (phone)
- [ ] Prepare a 512 × 512 px app icon (PNG, 32-bit, no alpha border)
- [ ] Prepare a 1024 × 500 px feature graphic (JPG or PNG)
- [ ] Write a short description (≤ 80 characters)
- [ ] Write a full description (≤ 4000 characters)

### Policy & Compliance

- [ ] Fill in the **Data Safety** form:
  - No data collected or shared
  - No data encrypted in transit (no network calls)
  - Users can request deletion (not applicable — no data stored)
- [ ] Complete the **Content Rating** questionnaire (expected rating: Everyone)
- [ ] Provide a **Privacy Policy** URL (see `docs/privacy-policy.md`; host it publicly, e.g. GitHub Pages)

### Testing & Release

- [ ] Upload the signed AAB to the **Internal Testing** track
- [ ] Add internal testers and verify the app installs and functions correctly
- [ ] Promote to **Closed Testing (Alpha)** or **Open Testing (Beta)** as needed
- [ ] Promote to **Production** when ready

---

## Notes

- Signing is intentionally left for a separate task/PR.
- Do not commit keystore files or plain-text credentials to the repository.
- The app currently targets API 24+ (Android 7.0) and requires no special permissions.
