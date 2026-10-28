# Contributing to Karel

Developer notes for the engine, the worlds, the exercise ZIPs and the solutions. Users (students) read `README.md`; internal plans are in the local `PLAN.md` (not in the repository);
every release is in `CHANGELOG.md`. Rules for agent sessions: `AGENTS.md`.

## Files

| Path | Content | handed out |
|---|---|---|
| `src/karel/Karel.java` | the engine, one file: worlds (text maps and generators), task texts (in `WorldMaps`), commands, conditions, sonar, window, goal check, headless mode, snapshots; version in the header | yes (in the ZIP) |
| `src/FirstKarelProgram.java` | the starter file: three commands in `DEFUSE_ONE_BOMB`, the lists of commands, conditions and sonar as comments; students copy it per task | yes (in the ZIP) |
| `Karel-v1.3.1.zip` | complete IntelliJ project per exercise (`.idea/`, `KarelExerciseNN.iml`, `src/`), no top-level folder. 01 = Karel 1.1.0 as handed out 2026-10-21/23 (renamed `v2` when a second version was handed out); 02 = Karel 1.2.0, contains all worlds | yes |
| `intellij/` | template for the ZIP: `.idea/misc.xml` (JDK 25, language level 25), `modules.xml`, run configuration, `.iml` with source folder `src`; the name `KarelExercise01` in the template files is a placeholder replaced by the ZIP name | – |
| `make_zip.ps1` | builds `Karel-v<version>.zip` from `intellij/` + `src/`; run after every change of `src/` | – |
| `solutions/` | the private repository `karel-on-plain-java-solutions`, cloned into this folder (git-ignored here): one self-contained sample solution per world, same shape as a student file | never public; handed out after the exercise |
| `docs/requirements.md` | requirements in OpenFastTrace format, see Tracing | – |

## Build, test, release

- Compile: `javac -Xlint:all -d <classes> src/karel/Karel.java` (JDK 25 or newer; compact source files need 25).
- Test a solution without window and delay: `java -Dkarel.headless=true [-Dkarel.seed=<n>] -cp <classes> solutions/<World>.java`
  prints the task text, the result and the final map. Run all solutions, random worlds with several seeds, before every release.
- Version: `Karel.VERSION` and `Karel.RELEASE_DATE` plus the header comment, shown in the window title. Semantic versioning: major = commands
  change incompatibly, minor = new worlds or features, patch = fixes. A version that was handed out is never changed; the next change bumps it.
- Release: bump version, `CHANGELOG.md` (Keep a Changelog), `make_zip.ps1` (ZIP name = `Karel-v<version>.zip`, the extracted folder has that name), publish the ZIP as a GitHub release asset (and on the course platform); the release notes are the CHANGELOG excerpt for that version in Keep-a-Changelog format (`### Added` / `### Changed` / `### Fixed`), not a one-line summary; solutions are handed out only after the exercise.

## Worlds

- Text map: `.` empty, `*` one beeper, `2`–`9` that many beepers, `#` wall block, `> ^ < v` Karel facing east/north/west/south,
  `E N W S` Karel on a beeper; optional last line `karel on n` = n beepers under Karel. The run history and the goal check use the same text
  format, so every state must be expressible in it: at most 9 beepers on a cell other than Karel's.
- Data: `record WorldMaps(String text, String start, String goal)` (task text, start, goal); `WORLDS` maps a camelCase name to a `Supplier<WorldMaps>` (supplier, because random worlds are generated per run). Students' own worlds go through `openWorld(start, goal)` and never touch the registry.
- Fixed world: `world(name, start, goal)` in the static block. Random world: a generator method like `lampions()` registered with
  `WORLDS.put`; use `RANDOM` (seeded by `-Dkarel.seed`). Add the enum constant in `World`, the task text as first argument of `world(...)` or of the generator's `WorldMaps`, a solution in
  `solutions/`, a `req` if the engine gains behaviour, and the task to `README.md` with its number.
- Task numbers: one block of ten per part, numbers already handed out never change. Exercise 01 = 1 to 6 (extras unnumbered), methods 10 to 12,
  advanced 13 to 16, the 20s are reserved for Karel 2.0.
- Task text: one or two sentences, imperative, "changes on every run" for random worlds; the slides use the same wording.

## Images for slides

`-Dkarel.snapshot=<file.png>` saves the start world and exits; `-Dkarel.snapshotGoal=true` adds the goal outline, `-Dkarel.snapshotEnd=true`
renders the goal state. The deck build uses seed 7; images live in `Vorlesungen\00Template\PowerPoint\deckbuild\img\karel`, window
screenshots come from `deckbuild\karel-tools\WindowShot.java` (needs a display).

## Tracing (OpenFastTrace)

`docs/requirements.md` lists features (`feat~…~1`) and requirements (`req~…~1`); every `req` is covered by a `// [impl->req~…~1]` comment at the
code that implements it. Check with [OpenFastTrace](https://github.com/itsallcode/openfasttrace), run through
[gg.cmd](https://github.com/eirikb/gg) in the repo root (downloads the tool, works on Windows, macOS and Linux, nothing to install):

```
./gg.cmd gh/itsallcode/openfasttrace trace docs src      (Windows: gg.cmd gh/itsallcode/openfasttrace trace docs src)
```

"ok" and exit code 0 mean full coverage. CI (`.github/workflows/ci.yml`) runs the same command on every push through the gg GitHub Action (`eirikb/gg/action@main`, which passes the workflow token so tool lookups do not hit the API limit), after compiling the engine
with `-Xlint:all -Werror` and running the starter program headless; the solutions are not in the repository, so CI cannot run them. New engine behaviour: write the `req` first, then the code with its tag. Same tool and style as in SOFEN.

## Style

- Java: Netflix jfmt (Oracle conventions, 4 spaces), `{` on the same line, braces also around one-line bodies. Identifiers and comments in English.
- Doc comments as `///` Markdown doc comments (JEP 467), not `/** ... */`: backticks for code, fenced ```java blocks for examples. IntelliJ renders them in the quick documentation popup.
- Solutions follow the course rules: store a result in a variable, then use it (no nested calls); one method per idea; names are verbs.
- Locale: never call `toLowerCase()`/`toUpperCase()`/`String.format` without a locale; use `Locale.ROOT` (a Turkish student machine turned `FILL_THE_HOLES` into `fıll_the_holes`, 1.3.1). CI runs the starter with `-Duser.language=tr -Duser.country=TR`.
- The engine stays one file without dependencies, so that the ZIP is the whole project.
