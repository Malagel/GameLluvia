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

## GM-4 — Abstract class and interface (implemented)

- `FallingItem` is the abstract parent class actively used by the gameplay loop. Its
  `GoodDrop` and `BadDrop` subclasses identify the beneficial and harmful drop types
  used by `DropSpawner` and `GameSession`; `GameScreen` renders their read-only type
  projections.
- `DropEffect` is the custom effect interface. Its concrete implementations are
  `ScoreEffect`, which awards a configurable number of points, and `DamageEffect`,
  which removes one player life and starts the existing damage animation.
- Each `FallingItem` stores a `DropEffect` and delegates `applyTo(Player)` to that
  interface. The strategy also reports the gameplay events caused by the operation.
  The default `GoodDrop` uses a 10-point `ScoreEffect`, while the default `BadDrop`
  uses a `DamageEffect`, preserving the existing gameplay values.
- Both required abstractions are therefore exercised during normal play: the session
  processes different items through the `FallingItem` parent type, and collision
  behavior is invoked polymorphically through `DropEffect`.

## GM-5 — Encapsulation, GRASP, and SOLID (implemented)

### Problems identified and focused changes

- `GameSession` previously inferred collision results from `FallingItem.Type.GOOD`
  or `BAD`. This coupled a drop's visual identity to its behavior and produced an
  incorrect event when a different strategy was injected. `DropEffect.apply` now
  returns the `GameplayEvent` values produced by the actual effect, and
  `GameSession` forwards those events without inspecting drop type. A list is used
  so a future combined effect can report several outcomes without changing the
  strategy contract.
- `GameScreen.drawPlayer()` previously advanced the hurt counter as a rendering side
  effect. `GameSession.update()` now advances it after each gameplay update, including
  suspended damage frames and the collision frame. Rendering only reads the hurt
  state and draws it. The original 50-frame animation, movement/gameplay suspension,
  and collision-frame timing are preserved.
- `GameSession` previously depended directly on `DropSpawner`, which made focused
  tests depend on time and randomness. The package-level `DropSource` abstraction now
  separates session control from spawn policy; `DropSpawner` remains the production
  implementation with the same interval, probability, positions, and speed.
- Presentation remains in `GameScreen`: it maps `FallingItem.Type` to textures and
  `GameplayEvent` to sounds. Effects mutate domain state and report outcomes but do
  not load assets, play audio, or perform collision detection.

### Encapsulation

- Mutable state remains private: `Player` owns its rectangle, lives, score, and hurt
  counter; `FallingItem` owns its bounds and selected strategy; `GameSession` owns
  the player and active-item collection. Changes go through behavior such as
  `move`, `takeDamage`, `fall`, and `applyTo`, rather than exposed fields.
- `Player.takeDamage()` prevents lives from becoming negative, and positive score
  awards saturate at `Integer.MAX_VALUE` instead of overflowing. `ScoreEffect`
  rejects non-positive awards, while `FallingItem` rejects null players, strategies,
  result lists, and event entries. These checks protect gameplay invariants at their
  owning boundaries.
- `GameSession.getFallingItems()` exposes an unmodifiable list of read-only
  projections containing only position and visual type. Mutable `FallingItem`
  references remain private to the session. Each projection is created once when an
  item spawns and reused while active, avoiding per-frame snapshots. `UpdateResult`
  and `FallingItem` also defensively copy their event lists before exposing them.

### GRASP

- **Information Expert:** `Player` updates movement, lives, score, and hurt duration;
  each `DropEffect` knows how to apply and describe its own outcome; `DropSpawner`
  owns timing and random drop selection; `FallingItem` owns falling and overlap data.
- **Controller:** `GameSession.update()` coordinates input, spawning, movement,
  collisions, effect execution, removal, game-over detection, and animation progress.
  `GameScreen` translates framework input into that call and presents its result.
- **High Cohesion:** domain mutation stays in model/effect classes, session flow in
  `GameSession`, creation policy in `DropSpawner`, and drawing/audio in `GameScreen`.
