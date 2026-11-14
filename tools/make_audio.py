#!/usr/bin/env python3
"""Generate gentle, license-free chime beds for the bundled lesson videos."""
import math
import struct
import sys
import wave

SAMPLE_RATE = 44100
AMPLITUDE = 0.085


def add_tone(buffer, start_sec, freq, duration_sec):
    start = int(start_sec * SAMPLE_RATE)
    length = int(duration_sec * SAMPLE_RATE)
    for index in range(length):
        position = start + index
        if position >= len(buffer):
            break
        t = index / SAMPLE_RATE
        attack = min(1.0, t / 0.02)
        decay = math.exp(-3.2 * t / duration_sec)
        envelope = attack * decay
        sample = math.sin(2 * math.pi * freq * t)
        sample += 0.18 * math.sin(2 * math.pi * freq * 2 * t)
        buffer[position] += AMPLITUDE * envelope * sample


def write_wav(path, duration_sec, notes):
    buffer = [0.0] * int(duration_sec * SAMPLE_RATE)
    for start, freq in notes:
        add_tone(buffer, start, freq, 1.35)

    with wave.open(path, "wb") as handle:
        handle.setnchannels(1)
        handle.setsampwidth(2)
        handle.setframerate(SAMPLE_RATE)
        frames = bytearray()
        for value in buffer:
            clipped = max(-1.0, min(1.0, value))
            frames += struct.pack("<h", int(clipped * 32767))
        handle.writeframes(frames)


def main():
    out_dir = sys.argv[1]
    write_wav(
        f"{out_dir}/count_to_3.wav",
        26.0,
        [(0.6, 523.25), (3.2, 659.25), (8.7, 783.99), (14.2, 659.25), (19.7, 783.99), (23.7, 523.25)],
    )
    write_wav(
        f"{out_dir}/more_and_less.wav",
        28.0,
        [(0.6, 523.25), (3.2, 659.25), (9.2, 783.99), (15.2, 880.00), (21.2, 659.25), (26.7, 523.25)],
    )
    print("Audio complete.")


if __name__ == "__main__":
    main()
