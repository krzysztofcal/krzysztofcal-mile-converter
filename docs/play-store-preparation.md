# Google Play Store Preparation Checklist

This document tracks the steps required to publish Mile Converter on Google Play.

## App Summary

Mile Converter performs unit conversion locally on the device. It does not collect data, use analytics, display ads, require accounts, or make network requests.

## Current Technical Status

- Package name: `com.krzysztofcal.mileconverter`
- Version: `1.0.0` / `versionCode` 1
- Minimum SDK: 24
- Target SDK: 35
- Permissions: none
- Release format: Android App Bundle (`.aab`)

## Signing

- [ ] Create an upload keystore and keep it outside the repository:

  ```bash
  keytool -genkeypair -v -keystore upload-keystore.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
  ```

- [ ] Store the keystore file and passwords securely.
- [ ] Add these GitHub Actions secrets:
  - `KEYSTORE_BASE64`: base64-encoded `upload-keystore.jks`
  - `KEY_ALIAS`: key alias, for example `upload`
  - `KEY_PASSWORD`: key password
  - `STORE_PASSWORD`: keystore password
- [ ] For local signed builds, provide the same values as environment variables or Gradle properties:
  - `KEYSTORE_FILE`
  - `KEY_ALIAS`
  - `KEY_PASSWORD`
  - `STORE_PASSWORD`

## Build

Run tests:

```bash
./gradlew test
```

Build the release app bundle:

```bash
./gradlew bundleRelease
```

The generated bundle is written to:

```text
app/build/outputs/bundle/release/app-release.aab
```

## Play Console Setup

- [ ] Create a new app in Google Play Console.
- [ ] Choose app type: App.
- [ ] Choose pricing: Free, unless you intentionally want a paid app.
- [ ] Set app title: Mile Converter.
- [ ] Enable Play App Signing.
- [ ] Upload the signed `.aab` to an internal or closed testing track first.

## Store Listing Assets

- [ ] App icon: 512 x 512 PNG.
- [ ] Feature graphic: 1024 x 500 PNG or JPG.
- [ ] Phone screenshots: at least 2.
- [ ] Short description, up to 80 characters:

  ```text
  Convert miles, kilometers, and nautical miles quickly.
  ```

- [ ] Full description, up to 4000 characters:

  ```text
  Mile Converter is a simple distance conversion app for miles, kilometers, and nautical miles.

  Choose a source unit, enter a value, and instantly see the converted results. The app runs fully on your device and does not require internet access, accounts, ads, analytics, or extra permissions.
  ```

## Policy And Compliance

- [ ] Privacy policy: host `docs/privacy-policy.md` publicly, for example with GitHub Pages.
- [ ] Data safety:
  - Data collected: No.
  - Data shared: No.
  - Data encrypted in transit: Not applicable, because the app makes no network requests.
  - Data deletion: Not applicable, because the app does not collect or store user data.
- [ ] Ads declaration: No ads.
- [ ] App access: All functionality is available without login.
- [ ] Content rating: expected to be suitable for everyone.
- [ ] Target audience: choose the actual intended age group. If you include children, Google may require stricter family policy compliance.

## Testing And Release

- [ ] Upload the signed `.aab` to Internal testing.
- [ ] Install it from Google Play on a real device.
- [ ] Verify conversion results, rotation/state restore, and the About link.
- [ ] For new personal developer accounts, complete the required closed testing process before requesting production access.
- [ ] Submit the production release after testing and policy forms are complete.