- **Low Coupling:** `GameSession` depends on `DropSource` and `DropEffect` outcomes
  rather than random/time APIs, concrete effects, or `GOOD`/`BAD` conditionals.
- **Polymorphism:** the loop handles every drop as `FallingItem`, applies behavior via
  `DropEffect`, and obtains drops through `DropSource`; concrete subclasses and
  strategies supply their variations.
- **Protected Variations:** effect behavior and spawn policy are behind stable
  interfaces. New drop subclasses may provide a distinct identity and default effect,
  while new strategies can return the appropriate event or multiple events.
- **Pure Fabrication / Indirection:** `GameSession` is a cohesive gameplay controller
  and `DropSource` is a small seam between it and nondeterministic spawning. This seam
  also enables deterministic unit tests without adding a factory or registry.

### SOLID

- **Single Responsibility:** `FallingItem` handles shared falling-item state and
  effect delegation, effects implement collision consequences, `DropSpawner` decides
  when/which item appears, `GameSession` controls gameplay, and `GameScreen` presents
  state. The boundaries are pragmatic: `GameSession` still coordinates several steps
  because that is its controller responsibility.
- **Open/Closed:** new `FallingItem` subclasses and `DropEffect` implementations can
  be added without changing collision execution. Returning event lists also permits
  a future combined strategy. A genuinely new texture identity or event still
  requires extending the presentation mapping; this is an intentional, explicit
  limitation rather than a speculative registry.
- **Liskov Substitution:** `GoodDrop` and `BadDrop` obey the `FallingItem` contract and
  are processed uniformly. Injecting `DamageEffect` into a visually good drop or
  `ScoreEffect` into a visually bad drop still produces correct state and events,
  demonstrating that behavior does not rely on subclass identity.
- **Interface Segregation:** `DropEffect` contains only effect application/reporting,
  and the internal `DropSource` contains only the two operations the session needs.
  Neither forces implementations to depend on rendering or session details.
- **Dependency Inversion:** `FallingItem` depends on the `DropEffect` abstraction and
  `GameSession` depends on `DropSource`; constructors supply the concrete strategies
  and production spawner. `GameScreen` consumes immutable event results rather than
  inspecting effect implementations.

### Verification and future considerations

- Unit tests cover the unchanged 10-point and one-life defaults, configurable and
  saturating score behavior, invalid score values, read-only item projections,
  identity/effect independence in both directions, defensive Strategy results, and
  hurt-animation advancement with movement suspended for the original duration.
- Future healing, bonus, temporary, or combined effects can implement `DropEffect`;
  future visual identities can extend `FallingItem.Type` and the texture mapping.
  Those features are deliberately not included in GM-5. If the number of identities
  or presentation events grows substantially, a dedicated presentation descriptor or
  mapper may become worthwhile, but adding one now would add indirection without a
  current need.
## GM-6 — Singleton and persistent local leaderboard (code implemented; UML pending)

### Problem and architectural context

- The game previously retained one high-score integer only in `GameLluviaMenu`, so
  it was lost when the application closed and could not represent a ranking. The
  gameplay HUD also mixed session information with that application-level value,
  and the menu had no route for consulting saved results.
- A leaderboard is shared application state: gameplay writes completed results and
  a separate screen reads them. It must have one authoritative ranking and one
  persistence lifecycle without making screens responsible for file formats or
  storage cleanup.

### Singleton solution and controlled lifecycle

- `HighScoreManager` is the single owner of ranking rules and saved-score access.
  Its constructor is private. `initialize(ScoreStore)` creates the instance once,
  `getInstance()` provides controlled access only after initialization, and
  `shutdown()` ends the application-scoped lifecycle without deleting persisted
  scores. The instance contains no textures, audio, stages, or other disposable
  LibGDX resources.
- `GameLluviaMenu.create()` supplies a `PreferencesScoreStore` and retains the
  returned singleton for dependency access by screens. `dispose()` shuts down the
  singleton after disposing the current screen and shared assets. Repeated
  initialization during the same lifecycle returns the same object.
