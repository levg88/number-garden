#!/usr/bin/env bash
# Rebuild the bundled lesson videos from source frames.
#
# Requirements (development machine only):
#   macOS + Xcode command line tools (swift), ffmpeg, python3
# The generated MP4 files are committed, so ordinary Android builds do not
# need any of this tooling.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT="$ROOT/tools/out"
ASSETS="$ROOT/app/src/main/assets/videos"

command -v swift >/dev/null || { echo "swift is required"; exit 1; }
command -v ffmpeg >/dev/null || { echo "ffmpeg is required"; exit 1; }
command -v python3 >/dev/null || { echo "python3 is required"; exit 1; }

rm -rf "$OUT"
mkdir -p "$OUT" "$ASSETS"

swift "$ROOT/tools/render_media.swift" "$OUT"
python3 "$ROOT/tools/make_audio.py" "$OUT"

encode() {
  local name="$1"
  ffmpeg -hide_banner -loglevel warning -y \
    -framerate 24 -i "$OUT/frames/$name/%05d.png" \
    -i "$OUT/$name.wav" \
    -c:v libx264 -preset medium -crf 21 -pix_fmt yuv420p \
    -profile:v high -level 4.0 \
    -c:a aac -b:a 96k \
    -shortest -movflags +faststart \
    "$ASSETS/$name.mp4"
}

encode count_to_3
encode more_and_less

for name in count_to_3 more_and_less; do
  echo "--- $name"
  ffprobe -v error -show_entries format=duration,size -of default=noprint_wrappers=1 "$ASSETS/$name.mp4"
done

rm -rf "$OUT"
echo "Videos written to $ASSETS"
