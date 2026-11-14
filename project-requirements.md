# Video Game Project (GM) – Project Requirements:

- **GM-1** Explanation of the game from the player's perspective, including objectives, narrative, gameplay elements, and controls, using mockups or screenshots.

- **GM-2** Analysis of the game from a software engineering perspective, specifying the base game and the proposed modifications.

- **GM-3** Design of a UML class diagram representing the game's domain model (LibGDX base classes + custom classes), along with their Java implementations.

- **GM-4** Design and implementation of:
  - One abstract class that serves as the parent of at least two classes and is actively used in the program.
  - One interface that is implemented by at least two classes and is actively used in the program.

- **GM-5** Application of encapsulation and object-oriented design principles (GRASP and SOLID).

- **GM-6** Application of the Singleton design pattern, including a description of the problem, context, solution, and UML diagram.

- **GM-7** Application of the Strategy design pattern, including a description of the problem, context, solution, and UML diagram.

- **GM-8** Application of the Template Method design pattern, including a description of the problem, context, solution, and UML diagram.

- **GM-9** Application of the Builder design pattern, including a description of the problem, context, solution, and UML diagram.

- **GM-10** Use of GitHub (at least 6 commits required).

# Updates and progress:

## Review scope and status

This review (2026-11-13) compares `README.md`, `docs.md`, the current Java sources
and tests, and local Git history through `cbc7c2f`. The original academic
requirements above are unchanged. Implemented code is distinguished from missing
report artifacts; the presence of tests is not evidence that they currently pass.

| Requirement | Current progress | Pending academic artifacts |
| --- | --- | --- |
| GM-1 | Player-facing explanation and in-game objective/controls implemented | Mockups or screenshots illustrating the game |
| GM-2 | Base-game comparison documented below; current architecture described in `README.md` | No additional artifact specified beyond the analysis |
| GM-3 | Java model and framework integration implemented; relationships described below | UML class diagram including LibGDX and custom classes |
| GM-4 | Required abstract class and interface actively implemented | None specific to GM-4 |
| GM-5 | Encapsulation, GRASP, and SOLID applications implemented and described | None specific to GM-5 |
| GM-6 | Singleton code and problem/context/solution description complete | Singleton UML diagram |
| GM-7 | Strategy code, runtime replacement, and problem/context/solution description complete | Strategy UML diagram |
| GM-8 | Template Method code and problem/context/solution description complete | Template Method UML diagram |
| GM-9 | Classical Builder roles and problem/context/solution description complete | Builder UML diagram |
| GM-10 | GitHub remote and commit/merge history present; six-commit minimum exceeded | None evidenced by the local review |

No screenshots or UML diagrams are supplied by this update. Runtime textures and
launcher icons are assets, not substitutes for GM-1 screenshots or GM-3 diagrams.

## GM-1 — Player perspective (description implemented; visual evidence pending)

- **Premise and objective:** the player controls a bucket in the rain, collecting
  beneficial blue drops and avoiding harmful red ones to earn the highest score
  before losing all three lives. There is no separate story campaign implemented.
- **Gameplay elements:** beneficial drops fall straight down and normally award
  10 points. Harmful drops sway sideways while falling, remove one life when
  caught, and trigger a brief hurt animation during which movement and gameplay
  updates are suspended. Missing a drop does not remove a life.
- **Progression:** at 300 points, calm rain becomes a storm for the remainder of
  the session. The sky darkens, the HUD shows **¡Tormenta! Gotas x2**, the spawn
  interval changes from 100 ms to 75 ms, and beneficial drops award 20 points.
  Beneficial drops already falling also acquire the doubled reward at the
  transition. Both phases retain the 60/40 beneficial/harmful distribution.
- **Controls:** left arrow or A moves left; right arrow or D moves right. Holding
  both directions cancels movement. The main menu explicitly explains the controls
  and objective. No keyboard-remapping or dedicated pause-key binding is implemented;
  pause/resume is handled by the existing screen/application lifecycle.
