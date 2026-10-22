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

`GameLluviaMenu` initializes the application, owns the shared assets, initializes the application-scoped leaderboard, and coordinates transitions among the main menu, gameplay, pause, game-over, and leaderboard screens. `GameAssets` loads each shared graphical or audio resource once, including the small texture used to style Scene2D buttons, and disposes of those resources when the application closes.

The `screen` package handles rendering, user input, navigation, and audio responses. `BaseUiScreen` is the Template Method parent for `MainMenuScreen`, `LeaderboardScreen`, and `GameOverScreen`. It owns each screen's Scene2D stage and fixed-world `FitViewport`, and fixes the shared show, render, resize, hide, and dispose workflow. Concrete screens implement only the `buildUi(Stage, GameAssets)` hook for their labels, buttons, saved-score content, and callbacks. UI construction occurs on first show, after the concrete constructor has finished. `GameScreen` translates keyboard input into gameplay updates and draws the state returned by the gameplay layer. The gameplay HUD shows the current score and lives; persistent scores are presented only by the leaderboard screen.

The `score` package contains the local leaderboard. `HighScoreManager` is a controlled, application-scoped Singleton that ranks and exposes at most five scores. It depends on the small `ScoreStore` persistence boundary; `PreferencesScoreStore` is the production adapter backed by LibGDX Preferences. This keeps ranking, malformed-data handling, and duplicate-completion protection testable without graphics or a LibGDX application.

The `gameplay` package contains the active-session rules. `GameSession` coordinates the player, falling items, collisions, score, lives, and game-over state. `DropSpawnProfile` is an immutable spawning configuration built incrementally with its nested `Builder`; it stores the interval and weighted drop factories. `DropSpawner` consumes a profile and owns runtime timing, weighted selection, and initial positions. Its no-argument constructor builds the standard 100 ms, 60% good, and 40% bad profile, while its profile constructor permits another validated configuration without changing spawning logic. Neither the profile, spawner, nor session depends on rendering, input, audio, or screens.

The `model` package contains the game entities and their state. `Player` owns movement, boundaries, score, lives, and hurt state. `FallingItem` defines shared falling behavior, while `GoodDrop` and `BadDrop` implement their respective collision effects.

# Architectural conventions

State is private and changed through small, intention-revealing methods. Read-only collection views prevent screens from modifying session-owned collections, and built spawn profiles defensively copy and expose an unmodifiable registration list. Responsibilities are separated so that screens present state, gameplay classes apply rules, models protect entity state, and the application owns shared resources and navigation.

Dependencies point from the application and screens toward gameplay, scores, and models; models do not know about screens or LibGDX rendering and audio resources. Score rules depend on `ScoreStore`, not directly on LibGDX. Shared disposable resources have one owner, `GameAssets`, and are never disposed by individual screens. `BaseUiScreen` owns and disposes the stage created for each menu-style screen while using, but not owning, the shared batch, font, and UI texture. It also registers its stage as the input processor only while active and updates the viewport with centered camera coordinates after a resize.

The implementation applies SOLID and GRASP principles in proportion to the size of the game: classes have focused responsibilities, gameplay rules remain cohesive, and screen coupling to model internals is limited. New presentation code belongs in `screen`, session rules in `gameplay`, entity behavior in `model`, and shared asset lifecycle changes in `GameAssets`.

Java names follow standard conventions: classes use PascalCase, methods and fields use camelCase, and constants use UPPER_SNAKE_CASE.
