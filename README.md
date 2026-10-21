# Karel the Robot – PROG1

Karel in plain Java 25 (Swing, no libraries), works in IntelliJ with compact source files. Commands and conditions as in Karel by Fred Overflow (used in SE1 at UHH, sheets `PROG1/UHH/2025-WS/Übung/SE1_01_*`, `SE1_02_*`); worlds are our own text maps modelled on the UHH tasks.

## Files

| Path | Content | Moodle |
|---|---|---|
| `src/karel/Karel.java` | engine, version 1.0.0 (2026-10-21): worlds, commands, window (speed slider, "Show goal", goal check at program end) | yes |
| `src/FirstKarelProgram.java` | first program (three commands in `DEFUSE_ONE_BOMB`) for the first runs before the exercises | yes (in the ZIP) |
| `src/KarelExercise01.java` | exercise 01: starter file for students | yes |
| `KarelExercise01.zip` | complete IntelliJ project (`.idea/`, `KarelExercise01.iml`, `src/karel/Karel.java`, `src/FirstKarelProgram.java`, `src/KarelExercise01.java`), no top-level folder; build with `make_zip.ps1` after every change | yes |
| `intellij/` | template for the ZIP: `.idea/misc.xml` (JDK 25, language level 25), `modules.xml`, run configurations `FirstKarelProgram` and `KarelExercise01`, `KarelExercise01.iml` (source folder `src`) | – |
| `make_zip.ps1` | builds `KarelExercise01.zip` from `intellij/` + `src/` | – |
| `solutions/KarelExercise01Solutions.java` | sample solutions, after UHH `SE1_01_KarelLoesungen.pdf`, `repeat (n)` → `for` | **not** before the exercise |

Students extract the ZIP (Explorer: Extract All gives the folder `KarelExercise01`), open that folder in IntelliJ (File, Open; This Window/New Window; Trust Project with the Defender option unticked) and run `src/KarelExercise01.java`. Worlds are chosen with the enum: `openWorld(World.OBTAIN_ARTIFACT)`; `World.` + Ctrl+Space lists all. The project JDK is named `25` in `misc.xml`; if IntelliJ has no JDK with that name it offers Setup SDK (not yet tried in IntelliJ, 2026-10-21). The window stays open after the program ends and shows "Goal reached" or what is still wrong; close it to end the program. Errors (wall ahead, no beeper) appear in the Run window with a link to the line.

## Worlds (Karel 1.0.0, used in exercise 01)

| World | Needs | UHH |
|---|---|---|
| `obtainArtifact` | own command (walk around the pillar twice) | 1.2.2 |
| `defuseOneBomb` | own command, `for` | 1.2.3 |
| `climbTheStairs` | `for` + own command | 1.3.1 |
| `fillTheHoles` | `for` + own command | 1.3.2 |
| `mowTheLawn` | nested own commands | 1.3.3 |
| `defuseTwoBombs` | reuse `defuseOneBomb` | 1.5.1 |
| `practiceHomeRun` | `for` | 1.5.2 |
| `saveTheFlower` | two `for` loops | 1.5.3 |
| `stealOlympicFire` | reuse `climbTheStairs` | 1.5.5 |
| `hangTheLampions` | `while (frontIsClear())`; random ceiling heights | 2.2.1 |
| `walkTheLabyrinth` | `while (!onBeeper())` + nested `if`/`else`; random corridor, no crossings or dead ends | 1.5.7 |

Not taken over: `harvestTheField` (UHH 1.4.1, too hard as an algorithm, OK 2026-10-21).

## Developer notes

- Map format: `.` empty, `*` beeper, `#` wall block, `> ^ < v` Karel facing east/north/west/south, `E N W S` Karel on a beeper; at most one beeper per cell.
- Test without window and delay: `java -Dkarel.headless=true -Dworld=<WORLD_CONSTANT> [-Dkarel.seed=<n>] -cp <classes> solutions/KarelExercise01Solutions.java` prints the result and the final map. All 11 worlds tested with JDK 25 on 2026-10-21 (random worlds with seeds 1–25).
- Images for slides: `-Dkarel.snapshot=<file.png>` (optionally `-Dkarel.snapshotGoal=true` for the goal as outline, `-Dkarel.snapshotEnd=true` for the goal state itself) saves the world and exits; used for `PROG1 - CTS - 08` (seed 7, images in `C:\TEMP\InguanaTeX\karel-img`).
- New worlds: `world(name, start, goal)` in the static block, or a generator like `lampions()`. Version: `Karel.VERSION` + `Karel.RELEASE_DATE` and the header comment (semantic versioning: major = commands change incompatibly, minor = new worlds or features, patch = fixes); shown in the window title. Exercises are numbered separately: `KarelExercise01`, `KarelExercise02`, … Licence/open source: `PLAN-OSS.md`.