- **Presentation and navigation:** the HUD reports current points (**Puntaje**,
  not a count of collected drops) and lives, drawn after the drops so they remain
  visible. The main menu offers **Jugar**, **Puntuaciones**, **Opciones**, and
  **Salir**. Game over presents the completed score, retry, and main-menu actions.
  The leaderboard displays up to five locally saved scores. Options changes music
  and effects volumes independently from 0% to 100% in 10-point steps, saves changes
  immediately when storage succeeds, and previews effects-volume changes.
- `docs.md` contains the Spanish player overview, storm rules, and run instructions;
  `README.md` documents controls and architecture. History records the harmful-drop
  texture correction (`e0d9c82`), menu instructions (`d7f4a3d`), and HUD draw-order
  correction (`2587caf`). A manual desktop review and representative screenshots
  of the menu, calm/storm gameplay, leaderboard, options, and game over remain to
  be supplied; no visual validation is claimed here.

## GM-2 — Base game and engineering analysis (documented)

- **Base game:** the initial commit `a62a50d` already supplied a LibGDX bucket-and-rain
  game with three lives, 10-point beneficial drops, harmful drops, arrow-key input,
  music/sounds, menu, pause, game over, and one in-memory high score. `Tarro` mixed
  player state, input, drawing, and damage audio. `Lluvia` managed parallel position
  and type arrays, spawning, collisions, scoring, rendering, and audio. The original
  `GameScreen` coordinated these objects and advanced gameplay within rendering.
- **Engineering problems:** those mixed responsibilities coupled rule changes to
  presentation, exposed mutable geometry, and made behavior difficult to test
  independently of input, timing, randomness, and graphics. Repeated screen setup
  and asset ownership also complicated maintenance and resource cleanup.
- **Modifications now implemented, rather than merely proposed:** the modular
  `model`, `gameplay`, `input`, `score`, `settings`, and `screen` packages separate
  state, rules, controls, persistence, and presentation. `GameAssets` owns shared
  resources; `GameLluviaMenu` controls navigation. The game uses a 640x360 world
  with `FitViewport` and a fullscreen desktop launcher. New behavior includes A/D
  input, swaying harmful drops, score-driven storms, a persistent top-five ranking,
  enabled audio options, and consistent Scene2D menus.
- **Design response:** inheritance expresses movement variation; Strategy expresses
  collision effects and their runtime replacement; Singleton coordinates the local
  ranking; Template Method fixes menu construction/lifecycle; Builder assembles
  calm/storm spawn profiles. Observer-style notifications deliver gameplay outcomes
  without exposing event collections. The sections below connect these choices to
  concrete classes, constraints, and existing tests.
- **Scope:** additional control devices, new effects/drop kinds, more weather phases,
  player accounts, and online rankings are possible extensions, not implemented
  features. Local audio settings and the two-phase difficulty progression are
  already implemented and must not be listed as future work.

## GM-3 — Domain model and Java integration (code implemented; UML pending)

- `GameLluviaMenu` extends LibGDX `Game` and coordinates `Screen` transitions;
  `GameScreen`, `PauseScreen`, and `BaseUiScreen` extend `ScreenAdapter`. The four
  concrete menu-style screens inherit from `BaseUiScreen`, which owns a Scene2D
  `Stage`, `Table` layout, and `FitViewport` while sharing the application's assets.
- `GameScreen` uses `PlayerInput` and a `GameSession`. The session owns `Player`,
  active `FallingItem` instances, a listener, and an `EnumMap` of `Weather` to
  `DropSource`. `DropSpawner` implements `DropSource` and consumes `DropSpawnProfile`.
- `GoodDrop` and `BadDrop` extend `FallingItem`; each declares a `DropAppearance`.
  The parent and `Player` privately own LibGDX `Rectangle` bounds. `FallingItem`
  composes a `DropEffect`, implemented by `ScoreEffect` and `DamageEffect`, and
  returns a `GameplayEvent` when applying it.
