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
  interface. The default `GoodDrop` uses a 10-point `ScoreEffect`, while the default
  `BadDrop` uses a `DamageEffect`, preserving the existing gameplay values.
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
  a collision. Drop type remains separate so that gameplay events and presentation
  textures retain their current responsibilities.
- The Strategy implementation itself is complete in code. GM-7 as an academic
  requirement remains partial because its required problem/context/solution
  description and UML diagram have not yet been produced; those artifacts are
  outside the scope of this change.
- Additional drop types and effects (including healing, temporary, or combined
  effects) are planned for future iterations and are explicitly not implemented here.
