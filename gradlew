#!/usr/bin/env bash
if [ -x "./gradlew.real" ]; then
  exec ./gradlew.real "$@"
fi
if command -v gradle >/dev/null 2>&1; then
  gradle "$@"
  exit $?
fi
echo "Gradle not found. On GitHub Actions runner gradle will be downloaded automatically."
exit 1
