#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
if [ ! -f 'JSFML Project/build/orbit-run.jar' ]; then sh ./build.sh; fi
exec java -jar 'JSFML Project/build/orbit-run.jar' "$@"
