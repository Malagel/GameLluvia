# GameLluviaMenu2024

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

## Development Setup

### Requirements

- Java Development Kit (JDK) 11
- Git
- Internet connection for the initial dependency download

The game uses **LibGDX 1.12.1** and **Gradle 8.10.1**.

Gradle is managed through the included Gradle Wrapper, so a global Gradle installation is not required.

### 1. Verify Java

Open a terminal and run:

```bash
java -version
javac -version
```

Both commands should report Java 11.

### 2. Clone the repository

```bash
git clone <repository-url>
cd GameLluviaMenu2024
```

Replace `<repository-url>` with the actual GitHub repository URL.

### 3. Run the game

**Linux/macOS:**

```bash
chmod +x gradlew
./gradlew lwjgl3:run
```

**Windows (PowerShell):**

```powershell
.\gradlew.bat lwjgl3:run
```

The first execution may take longer because Gradle needs to download project dependencies.

### 4. Clean generated build files (optional)

```bash
./gradlew clean
```

This removes generated build outputs without deleting the source code or game assets.

### Troubleshooting

If Gradle uses an incorrect Java version, verify that the `JAVA_HOME` environment variable points to a JDK 11 installation.

If the game cannot launch, check the error message and ensure that the machine supports OpenGL, as required by LibGDX's desktop backend.