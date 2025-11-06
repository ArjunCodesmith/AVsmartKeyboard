# AVsmart Keyboard - GitHub Actions + Termux ready

Features:
- IME keyboard with QWERTY layout
- Top buttons: Settings, Auto Type, Clipboard, Paste
- Editable wordlist in Settings (used by Auto Type)
- Delay per word (default 500ms), choose space or enter after each word
- Clipboard history (last 5 items, auto-clear after 2 minutes)
- Dark material theme

Build locally (Termux):
1. Install JDK 17 and Android SDK.
2. From project root run:
   ```bash
   gradle wrapper --gradle-version 7.6.6
   ./gradlew assembleDebug
   ```

GitHub Actions: push to main to trigger `.github/workflows/android.yml` which builds APK and uploads artifact.