- The Singleton is appropriate here because the local ranking must have exactly one
  coordinator during an application run. Storage remains behind the `ScoreStore`
  interface, avoiding a global dependency on LibGDX inside ranking logic and
  allowing simulated restarts in unit tests.

### Storage, ranking, and duplicate protection

- `PreferencesScoreStore` uses one LibGDX Preferences entry. Scores are serialized
  in descending order, and every write replaces that complete entry. The manager
  sorts after loading or recording and truncates its owned list to five before it
  writes, so results outside the top five are removed from persistent storage and
  cannot accumulate in hidden keys.
- Missing or blank data produces an empty ranking. Malformed or negative values are
  discarded while valid values are retained; overlong or noncanonical saved data is
  rewritten in normalized top-five form. Equal scores from distinct games remain
  valid separate leaderboard entries.
- Every started game receives an application-local positive completion identifier.
  `recordScore(id, score)` remembers the latest monotonically increasing identifier
  handled in the current lifecycle and ignores repeated callbacks for the same or
  an older completed game, including when its score did not enter the top five. The
  persisted data intentionally contains only the five scores; completion identifiers
  do not accumulate in memory or on disk.
- If `ScoreStore.save()` fails, the accepted score and completion identifier remain
  valid in memory, the manager marks the ranking as pending, and the exception does
  not interrupt game-over navigation. `hasPendingSave()` exposes that status, and
  `retryPendingSave()` rewrites the complete serialized top-five ranking. Repeated
  writes are therefore safe, and a duplicate completion can trigger a retry without
  adding the score twice. `recordScore` reports acceptance, not persistence success.
- `getScores()` returns an unmodifiable defensive copy, so screens cannot mutate the
  manager's state.

### Screen integration and navigation

- The redesigned Scene2D main menu shows **Game Lluvia** and the **Jugar**,
  **Puntuaciones**, and disabled **Opciones - Próximamente** buttons. Its stage uses
  the existing 640x360 `FitViewport`, keeping pointer coordinates correct after a
  resize.
- `LeaderboardScreen` reads the manager's snapshot, displays ranked scores in
  descending order, shows **Aún no hay puntuaciones.** for an empty ranking, and
  returns through **Volver**. It contains no persistence or ranking logic.
- `GameOverScreen` shows the completed score and supplies **Reintentar** and
  **Menú principal** actions. `GameLluviaMenu.showGameOver()` records the result
  before navigation. `GameScreen` sends its game identifier with the score and no
  longer draws a high score in the HUD; current score, lives, gameplay, sound,
  music, pause, and resume behavior remain unchanged.
- The small generated UI texture remains owned and disposed by `GameAssets`. Each
  menu-oriented screen owns only its stage and clears its input processor when it
  is hidden, preserving the established resource-ownership model.

### GRASP and SOLID considerations

- **Information Expert / High Cohesion:** `HighScoreManager` owns ranking and
  validation, the store adapter owns Preferences calls, screens own presentation,
  and `GameLluviaMenu` remains the navigation controller.
- **Low Coupling / Indirection / Dependency Inversion:** score logic depends on the
  narrow `ScoreStore` abstraction. Neither the manager nor its tests depend on a
  stage, renderer, or active LibGDX runtime.
- **Single Responsibility:** persistence access, ranking policy, navigation,
  resource ownership, and UI presentation remain in separate focused classes.
  `ScreenUi` centralizes only the shared visual styles so the three Scene2D screens
  are easy to restyle consistently.
- **Open/Closed and Interface Segregation:** another local persistence mechanism can
  implement the two-method `ScoreStore` contract without changing ranking logic.
  No player/account, online service, settings system, or speculative repository
  hierarchy was added.

### Verification and remaining academic work

