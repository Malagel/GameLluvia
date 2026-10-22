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
  used by `DropSpawner`, `GameSession`, and `GameScreen`.
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
- The Strategy implementation itself is complete in code. GM-7 as an academic
  requirement remains partial because its required problem/context/solution
  description and UML diagram have not yet been produced; those artifacts are
  outside the scope of this change.
- Additional drop types and effects (including healing, temporary, or combined
  effects) are planned for future iterations and are explicitly not implemented here.

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
- `Player.takeDamage()` prevents lives from becoming negative, `ScoreEffect` rejects
  non-positive awards, and `FallingItem` rejects null players, strategies, result
  lists, and event entries. These checks protect important gameplay invariants at
  their owning boundaries.
- `GameSession.getFallingItems()` exposes an unmodifiable view, while `UpdateResult`
  defensively copies its events and exposes an unmodifiable list. `FallingItem`
  likewise copies the strategy's result before returning it, so callers cannot alter
  internal event collections. Falling items themselves remain mutable gameplay
  entities because the session must advance them; presentation is expected to use
  only their query methods.

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

- Unit tests cover the unchanged 10-point and one-life defaults, configurable score
  behavior, invalid score values, identity/effect independence in both directions,
  correct event reporting, immutable result events, and hurt-animation advancement
  with movement suspended for the original duration.
- Future healing, bonus, temporary, or combined effects can implement `DropEffect`;
  future visual identities can extend `FallingItem.Type` and the texture mapping.
  Those features are deliberately not included in GM-5. If the number of identities
  or presentation events grows substantially, a dedicated presentation descriptor or
  mapper may become worthwhile, but adding one now would add indirection without a
  current need.
