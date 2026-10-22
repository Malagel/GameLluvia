# GameLluvia

GameLluvia is a desktop game in which the player moves a bucket to collect
beneficial falling drops while avoiding harmful ones. The objective is to earn as
many points as possible before losing all three lives.

The project uses Java 11, LibGDX, and Gradle. Its main modules are:

- `core`: platform-independent gameplay, model, persistence, and screen code.
- `lwjgl3`: the LWJGL3 desktop launcher and platform configuration.

## Requirements

- JDK 11
- An internet connection for the initial dependency download
- Desktop graphics support compatible with LibGDX/LWJGL3

Gradle is provided through the included wrapper, so a separate installation is
not required.

## Run the game

On Linux or macOS:

```bash
./gradlew lwjgl3:run
```

On Windows:

```powershell
.\gradlew.bat lwjgl3:run
```

## Run the tests

```bash
./gradlew test
```

See [docs.md](docs.md) for architecture and setup details, and
[project-requirements.md](project-requirements.md) for the academic GM requirement
tracking.