- `HighScoreManagerTest` covers Singleton identity and initialization control,
  descending order, equal scores, the top-five limit, read-only access, persistence
  across simulated restarts, physical normalization of overlong storage, empty and
  malformed data, duplicate completed-game protection, retained in-memory state on a
  save failure, pending status, and safe retry without duplication.
- The complete Gradle test suite passes, including the pre-existing gameplay and
  strategy tests, and both core and desktop sources compile successfully.
- The code-level GM-6 implementation is complete. The GM-6 UML diagram required by
  the academic specification is deliberately still pending, as this iteration does
  not create that diagram. Configurable options, player identities, accounts,
  online leaderboards, and extra score metadata are also intentionally outside
  scope.

## GM-7 — Strategy pattern (partially implemented)

- The collision-effect architecture implements the code-level Strategy roles:
  `DropEffect` is the strategy interface, `ScoreEffect` and `DamageEffect` are the
  concrete strategies, and `FallingItem` is the context that delegates the effect.
- `GoodDrop` and `BadDrop` select their existing default strategies. They also accept
  an injected `DropEffect`, allowing collision behavior to be configured without
  changing `FallingItem` or coupling it to a concrete effect implementation.
- Runtime gameplay already executes the selected strategy when `GameSession` detects
  a collision. The strategy reports its actual `GameplayEvent` values; drop type is
  independent and is used only to select the current presentation texture. Thus an
  injected strategy cannot cause one outcome while the session reports another.
- `FallingItem` validates that a strategy returns a non-null list with no null
  entries, then returns an unmodifiable defensive copy. Tests cover multiple events,
  no events, invalid null results and entries, and mutable source lists.
- The Strategy implementation itself is complete in code. GM-7 as an academic
  requirement remains partial because its required problem/context/solution
  description and UML diagram have not yet been produced; those artifacts are
  outside the scope of this change.
- Additional drop types and effects (including healing, temporary, or combined
  effects) are planned for future iterations and are explicitly not implemented here.

## GM-8 — Template Method for UI screens (code implemented; UML pending)

### Problem and architectural context

- `MainMenuScreen`, `LeaderboardScreen`, and `GameOverScreen` repeated the same
  Scene2D lifecycle: create a stage and `FitViewport`, apply the initial window size,
  register and remove the input processor, clear/update/draw every frame, resize the
  viewport, and dispose the stage. Maintaining these copies made a lifecycle fix
  easy to omit from one screen and would have duplicated the same code again in a
  future Options menu.
- These three screens have the same rendering workflow and differ only in their UI
  actors and callbacks, making Template Method a direct fit. `GameScreen` and
  `PauseScreen` retain their separate rendering and interaction responsibilities and
  deliberately remain outside this hierarchy.

### Implemented solution and participants

- `BaseUiScreen` is the abstract Template Method parent and extends LibGDX
  `ScreenAdapter`. `MainMenuScreen`, `LeaderboardScreen`, and `GameOverScreen` are
  the concrete participants. `ScreenUi` continues to supply their shared label and
  button styles without taking on lifecycle responsibilities.
- The parent creates a stage with the shared `GameAssets` batch and a 640x360
  `FitViewport`. Its final `show`, `render`, `resize`, `hide`, and `dispose` methods
  define the common algorithm: initialize screen content once, register input,
  clear the frame, advance the stage with the existing capped delta, draw it, update
  and center the viewport after resizing, unregister input only when the stage owns
  it, and dispose the stage once.
- Concrete screens override the protected `buildUi(Stage, GameAssets)` hook. The
  main menu builds its title and four existing actions, the leaderboard builds its
  saved top-five or empty state and return action, and game over builds its completed
  score and retry/menu actions. Navigation remains delegated to `GameLluviaMenu`.
- UI construction is lazy on first `show` (and defensively ensured by `render`), so
  the abstract hook is never called from the base constructor before fields such as
  the completed-game score have been initialized. Re-showing a screen does not add
  its actors again, and attempting to render or resize an already disposed screen is
  rejected.

### Template Method, GRASP, and SOLID

