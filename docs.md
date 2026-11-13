# Project overview

GameLluvia is a small desktop game in which the player moves a bucket to collect beneficial falling drops while avoiding harmful ones. The project uses Java 11, LibGDX 1.12.1 for game and multimedia support, and Gradle 8.10.1 for builds and dependency management.

# Requirements and setup

The required development tools are:

- JDK 11
- Git for cloning the repository
- An internet connection for the first dependency download

Gradle itself does not need to be installed. The included Gradle Wrapper downloads and runs the project's configured Gradle version, and Gradle resolves the LibGDX dependencies declared by the project.

IntelliJ IDEA, Eclipse, and Visual Studio Code with Java development extensions can be used for development, but they are optional. Configure the selected IDE to use JDK 11 and import the project as a Gradle project.

# Project structure

- `assets/` contains the textures, sound effects, music, and generated asset list used at runtime.
- `core/` contains platform-independent application, gameplay, model, and screen code.
- `lwjgl3/` contains the desktop launcher and LWJGL3-specific configuration.
- `gradle/` and the `gradlew` scripts provide the Gradle Wrapper.

# Core code organization

The code under `core/src/main/java/puppy/code/` is organized as follows:

`GameLluviaMenu` initializes the application, owns the shared assets, initializes the application-scoped leaderboard, and coordinates transitions among the main menu, gameplay, pause, game-over, and leaderboard screens. `GameAssets` loads each shared graphical or audio resource once, including the small texture used to style Scene2D buttons, and disposes of those resources when the application closes. It is also the single presentation mapping from domain values to resources: `getDropTexture(DropAppearance)` looks up the texture registered for each kind of drop, and `playEventSound(GameplayEvent)` uses an `EnumMap` checked for completeness at startup, so screens contain no drop-type or event conditionals.

The `screen` package handles rendering, navigation, and audio responses. `BaseUiScreen` is the Template Method parent for `MainMenuScreen`, `LeaderboardScreen`, `GameOverScreen`, and `OptionsScreen`. It owns each screen's Scene2D stage and fixed-world `FitViewport`, and fixes the shared show, render, resize, hide, and dispose workflow. Building the interface is also a fixed sequence inside the template: on first use it creates the widget kit and a centred, fill-parent root table, adds the screen's title, and places a content table below it. Concrete screens implement only the two variable steps: `getTitle()` and `buildContent(Table content, ScreenUi ui)` for their labels, buttons, saved-score content, and callbacks. `ScreenUi` is a small per-screen widget kit that creates the shared label and button styles once and builds styled labels, titles, buttons bound to a `Runnable` action, and standard menu-button rows, so screens no longer repeat style setup or click-listener boilerplate. UI construction occurs on first show, after the concrete constructor has finished. `GameScreen` passes the movement reported by its `PlayerInput` to the gameplay layer and draws the session's state; it no longer reads the keyboard directly. It registers `GameAssets::playEventSound` as the session's listener, draws each item through a callback created once, and takes the sky colour and weather label from per-weather `EnumMap` tables instead of conditionals. The gameplay HUD shows the current score and lives; persistent scores are presented only by the leaderboard screen.

The `score` package contains the local leaderboard. `HighScoreManager` is a controlled, application-scoped Singleton that ranks at most five scores and exposes them by position through `getScoreCount()` and `getScore(int)`, never as a collection. It depends on the small `ScoreStore` persistence boundary; `PreferencesScoreStore` is the production adapter backed by LibGDX Preferences. If a save fails, the manager retains the valid in-memory ranking, marks it as pending, and permits an idempotent retry of the complete serialized ranking without interrupting game-over navigation. This keeps ranking, malformed-data handling, duplicate-completion protection, and persistence failures testable without graphics or a LibGDX application.

The `input` package separates player controls from the screens. `PlayerInput` is a small interface that reports whether the player is moving left or right, without depending on LibGDX or on specific keys. `KeyboardPlayerInput` implements it over an injected LibGDX `Input`; each direction accepts several keys, and `KeyboardPlayerInput.standard(Input)` binds the arrow keys and the A/D keys at the same time. `GameLluviaMenu` creates the standard keyboard input when a game starts and hands it to `GameScreen`. Controls are therefore: left arrow or A to move left, and right arrow or D to move right; pressing both directions at once cancels the movement. Other schemes, such as a gamepad, can be added as new `PlayerInput` implementations without changing `GameScreen`.

