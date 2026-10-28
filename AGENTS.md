# AGENTS.md – Karel

Rules for agent sessions in this folder. Everything technical is in `CONTRIBUTING.md`; read it first.

- `solutions/` is a separate private repository (`karel-on-plain-java-solutions`) cloned into this folder; never copy it anywhere students can see it before the exercise, never into the ZIP, never into this repository.
- Change the engine only with a requirement: add or update the `req` in `docs/requirements.md`, tag the code `// [impl->req~…~1]`,
  run `./gg.cmd gh/itsallcode/openfasttrace trace docs src` and keep it at "ok" (CI runs it too).
- After every change of `src/`: compile with `-Xlint:all`, run all `solutions/*.java` headless (random worlds with several seeds),
  rebuild the ZIP with `make_zip.ps1 -Exercise NN`.
- A version that was handed out is never changed; bump `VERSION`/`RELEASE_DATE` and write `CHANGELOG.md` instead.
- Task numbers already handed out never change (`CONTRIBUTING.md`, Worlds). New tasks get the next block of ten.
- Task texts in `TEXTS` and on the slides must stay identical; `README.md` lists them for students.
- Plans and ideas (Karel 2.0, next year) go to the local `PLAN.md` (git-ignored), not into code comments or the README.
- Doc comments are Markdown doc comments: `///` lines (JEP 467, Java 23+), never `/** ... */`; code in backticks, examples as fenced ```java blocks. Applies to the engine and to the sample solutions (pre- and postconditions as `/// Precondition:` / `/// Postcondition:`).
- String case conversion and formatting always with `Locale.ROOT`; test headless with `-Duser.language=tr -Duser.country=TR` as well.
- Changes reach `main` only through a pull request that OK reviews; never push to `main` directly. One squashed commit per release.
- Release notes on GitHub are the CHANGELOG section of that version, verbatim (Keep a Changelog headings), e.g. `awk '/^## \[1.3.1\]/{f=1} /^## \[1.3.0\]/{f=0} f' CHANGELOG.md`.