- `RainProfileBuilder`, its two concrete builders, and `RainDirector` construct
  the immutable profile. `HighScoreManager` uses `ScoreStore`, implemented by
  `PreferencesScoreStore`; `AudioSettings` similarly uses `SettingsStore`, with
  `PreferencesSettingsStore` as its production adapter.
- These are relationships to represent in the required class diagram, not a UML
  deliverable themselves. The diagram must include relevant LibGDX base classes,
  custom classes, inheritance/interface realization, and ownership/dependencies.
  GM-3 remains incomplete as an academic requirement until that artifact is supplied.

## GM-4 — Abstract class and interface (implemented)

- `FallingItem` is the abstract parent used throughout the active-item gameplay
  loop. Its `GoodDrop` and `BadDrop` subclasses provide distinct visual identities
  and movement: straight falling versus sinusoidal horizontal drift. The final
  `fall(float)` template applies common vertical speed and delegates sideways
  movement to the protected `horizontalDrift(float)` hook, clamping drift inside
  the world. Inheritance therefore contributes behavior, not just type labels.
- `DropEffect` is the required interface with two active implementations:
  `ScoreEffect` awards configurable positive points and `DamageEffect` removes
  one life and starts the hurt state. `FallingItem.applyTo(Player)` invokes the
  selected strategy polymorphically and returns its single non-null outcome.
- Default beneficial drops use a 10-point reward and harmful drops use damage;
  storm builders create beneficial drops with a 20-point strategy. Runtime
  replacement is described under GM-7. `RainProfileBuilder` additionally has two
  concrete implementations, both used when a normal session is constructed.
- Existing `FallingItemMotionTest` and `DropEffectTest` exercise the movement and
  effect contracts. Test execution status is recorded separately below.

## GM-5 — Encapsulation, GRASP, and SOLID (implemented)

### Encapsulation and current interaction contracts

- `Player` owns private position, score, lives, and hurt duration; `FallingItem`
  owns private bounds and its replaceable effect; `GameSession` owns the player
  and active-item list. Behavior methods, rather than exposed fields or rectangles,
  perform mutation. Lives cannot become negative, positive awards saturate at
  `Integer.MAX_VALUE`, and `ScoreEffect` rejects non-positive point values.
- `DropEffect.apply(Player)` returns exactly one `GameplayEvent`, not an event
  list. `FallingItem.applyTo(Player)` rejects a null player or returned event, and
  `setEffect(DropEffect)` rejects null strategies. The old multi-event result,
  defensive event-list copies, and `UpdateResult` are no longer the contract.
- `GameSession.update(...)` returns no result collection. It pushes each collision
  outcome to the `GameplayListener` supplied at construction. `GameScreen` registers
  `GameAssets::playEventSound`; the event follows the actual effect, not the visual
  identity of the drop. No collision means no notification.
- `forEachFallingItem(FallingItemConsumer)` supplies only appearance and x/y values.
  Screens receive neither a collection nor mutable `FallingItem` references.
  `GameScreen` reuses a drawing callback created once; the obsolete item-list
  projections and per-frame event snapshots are not used.
- `HighScoreManager` exposes only `getScoreCount()` and `getScore(int)` for ranking
  reads. `DropSpawnProfile` copies registrations on construction and performs
  weighted selection internally through `createDrop(roll, x, y)`; registrations
  remain private. These owners expose operations, not collection getters.
- `GameSession.update()` advances the 50-frame hurt animation, including the
  collision frame and suspended frames. Rendering reads hurt state without
  changing its duration.

### Presentation boundaries and extension points

- `GameAssets` is the single mapping from `DropAppearance` to textures and from
  `GameplayEvent` to sounds. Drop appearances are identity objects declared by
  subclasses, not a `FallingItem.Type` enum that lists the parent's subclasses.
  The texture registry is a private map; the sound registry is an `EnumMap` checked
  for completeness at startup. Missing texture registrations fail on lookup.
