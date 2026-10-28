# Changelog

This file documents all notable changes to Karel.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), versions follow [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Changed

- Engine formatted with Netflix jfmt 0.9.0; `./jfmt.sh --check` runs in CI.

## [1.3.1] – 2026-10-28

Initial public release.

### Added

- Karel the Robot in one plain Java 25 file (Swing, no dependencies), for compact source files: `import static karel.Karel.*;`, `void main()`.
- Commands `moveForward`, `turnLeft`, `turnRight`, `turnAround`, `pickBeeper`, `dropBeeper`; conditions `frontIsClear`, `leftIsClear`, `rightIsClear`, `onBeeper`, `beeperAhead`; sonar `distanceToWall`, `distanceToBeeper`.
- 18 built-in worlds with start, goal and task text; random worlds (`-Dkarel.seed=<n>` fixes them); a cell holds any number of beepers.
- Own worlds from two text blocks: `openWorld(start, goal)`, or `openWorld(start)` without goal check.
- Window with step slider, speed slider, Show goal, status line, Replay; goal check at the end of the program; errors with a message that names the command and the reason.
- Developer mode: `-Dkarel.headless=true`, `-Dkarel.snapshot=<file.png>` (`-Dkarel.snapshotGoal`, `-Dkarel.snapshotEnd`).
- Requirements in `docs/requirements.md` with OpenFastTrace tags in the code; CI compiles with `-Werror`, runs the starter headless (also under a Turkish locale) and traces the requirements.
- `make_zip.ps1` builds the project students open, `Karel-v<version>.zip`.

[Unreleased]: https://github.com/koppor/karel-on-plain-java/compare/v1.3.1...HEAD
[1.3.1]: https://github.com/koppor/karel-on-plain-java/releases/tag/v1.3.1