- The implementation is a Template Method rather than a shared-field superclass:
  the abstract parent owns and fixes the lifecycle operations and calls the
  variation hook at the defined initialization step. Marking the critical lifecycle
  methods final prevents a concrete menu from silently bypassing input cleanup,
  viewport handling, frame sequencing, or disposal.
- **Information Expert / High Cohesion:** the base screen owns the lifecycle of its
  stage, each concrete screen owns its particular presentation, `ScreenUi` owns
  styling, and `GameLluviaMenu` remains the navigation controller.
- **Low Coupling / Protected Variations:** concrete screens no longer depend on the
  details of common Scene2D setup, while a future menu-style screen can vary only
  the actor-building hook. The base uses the existing `GameAssets` boundary and does
  not create or dispose shared resources.
- **Single Responsibility and Open/Closed:** lifecycle changes now have one reason
  and one place to change; another compatible UI screen can extend the template
  without copying it. The narrow hook avoids speculative factories or additional
  inheritance layers. Final workflow methods preserve substitutability and resource
  invariants for all concrete screens.

### Verification and remaining work

- `BaseUiScreenTest` verifies one-time initialization, input registration and
  conditional cleanup, capped stage update and drawing, centered viewport resizing,
  idempotent stage disposal, non-disposal of shared assets, rejection of reuse after
  disposal, final lifecycle methods, and participation of all three concrete UI
  screens. The existing gameplay, Strategy, and Singleton tests remain in the same
  suite.
- Automated unit tests cannot validate the appearance of Scene2D widgets or perform
  real desktop pointer interaction. Manual desktop verification should confirm the
  unchanged labels and layout, every menu transition and exit action, empty and
  populated leaderboards, retry/main-menu actions, and correct pointer coordinates
  after resizing to different aspect ratios.
- The code implementation for GM-8 is complete. The UML diagram required by the
  academic GM-8 specification remains pending and is intentionally distinguished
  from this completed refactor. The planned Options screen is also not implemented;
  it can use `BaseUiScreen` when its requirements are defined.

## GM-9 — Builder for drop spawn configuration (code implemented; UML pending)

### Problem and architectural context

- `DropSpawner` previously embedded its 100 ms interval and selected `GoodDrop` or
  `BadDrop` with a fixed conditional. Adding a third drop, changing a distribution,
  or defining a separate configuration would have required editing the runtime
  spawning algorithm and adding more branches.
- A spawning configuration has several related values and invariants: a positive
  interval, one or more non-null factories, and positive integer weights whose sum
  must be representable. Builder fits this construction because callers can add an
  arbitrary number of weighted entries incrementally and validate the complete
  configuration at one explicit `build()` step.

### Implemented solution and participants

- `DropSpawnProfile` is the immutable product. It retains the spawn interval, the
  weighted `DropFactory` registrations, and their validated total. Its nested
  `Builder` supplies `spawnIntervalMillis(long)` and `addDrop(int, DropFactory)`;
  constructor references such as `GoodDrop::new` and `BadDrop::new` satisfy the
  two-coordinate factory contract directly.
- `build()` rejects non-positive or nanosecond-overflowing intervals, an empty
  registration list, non-positive weights, null factories, and integer overflow in
  the accumulated weight. A completed profile defensively copies the registrations
  and returns an unmodifiable list. Reusing and then modifying a builder therefore
  cannot change profiles that it built earlier.
- `DropSpawner` accepts a profile through its public constructor. It remains
  responsible for elapsed-time checks, choosing a random point in the total integer
  weight, walking the registered weights, selecting the horizontal coordinate, and
  invoking the chosen factory at world height. Selection contains no drop-type
  conditional, so another registered factory does not require an algorithm change.
- Normal gameplay still creates `new DropSpawner()`. That convenience constructor
  delegates to `standardProfile()`, which uses the Builder to register a 100 ms
  interval, weight 6 for `GoodDrop`, and weight 4 for `BadDrop`. `GameSession`
  continues to consume only the existing `DropSource` abstraction.