- `GameScreen` asks `getDropTexture(appearance)` when drawing and does not branch on
  drop kinds or gameplay events. It uses per-weather `EnumMap` tables for sky colors
  and HUD labels. Domain effects do not load assets, play audio, or navigate screens.
- `Weather` declares phase thresholds and computes the phase from score. The session
  stores a `DropSource` per phase in an `EnumMap` and requires every phase to have
  a source. Adding a phase still requires configuring that source and its visual
  mappings; adding a drop appearance requires registering its texture. These are
  explicit composition/presentation changes, not a claim of unlimited extension
  without edits.

### GRASP and SOLID applications

- **Information Expert / High Cohesion:** `Player` protects player invariants,
  effects own collision consequences, the profile owns weighted selection,
  `DropSpawner` owns timing/randomness/positions, and assets own resource mappings.
- **Controller / Pure Fabrication:** `GameSession` coordinates movement, spawning,
  collisions, removal, weather, game over, and hurt progress. `GameLluviaMenu`
  coordinates application navigation; screens present state and actions.
- **Low Coupling / Indirection / Protected Variations:** `DropSource` separates
  sessions from nondeterministic spawning; effect, input, and persistence interfaces
  isolate their respective variations. Listener and drawing callbacks avoid exposing
  internal collections or model references to screens.
- **Polymorphism / Liskov Substitution:** the loop processes every drop through
  `FallingItem`, movement varies through its hook, and effects through `DropEffect`.
  Injecting damage into a visually beneficial drop, or points into a harmful one,
  still produces the corresponding state and event.
- **Single Responsibility / Dependency Inversion:** state, effects, profile
  construction, runtime spawning, input, ranking, persistence, and presentation have
  focused owners. Dependencies use `DropEffect`, `DropSource`, `PlayerInput`,
  `ScoreStore`, and `SettingsStore` at the relevant boundaries.
- **Open/Closed / Interface Segregation:** narrow contracts permit new effects,
  movement subclasses, devices, or persistence adapters without rewriting their
  consumers. The effect contract intentionally reports one event; combined or
  multi-event effects are not an implemented feature. Singleton callers still depend
  on `HighScoreManager`'s global access point, a deliberate tradeoff rather than
  complete dependency injection.

### Player input and audio options

- `PlayerInput` exposes only `isMovingLeft()` and `isMovingRight()`.
  `KeyboardPlayerInput` reads an injected LibGDX `Input`, defensively copies custom
  key arrays, and `standard(Input)` binds arrows and A/D simultaneously.
  `GameLluviaMenu` supplies it when creating `GameScreen`; the screen no longer
  reads movement keys directly. Other devices remain future implementations.
- `OptionsScreen` is implemented, not a disabled placeholder. `AudioSettings` owns
  bounded music/effects percentages, normalizes saved values, and saves through
  `SettingsStore`. `PreferencesSettingsStore` is the LibGDX adapter. Failed saves
  retain the selected in-memory volume, without claiming durable persistence.
  `GameAssets` applies effects volume on playback and music volume when starting
  the rain music.
- Existing tests cover effect/identity independence, invalid/null contracts, score
  saturation, hurt progression, callback visitation and notifications, keyboard
  bindings, and audio settings. Relevant suites include `DropEffectTest`,
  `GameSessionTest`, `KeyboardPlayerInputTest`, and `AudioSettingsTest`.

## GM-6 — Singleton and persistent local leaderboard (code and description complete; UML pending)

### Problem and context

The base game stored one high-score integer in the application, losing it on exit
and mixing it with session information in the HUD. Recording completed results and
showing a separate persistent ranking need one authoritative application-level
coordinator, without making screens responsible for ranking or storage formats.

### Solution and participants

