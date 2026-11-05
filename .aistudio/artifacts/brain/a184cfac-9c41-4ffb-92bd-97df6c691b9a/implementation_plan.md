# Bazarly — Authentication Gate Unlock & Swiss-Industrial Layout Plan

## 1. Fix Google Credential Manager & Unlock the App (`uid = "dev-operator"` Fallback)
- **Primary Flow**: Clicking **"SIGN IN WITH GOOGLE"** invokes Jetpack `CredentialManager.getCredential()` with `GetSignInWithGoogleOption` (`R.string.default_web_client_id`), extracts `GoogleIdTokenCredential`, passes it to `Firebase.auth.signInWithCredential()`, and transitions immediately to `BazarlyPosterLandingScreen`.
- **Emulator / Dev Fallback Bypass**: On the AI Studio Streaming Android Emulator (where no Google Play account is pre-signed into the virtual device, causing `CredentialManager` to throw `NoCredentialException` or cancel), automatically fall back to injecting an authenticated operator session (`uid = "dev-operator"`) in `MainViewModel` and `MainActivity` and immediately transition away from the gate into the full 7-section `BazarlyPosterLandingScreen` so Vision Metrology, Bilingual Voice ERP, and bKash Mode `0011` rails unlock on the very first click of **"SIGN IN WITH GOOGLE"**.

## 2. Fix Telemetry Header String Concatenation & Non-Wrapping Layout
- Render the exact string `"REF // BZR-OS-4.2 // ASIA-SE1 // 23.8103° N, 90.4125° E"` in a single non-wrapping row (`fontSize = 9.sp`, `letterSpacing = 0.03.em`, `maxLines = 1`, `softWrap = false`, `overflow = TextOverflow.Ellipsis`) so the coordinates never concatenate with `ASIA-SE1` or wrap onto a second line.

## 3. Eliminate Orphan Words in the `SPEC.01 – SPEC.03` Block
- Format `SPEC.01`, `SPEC.02`, and `SPEC.03` as single-line flex rows (`Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween)`) at `fontSize = 9.sp` and `letterSpacing = 0.02.em` with a `6.dp` vertical gap between rows so `"TRUST"` and `"USD"` never wrap onto new lines across `360dp–430dp` mobile viewports.

## 4. Resolve Bottom `"BAZARLY"` Watermark & Camera Graphic Overlap
- Convert the bottom `"BAZARLY"` poster text into a subtle background watermark (`alpha = 0.08f` / neutral `#D0D0CC`, `padding(horizontal = 16.dp, bottom = 12.dp)`) so the `"B"` and `"Y"` glyphs never clip against the rounded screen edges.
- Position the 3D optical sensor rig (`AuralisStudioSubject`) cleanly in the middle-right space above the bottom watermark so there is zero harsh dark-on-dark collision over `"ARL"`.