### Compatibility and design principles

- The integer weights reproduce the original 60/40 distribution without requiring
  percentages to total 100. The session still receives an immediate initial drop;
  later drops require elapsed time strictly greater than 100 ms. Spawns retain the
  inclusive horizontal range from 0 through world width minus item size and the
  same world-height y coordinate. Falling speed, collisions, effects, scoring,
  damage, player movement, presentation, and audio code were not changed.
- **Encapsulation and immutability:** profile state is final, validation protects
  its invariants, and registration storage cannot be mutated through either the
  builder or getter after construction.
- **Information Expert / Creator / High Cohesion:** the Builder knows how to
  assemble and validate the product, the profile owns configuration, `DropSpawner`
  owns runtime creation policy, and `GameSession` remains the gameplay controller.
- **Single Responsibility and Dependency Inversion:** construction, stored rules,
  nondeterministic runtime behavior, and session flow remain separate. The session
  depends on `DropSource`, while the spawner depends on the supplied profile and its
  factory contract rather than concrete drop constructors.
- **Open/Closed and Protected Variations:** weighted selection works over any number
  of registrations. The existing `DropEffect` Strategy remains independent and is
  still executed by `FallingItem`; Builder configures creation without replacing or
  absorbing Strategy responsibilities.

### Verification and future considerations

- `DropSpawnProfileTest` covers successful construction, retained interval and
  weights, read-only registrations, snapshots made from a reused builder, invalid
  intervals, missing registrations, invalid weights, null factories, and total
  weight overflow. `DropSpawnerTest` uses controlled random values and time to cover
  exact weighted boundaries, one and several factories, the standard 6/4 profile,
  the immediate first drop, strict interval timing, vertical placement, and the
  inclusive horizontal boundary without flaky statistical assertions.
- The complete automated test suite and Gradle build pass, and `git diff --check`
  reports no whitespace errors. Automated tests do not replace a manual playthrough
  for audiovisual behavior, but the balance constants and all unrelated gameplay
  code remain unchanged.
- Future difficulty levels, additional drops, UI-created profiles, or runtime
  profile replacement can build or supply other profiles through the same boundary.
  None of those features, nor persistence or progressive difficulty, is implemented
  by this change.
- The code-level GM-9 implementation is complete. The UML diagram required by the
  academic GM-9 specification remains pending and is intentionally not created in
  this iteration.

## GM-5 follow-up — Player input separation (implemented)

- **Problem:** `GameScreen` both presented the game and read the keyboard (arrow keys,
  and later A/D). Control bindings were therefore mixed with rendering and audio, and
  that behavior had no unit tests.
- **Solution:** the `PlayerInput` interface (`isMovingLeft()`, `isMovingRight()`)
  expresses the player's intent. `KeyboardPlayerInput` is the production
  implementation: it reads an injected LibGDX `Input`, accepts several keys per
  direction, and `standard(Input)` binds the arrow keys and A/D simultaneously.
  `GameLluviaMenu` supplies it to `GameScreen`; `GameSession` and `Player` are unchanged.
- **GRASP:** Information Expert and High Cohesion (key bindings live in one class),
  Low Coupling and Protected Variations (`GameScreen` depends on an interface, not on
  the keyboard), and Polymorphism (control schemes are interchangeable).
- **SOLID:** Single Responsibility (the screen presents, the input class reads
  controls), Open/Closed (a new device is a new implementation), Interface Segregation
  (two methods only), and Dependency Inversion (`GameScreen` receives the abstraction
  through its constructor).
- **Verification:** `KeyboardPlayerInputTest` uses a mocked `Input` to cover each
  scheme, mixed schemes, ignored keys, custom bindings, defensive copying, and null
  arguments.
- **Not included:** actions other than horizontal movement (pause, exit) and key
  remapping are deliberately omitted until the game needs them.
- **Report note (GM-1):** the controls to describe are left arrow or A (left) and right
  arrow or D (right).