- `HighScoreManager` is the application-scoped Singleton. Its constructor is private;
  `initialize(ScoreStore)` creates the instance once, `getInstance()` rejects access
  before initialization, and `shutdown()` ends the lifecycle without deleting scores.
  Repeated initialization returns the existing instance.
- `GameLluviaMenu.create()` initializes it with `PreferencesScoreStore`; the application
  does not retain a manager field or expose a forwarding getter. `showGameOver()`
  records through `HighScoreManager.getInstance()` before navigation, and
  `LeaderboardScreen` accesses the same Singleton directly. This access refactor is
  recorded in `e07e6c4`. Application disposal shuts it down after screens/assets.
- `ScoreStore` isolates ranking logic from LibGDX. The production Preferences adapter
  stores the complete comma-separated ranking in one entry and flushes writes.
  The manager sorts descending, retains at most five scores, preserves ties from
  distinct games, discards malformed/negative loaded values, and normalizes saved
  data when necessary. Missing or blank data gives an empty ranking.
- `recordScore(id, score)` rejects invalid ids/scores and ignores repeated or older
  completion ids, even for scores outside the top five. Only the latest handled id
  is retained in memory; persisted data contains scores, not accumulated ids.
- A failed save keeps the accepted score/id in memory and sets `hasPendingSave()`.
  `retryPendingSave()` safely rewrites the complete ranking; a duplicate completion
  can also trigger a retry without inserting again. Acceptance does not imply a
  successful durable save.
- Ranking reads use count and zero-based positional access, never snapshots or
  `getScores()`. The leaderboard builds descending rows or **Aún no hay puntuaciones.**
  and offers **Volver**. Game over shows the completed score and retry/menu actions;
  the gameplay HUD contains session score/lives, not the persistent ranking.

### Design, tests, and remaining work

Ranking is an Information Expert with high cohesion; `ScoreStore` provides
Indirection and Dependency Inversion, and screens remain presentation-only. The
Singleton supplies the requested shared lifecycle but introduces global access;
it does not own graphics/audio resources or model accounts/online services.
`HighScoreManagerTest` covers identity/initialization, top-five ordering, ties,
positional bounds, simulated restarts, normalization, malformed data, duplicate
completion protection, save failures, and retries. The Singleton UML diagram is
still pending; enabled audio options are already implemented separately.

## GM-7 — Strategy pattern (code and description complete; UML pending)

### Problem and context

A drop's appearance must not determine its collision consequence. The same
beneficial drop should award a different reward during a storm, including when it
was created before the weather changed. Selecting behavior only in constructors
would not demonstrate replacing the strategy of an existing object during play.

### Solution and participants

- **Strategy:** `DropEffect` declares `apply(Player)` and the default
  `intensified()` operation. **ConcreteStrategies:** `ScoreEffect` and `DamageEffect`.
  **Context:** `FallingItem`, whose final `applyTo(Player)` delegates to its current
  effect and returns one validated `GameplayEvent`.
- Drop constructors select defaults or accept an injected effect. `setEffect(...)`
  can replace that effect on an existing item, and `intensify()` installs the
  strategy returned by the current effect's `intensified()` method. `ScoreEffect`
  returns a doubled, saturating reward; damage retains the same strategy by default.
- `Weather.STORM` starts at 300 points. When the session detects the transition,
  it intensifies all remaining active items, so already-falling beneficial drops
  change from 10 to 20 points without changing their identity or motion. Subsequent
  drops come from the storm source, whose builder injects a 20-point reward.
  Score never decreases, so the storm persists to game over.
- The session emits the event returned by the actual strategy through its listener;
  presentation maps it to audio. Neither `FallingItem` nor the session switches on
  concrete effect types. Motion inheritance and collision composition remain
  independent axes of variation.

`DropEffectTest` covers delegation, replacement on the same context, intensified
rewards/damage, null rejection, and saturation. `GameSessionTest` covers the storm
threshold, calm behavior below it, source switching, and intensified drops already
falling. `RainBuilderTest` compares calm/storm rewards for the same drop class and
appearance. History distinguishes initial weather-based selection (`f6da544`)
from replacement on existing objects (`00c625d`). Code and problem/context/solution
are complete; the academic Strategy UML diagram remains pending. Healing,
temporary effects, and multi-event outcomes are not implemented.

