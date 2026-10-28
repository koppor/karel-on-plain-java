# Karel – requirements

Requirements of the Karel engine (`src/karel/Karel.java`) in [OpenFastTrace](https://github.com/itsallcode/openfasttrace) format.
Features (`feat`) describe what students and teachers see, requirements (`req`) what the engine has to do; every `req` is covered by an
`[impl->req~…~1]` tag in the code. Check: `./gg.cmd gh/itsallcode/openfasttrace trace docs src` (see `README.md`, section Tracing).

## Features

`feat~worlds~1`

Every task is a world with a start state and a goal; the engine tells at the end whether the goal was reached.

Needs: req

`feat~commands~1`

Karel is controlled by a handful of commands that follow Karel by Fred Overflow; wrong commands stop the program with a clear message.

Needs: req

`feat~conditions~1`

Karel can sense walls and beepers around it, and (since 1.2.0) measure distances with a sonar.

Needs: req

`feat~window~1`

A window shows the world step by step so that students see what their program does, and lets them inspect a run afterwards.

Needs: req

`feat~developer~1`

Teachers can run programs without a window, fix random worlds, and render world images for slides.

Needs: req

## Requirements

### Worlds

`req~world.text-map~1`

A world is defined by two text maps (start and goal): `.` empty, `*` one beeper, `2`–`9` that many beepers, `#` wall block,
`> ^ < v` Karel facing east/north/west/south, `E N W S` Karel on a beeper; an optional last line `karel on n` puts n beepers under Karel.
The same format is used to record every step of a run.

Needs: impl

Covers:

* `feat~worlds~1`

`req~world.random~1`

A world may be generated anew on every run; the system property `karel.seed` fixes the random sequence for tests and slides.

Needs: impl

Covers:

* `feat~worlds~1`
* `feat~developer~1`

`req~world.task-text~1`

Every world has a task text of one or two sentences; it is shown under the world in the window and printed to the console when the
world opens, so that a task is understandable without the slides.

Needs: impl

Covers:

* `feat~worlds~1`

`req~world.own~1`

Students can open an own world from two text maps given directly in their program (`openWorld(start, goal)`), or from a start map
alone without a goal check; the maps use the same characters as the built-in worlds, must have one Karel and rows of equal length,
and a start and goal of different size is an error with a message.

Needs: impl

Covers:

* `feat~worlds~1`

`req~world.goal-check~1`

When the program ends, the engine compares the world with the goal (beepers per cell, Karel's position and direction) and reports
"Goal reached in n steps" or what is still wrong.

Needs: impl

Covers:

* `feat~worlds~1`

`req~world.open-once~1`

`openWorld` must be called exactly once per run, before any command; a second call or a command without a world stops the program with a message.

Needs: impl

Covers:

* `feat~worlds~1`

### Commands

`req~cmd.move-forward~1`

`moveForward()` moves Karel one cell in its direction; a wall ahead stops the program with the message "wall ahead".

Needs: impl

Covers:

* `feat~commands~1`

`req~cmd.turn~1`

`turnLeft()`, `turnRight()` and `turnAround()` turn Karel by 90° counterclockwise, 90° clockwise and 180° without moving it.

Needs: impl

Covers:

* `feat~commands~1`

`req~cmd.beepers~1`

`pickBeeper()` takes one beeper from Karel's cell and fails with a message if there is none; `dropBeeper()` adds one beeper to the cell,
a cell can hold any number of beepers; Karel's supply is unlimited.

Needs: impl

Covers:

* `feat~commands~1`

`req~cmd.error-message~1`

A command that cannot be carried out throws a `KarelException` whose message names the command and the reason, so that IntelliJ's
Run window shows the message with a link to the line in the student's program.

Needs: impl

Covers:

* `feat~commands~1`

### Conditions

`req~cond.walls~1`

`frontIsClear()`, `leftIsClear()` and `rightIsClear()` are true if there is no wall (and no world border) on that side of Karel.

Needs: impl

Covers:

* `feat~conditions~1`

`req~cond.beepers~1`

`onBeeper()` is true if at least one beeper lies on Karel's cell; `beeperAhead()` if at least one lies on the cell in front of Karel.

Needs: impl

Covers:

* `feat~conditions~1`

`req~cond.sonar~1`

`distanceToWall()` returns how many times Karel can move forward before the wall (0 if the wall is in front);
`distanceToBeeper()` the number of moves to the next beeper ahead, or -1 if there is none before the wall. Neither moves Karel.

Needs: impl

Covers:

* `feat~conditions~1`

### Window

`req~window.world~1`

The window draws walls, beepers (green diamonds, with the count when more than one) and Karel (blue triangle pointing in its direction),
scaled to the window size; the title shows the engine version and the world.

Needs: impl

Covers:

* `feat~window~1`

`req~window.speed~1`

A speed slider sets the pause between steps while the program runs.

Needs: impl

Covers:

* `feat~window~1`

`req~window.step-slider~1`

A step slider follows the run; after the end the user can drag it to see the world after any step.

Needs: impl

Covers:

* `feat~window~1`

`req~window.show-goal~1`

The checkbox "Show goal" draws the goal state as an orange outline over the current world.

Needs: impl

Covers:

* `feat~window~1`

`req~window.replay~1`

After the end, "Replay" plays the recorded run again at the current speed without running the program again.

Needs: impl

Covers:

* `feat~window~1`

`req~window.status~1`

A status line shows the current step while running and the result (goal reached, goal not reached with reason, or the error) at the end;
the window stays open until the user clicks "Close".

Needs: impl

Covers:

* `feat~window~1`

### Developer mode

`req~dev.headless~1`

With `-Dkarel.headless=true` a program runs without window and delay and prints the result and the final map, so that solutions can be tested automatically.

Needs: impl

Covers:

* `feat~developer~1`

`req~dev.snapshot~1`

With `-Dkarel.snapshot=<file.png>` the engine saves the start world as an image and exits; `-Dkarel.snapshotGoal=true` adds the goal
outline, `-Dkarel.snapshotEnd=true` renders the goal state itself.

Needs: impl

Covers:

* `feat~developer~1`

`req~dev.version~1`

The engine carries a semantic version and release date (`VERSION`, `RELEASE_DATE`), shown in the window title and documented in `CHANGELOG.md`.

Needs: impl

Covers:

* `feat~developer~1`
