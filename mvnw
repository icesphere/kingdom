#!/usr/bin/env sh
set -eu

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_VERSION="3.9.9"
MAVEN_DIR="$ROOT_DIR/.mvn/apache-maven-$MAVEN_VERSION"
MAVEN_BIN="$MAVEN_DIR/bin/mvn"

if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

if [ ! -x "$MAVEN_BIN" ]; then
  mkdir -p "$ROOT_DIR/.mvn"
  ARCHIVE="$ROOT_DIR/.mvn/apache-maven-$MAVEN_VERSION-bin.tar.gz"
  if [ ! -f "$ARCHIVE" ]; then
    curl -L "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" -o "$ARCHIVE"
  fi
  tar -xzf "$ARCHIVE" -C "$ROOT_DIR/.mvn"
fi

exec "$MAVEN_BIN" "$@"
