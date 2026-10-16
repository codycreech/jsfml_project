# Orbit Run — JSFML project

The original movement/collision experiment is now a complete small obstacle-course game. Collect all five gold sparks and reach the green gate. The original bundled JSFML JAR is preserved.

## Build and play

Use JDK 17+ (the build emits Java 8-compatible classes).

Windows:

```bat
build.bat
run.bat
```

macOS/Linux:

```sh
sh build.sh
sh run.sh
```

Build scripts compile both views, run the game regression checks, and package `JSFML Project/build/orbit-run.jar`. They work from any current directory. The application uses procedural shapes and system fonts, so launching it does not depend on finding texture files in the working directory.

| Control | Action |
|---|---|
| Arrows or WASD | Move |
| P | Pause / resume |
| R | Restart the course |
| Esc | Quit |

The timer starts on movement. Losing focus pauses the run and clears held movement keys. The gate activates when every spark has been collected. Winning freezes the timer; restart begins a fresh run.

## Renderers

On Windows, the normal launcher tries the JSFML renderer and falls back to Java2D for a native-library or font-loading error. On other platforms it uses Java2D, because the bundled 2013 JSFML JAR contains Windows native binaries only.

- `run.bat --swing` / `sh run.sh --swing`: use the native-library-free Java2D view explicitly.
- `run.bat --jsfml`: require the JSFML renderer and report an error if it cannot start.
- `java -jar "JSFML Project/build/orbit-run.jar" --screenshot preview.png`: render a PNG without a desktop or JSFML native libraries.

The JSFML renderer uses the original Windows binaries and has not been runtime-tested on Windows in this pass. Java2D avoids that old native dependency. This branch does not replace the bundled JAR with an unverified external build.

## Fixes and optimization

Movement is measured in pixels per second; diagonal motion is normalized. Physics tests the proposed position against actual circle boundaries, resolves the two axes separately to allow sliding, and uses small spatial steps to prevent tunneling after a frame stall. The player stays in the arena and outside the HUD. Contact no longer produces the old 10-pixel kick in the opposite direction or traps the player against an obstacle.

`GameModel`, `Level`, and `Physics` have no rendering dependencies. Both views use the same state and rules. Shapes/fonts are reused, the Java2D grid is cached, and idle/paused rendering is reduced. Stalled frames are capped at 0.25 seconds to avoid teleporting or a large catch-up spike.

## Validation

The build passes 21 regression checks covering frame-rate independence, normalized diagonal speed, circle geometry, collision escape, tunneling, all arena boundaries, non-finite inputs, pause/resume, restart, collection, victory and a complete route through the shipped level. The full source compiles against the bundled JSFML JAR, and the packaged application produces a verified headless Java2D screenshot.

Interactive desktop testing, including the original Windows-native renderer, remains a manual check. The old textures and dyn4j JAR are retained as historical assets but are not needed by the finished course.
