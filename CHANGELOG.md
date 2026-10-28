# Changelog

All notable changes to Karel are documented in this file.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), versions follow [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [1.3.1] – 2026-10-28

### Fixed

- Crash on computers with a Turkish locale: the world name lookup used `toLowerCase()` without a locale, so `FILL_THE_HOLES` became `fıll_the_holes` (dotless ı) and no world was found. Now `toLowerCase(Locale.ROOT)`; CI runs the starter under `tr_TR`.

## [1.3.0] – 2026-10-28

### Added

- Own worlds: `openWorld(start, goal)` takes the two text maps directly from the student's program (text blocks), `openWorld(start)` opens a world without goal check ("Program ended after n steps"). Maps are checked: one Karel, rows of equal length, start and goal of the same size.

### Changed

- ZIP name carries the version: `KarelExercise02-Karel-v1.3.0.zip`.
- Engine internals: `record WorldMaps(String text, String start, String goal)` (task text included, the separate `TEXTS` map is gone) replaces the `String[]` pairs; all doc comments are `///` Markdown doc comments (JEP 467); generators return a `WorldMaps`; built-in and own worlds share one `open(...)`.

## [1.2.0] – 2026-10-27

### Added

- A cell can hold any number of beepers: `dropBeeper()` no longer fails on a cell with a beeper, `pickBeeper()` takes one of them. The window shows the number on the cell when there is more than one (centred on the beeper, in the corner when Karel stands on it). Map format: digits `2`–`9` and the optional last line `karel on n`.
- Sonar: `distanceToWall()` (free cells ahead) and `distanceToBeeper()` (steps to the next beeper ahead, -1 if none before the wall).
- Worlds for exercise 02 (methods with parameters and return values): `findTheCenter`, `carryTheHarvest`, `copyTheTower`; all three random per run.
- Advanced worlds for exercise 02 (added the same day, before the ZIP was handed out): `fetchTheBeeper` (sonar value as argument), `buildTheStaircase` (loop variable as argument, fixed world), `collectTheStacks` (adding up return values), `fenceTheField` (square of random size, sonar plus four equal sides).
- `make_zip.ps1 -Exercise NN` builds `KarelExerciseNN.zip`; `KarelExercise02.zip` for exercise 02.
- Documentation split: `README.md` for students, `CONTRIBUTING.md` for developers, `AGENTS.md` for agent sessions, a local `PLAN.md` for plans.
- Requirements in `docs/requirements.md` (OpenFastTrace: 5 features, 21 requirements) and `[impl->req~…~1]` tags in `Karel.java`; `./gg.cmd gh/itsallcode/openfasttrace trace docs src` reports full coverage; GitHub Actions CI (`.github/workflows/ci.yml`) compiles, runs the starter headless and traces on every push.
- Task text per world (`TEXTS` in `Karel.java`): one or two sentences under the world in the window and in the Run console when the world opens, so a task is self-contained without the slides (after Karel by Fred Overflow, OK 2026-10-27).

### Changed

- Sample solutions split into one self-contained file per world (`solutions/<World>.java`), same shape as the students' task files; the all-in-one file moved to `solutions/ARCHIVE/`.

## [1.1.0] – 2026-10-21

### Added

- Step slider above the speed slider: follows the run; after the end, drag it to see the world after any step. Replay moves it along.

### Changed

- Exercise project (ZIP): starter `KarelExercise01.java` removed; one file per task, copied from `FirstKarelProgram.java`, which now lists the commands and conditions.

## [1.0.0] – 2026-10-21

Initial release.