## GM-8 — Template Method for UI screens (code and description complete; UML pending)

### Problem and context

Menu-style screens shared stage/viewport setup, input registration, frame updates,
resizing, disposal, title placement, and button styling. Copying that workflow let
lifecycle fixes or layout conventions diverge. The common sequence belongs in a
parent; only the title, screen content, and action callbacks need to vary.

### Solution and participants

- `BaseUiScreen` extends `ScreenAdapter` and is the abstract Template Method parent.
  `MainMenuScreen`, `LeaderboardScreen`, `GameOverScreen`, and `OptionsScreen` are
  its four concrete participants. `GameScreen` and `PauseScreen` deliberately remain
  outside the hierarchy.
- Final `show`, `render`, `resize`, `hide`, and `dispose` methods fix the workflow:
  initialize UI once, register the stage for input, clear/act/draw with capped delta,
  center the viewport on resize, clear input only if this stage owns it, and dispose
  the stage at most once. Shared batch/font/textures remain owned by `GameAssets`.
- UI construction is itself a fixed sequence: create a per-screen `ScreenUi` widget
  kit and fill-parent root table, add `ui.title(getTitle())`, then place a content
  table below it and call `buildContent(Table content, ScreenUi ui)`.
  Those two protected abstract methods are the current variation points; the former
  `buildUi(Stage, GameAssets)` hook is no longer used. Title placement is part of the
  template, not duplicated by each screen (`484a1d8`).
- Construction occurs on first `show` (also ensured by `render`), after the concrete
  constructor has initialized its fields. Re-showing does not duplicate actors;
  rendering or resizing a disposed screen is rejected. `ScreenUi` centralizes styles,
  labels, titles, Runnable-backed buttons, and standard menu-button rows without
  taking on navigation or resource ownership.

This applies High Cohesion, Protected Variations, SRP, and Open/Closed: compatible
screens implement the two variable steps rather than copying lifecycle/layout
code. Final operations protect common sequencing and resource invariants.
`BaseUiScreenTest` covers one-time initialization, title-before-content composition,
input ownership, capped updates/drawing, centered resizing, disposal, final lifecycle
methods, and all four subclasses. Actual appearance, pointer coordinates after
resize, and navigation/audio need manual desktop verification. The UML diagram
remains pending; Options is already a concrete implementation, not future work.

## GM-9 — Builder for rain profiles (code and description complete; UML pending)

### Problem and context

Embedding interval, distribution, and constructors in the runtime spawner mixed
configuration with time/randomness. Calm and storm rain need the same construction
steps but different values and beneficial strategies. The current implementation
uses the classical Builder roles, not only a fluent configuration helper.

### Solution and participants

- **Builder:** package-level `RainProfileBuilder` declares `buildSpawnInterval()`,
  `buildBeneficialDrops()`, `buildHarmfulDrops()`, and `getProfile()`.
- **ConcreteBuilders:** `CalmRainBuilder` supplies 100 ms, beneficial/harmful weights
  6/4, and default 10-point beneficial drops. `StormRainBuilder` supplies 75 ms,
  the same 6/4 weights, and beneficial drops with an injected 20-point `ScoreEffect`.
- **Director:** `RainDirector.construct(builder)` fixes interval, beneficial, harmful,
  then result retrieval in that order. Registration order assigns rolls 0–5 to
  beneficial drops and 6–9 to harmful drops for both standard profiles.
- **Product:** immutable `DropSpawnProfile`. Each concrete builder uses its nested
  fluent `DropSpawnProfile.Builder` internally to assemble/validate the product.
  This helper remains useful but is not a substitute for the Builder interface,
  ConcreteBuilders, and Director added in `dc1c1f1`.
