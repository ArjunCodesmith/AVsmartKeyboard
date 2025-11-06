#!/usr/bin/env bash
# Generate a debug keystore (for local testing)
KEYSTORE=keystores/debug.keystore
mkdir -p $(dirname "$KEYSTORE")
keytool -genkeypair -keystore $KEYSTORE -storepass android -keypass android -alias debug -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=AV, OU=Dev, O=AVsmart, L=Unknown, S=Unknown, C=IN"
echo "Debug keystore created at $KEYSTORE (password: android)"