The `gameplay` package contains the active-session rules. `GameSession` coordinates the player, falling items, collisions, score, lives, weather, and game-over state, and never hands out its collections. Each outcome is pushed to a `GameplayListener` (Observer) supplied at construction, and `forEachFallingItem(FallingItemConsumer)` gives presentation only each active item's appearance and position, so mutable `FallingItem` references never leave the session. The `Weather` enum (`CALM`, `STORM`) defines the score at which each phase starts and computes the current phase from the score. The session keeps one rain source per phase in an `EnumMap`; once the score reaches 300, new drops come from the storm source for the rest of the game, and the drops already falling change strategy at runtime to their intensified effect.

Rain configurations use the Builder pattern in its classic form. `RainProfileBuilder` (Builder) declares the construction steps: spawn interval, beneficial drops, and harmful drops. `CalmRainBuilder` (100 ms, 60/40, default 10-point drops) and `StormRainBuilder` (75 ms, 60/40, beneficial drops created with a 20-point `ScoreEffect`) are the ConcreteBuilders, and `RainDirector` (Director) fixes the order of the steps, which also fixes which rolls select each drop. The product, `DropSpawnProfile`, is immutable and is assembled internally with its validating fluent `Builder`; it also performs the weighted selection itself through `createDrop(roll, x, y)`, so its registrations are never exposed. `DropSpawner` consumes a profile and owns only runtime timing, randomness, and spawn positions. Neither the builders, profile, spawner, nor session depends on rendering, input, audio, or screens.

The `model` package contains the game entities and their state. `Player` owns movement, boundaries, score, lives, and hurt state; positive score awards saturate at `Integer.MAX_VALUE` instead of overflowing. `FallingItem` defines shared falling behavior and is the context of the `DropEffect` Strategy: `applyTo(Player)` delegates to the current effect and returns the single `GameplayEvent` it produced, and `setEffect(DropEffect)` replaces the strategy at runtime. `intensify()` swaps in the effect's own `intensified()` version, a default method that returns the same effect except in `ScoreEffect`, which doubles its points. `GoodDrop` and `BadDrop` choose or receive strategies and each declares its own `DropAppearance` constant, so the parent class does not list its subclasses. Motion is the variation that belongs to the subclasses: `FallingItem.fall()` is a final template that applies the shared fall speed, asks the protected `horizontalDrift` hook for sideways movement, and keeps the item inside the world. `GoodDrop` keeps the default straight fall, while `BadDrop` sways along a sine wave around its spawn column. Inheritance therefore models how an item moves, and composition (Strategy) models what happens when it is caught.

# Architectural conventions

State is private and changed through small, intention-revealing methods. No public method returns a collection: objects that own a collection perform the work themselves (weighted selection, ranking lookups) or hand out its elements through callbacks and listeners, so screens never obtain mutable session-owned entities. Built spawn profiles defensively copy their registrations and never expose them. Responsibilities are separated so that screens present state, gameplay classes apply rules, models protect entity state, and the application owns shared resources and navigation.

Dependencies point from the application and screens toward gameplay, scores, and models; models do not know about screens or LibGDX rendering and audio resources. Score rules depend on `ScoreStore`, not directly on LibGDX. Shared disposable resources have one owner, `GameAssets`, and are never disposed by individual screens. `BaseUiScreen` owns and disposes the stage created for each menu-style screen while using, but not owning, the shared batch, font, and UI texture. It also registers its stage as the input processor only while active and updates the viewport with centered camera coordinates after a resize.

The implementation applies SOLID and GRASP principles in proportion to the size of the game: classes have focused responsibilities, gameplay rules remain cohesive, and screen coupling to model internals is limited. New control schemes belong in `input`, new presentation code in `screen`, session rules in `gameplay`, entity behavior in `model`, and shared asset lifecycle changes in `GameAssets`.

Java names follow standard conventions: classes use PascalCase, methods and fields use camelCase, and constants use UPPER_SNAKE_CASE.
