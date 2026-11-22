#!/bin/sh
set -eu
docker compose -f "$(dirname "$0")/../compose.yaml" up -d --build --wait
