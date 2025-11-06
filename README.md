# AVsmart Keyboard

This is a GitHub-ready Android project for **AVsmart Keyboard** (Kotlin).
Push this repository to GitHub and the included workflow will build a debug APK automatically.

## How to use
1. Unzip / clone this repo.
2. `git init` / push to GitHub.
3. GitHub Actions will run and upload APK as artifact.

## Keystore (debug)
This repo includes `scripts/generate_debug_keystore.sh` which creates a debug keystore locally:
```
chmod +x scripts/generate_debug_keystore.sh
./scripts/generate_debug_keystore.sh
```
You can then sign the APK locally or configure GitHub Actions to use it via secrets.