- **Client/integration:** `GameSession.standardRain()` constructs both profiles through
  the director, wraps them in `DropSpawner` instances, and registers one `DropSource`
  per weather phase. `DropSpawner` takes a profile; the former no-argument constructor
  and `standardProfile()` convenience method are no longer present.
- The product validates a positive, nanosecond-representable interval, nonempty
  registrations, positive weights, non-null factories, and a non-overflowing total.
  It defensively copies registrations without exposing them and performs weighted
  selection itself. Out-of-range rolls and null factory results are rejected.
- `DropSpawner` owns only timing, randomness, and spawn positions, delegating selection
  to the profile. Initial calm drops are immediate; subsequent spawns require elapsed
  time strictly greater than the configured interval. The horizontal range includes
  both world boundaries, and new items start at world height. Builder configures
  creation; Strategy independently controls collision consequences and replacement.

Construction, immutable selection rules, runtime spawning, and session control are
separate cohesive responsibilities (Information Expert, Creator, SRP, and Protected
Variations). `RainBuilderTest` covers director step order and both concrete profiles,
including storm reward injection and unchanged harmful effects. `DropSpawnProfileTest`
covers validation, weighted boundaries, null results, and independence after fluent
builder reuse; `DropSpawnerTest` covers deterministic selection, timing, and positions.
Storm difficulty is implemented, while further profiles or UI-created configurations
remain possible extensions. The required Builder UML diagram is still pending.

## GM-10 — GitHub and commit history (minimum exceeded)

- The configured `origin` is `https://github.com/Malagel/GameLluvia.git`. Local history
  through `cbc7c2f` contains 58 reachable commits, exceeding the six-commit minimum,
  with feature, refactoring, test, and documentation commits and GitHub pull-request
  merge records (#2 through #6).
- Traceable milestones include the base import (`a62a50d`), responsibility separation
  (`1d2d8aa`), Strategy abstractions (`5c76913`), persistent Singleton (`3441f85`),
  UI template (`0103775`), initial profile builder (`c382fa2`), input abstraction
  (`27a32db`, `f519e17`), and enabled options (`6e162d9`).
- Later feedback is represented by non-collection gameplay APIs (`387e592`), positional
  ranking (`84c3163`), appearance/weather extension points (`ac0cc44`), live strategy
  replacement (`00c625d`), title/content template steps (`484a1d8`), and classical
  Builder roles (`dc1c1f1`), followed by the architecture documentation update
  (`67d2b39`) and merge (`cbc7c2f`).
- This evidence comes from local Git history and remote configuration. No network
  fetch or live GitHub verification is claimed by this documentation review.

## Verification status and remaining deliverables

- `./gradlew --offline --no-daemon :core:compileJava :lwjgl3:compileJava` passes
  for the production sources. `git diff --check` also passes.
- Restored `org.mockito:mockito-core:5.12.0` as a `testImplementation` dependency in
  `core/build.gradle`, as required by `KeyboardPlayerInputTest` and `BaseUiScreenTest`.
  `./gradlew --offline --no-daemon :core:test :lwjgl3:compileJava` passes after the
  dependency download: all 83 tests pass and desktop sources compile. The successful
  run was outside the agent sandbox, which blocks Mockito's inline mock maker from
  attaching its Java agent. No test behavior or production dependencies were changed.
- Pending academic artifacts are GM-1 mockups/screenshots, the GM-3 domain/framework
  UML class diagram, and the GM-6/7/8/9 pattern UML diagrams. Their absence does not
  mean the corresponding Java implementations are pending. This update creates
  none of those artifacts; changes are limited to redundant comment removal,
  renaming the gameplay score label to **Puntaje**, and restoring the Mockito test
  dependency.
- Manual desktop verification remains necessary for visual readability, drop textures
  and sway, storm presentation, audio/volume behavior, all navigation actions,
  persisted rankings/settings after restart, and pointer alignment after resizing.
