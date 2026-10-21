# Plan: Karel as open source (parked 2026-10-21)

Status: discussed, not started. No legal advice – check with THU before publishing.

## Rights

- Code written with Claude (Anthropic); Anthropic's terms leave the rights to the output with the user. Design decisions by OK (no IDE change, packages, worlds, labyrinth instead of harvestTheField). Purely machine-generated parts may not be protected (German law requires a "persönliche geistige Schöpfung"); a licence does no harm either way.
- No code or world data copied from Fred Overflow's Karel (github.com/fredoverflow/karel; no licence file seen in the clone). Command names are an interface; in the EU functionality and programming language are not protected (CJEU C-406/10, SAS Institute).
- Task ideas and short names (defuseOneBomb, hangTheLampions …) after SE1, UHH; ideas are not protected, texts on our slides are our own wording.
- Open: THU rules for teaching code written by professors (in Germany rights to teaching material usually stay with the professor).

## Licence options

| Licence | Pro | Contra |
|---|---|---|
| CC0 | maximum freedom, no attribution | not OSI-approved, explicitly excludes patent rights |
| MIT | short, well known, OSI-approved; only keep the notice | – |
| Apache-2.0 | like MIT plus patent grant | more than needed |

Proposal: `Karel.java` under MIT (`// SPDX-License-Identifier: MIT` in the header), exercise starter files (`KarelExercise01.java` …) under CC0, so students' solutions carry no licence notice.

## Repository

- Name distinct from Fred Overflow's Karel, e.g. `karel-java25` or `karel-intellij`.
- Contents: `src/karel/Karel.java`, exercise starters, README (installation as in `PROG1 - CTS - 08`, slide 4), LICENSE, CHANGELOG (semantic versioning as in the header of `Karel.java`).
- Releases: ZIP per exercise as release asset; Moodle links to the release.
- Sample solutions stay private (otherwise the exercises are spoiled); CI runs the headless check (`-Dkarel.headless=true`) with the private solutions or with neutral test programs.
- Courtesy: credit Richard Pattis (1981) and Fred Overflow in the README; tell Fred Overflow and the SE1 team at UHH.
