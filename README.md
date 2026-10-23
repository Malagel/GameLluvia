# GameLluvia

GameLluvia es un juego de escritorio en el que el jugador mueve un balde para recoger las gotas beneficiosas que caen mientras evita las perjudiciales. El objetivo es conseguir la mayor cantidad de puntos posible antes de perder las tres vidas.

El proyecto utiliza Java 11, LibGDX y Gradle. Sus módulos principales son:

- `core`: contiene la lógica del juego, los modelos, la persistencia y el código de las pantallas, independientemente de la plataforma.
- `lwjgl3`: contiene el lanzador de escritorio LWJGL3 y la configuración específica de la plataforma.

## Requisitos

- JDK 11
- Conexión a Internet para la descarga inicial de dependencias.
- Soporte gráfico de escritorio compatible con LibGDX/LWJGL3.

Gradle se proporciona mediante el *wrapper* incluido en el proyecto, por lo que no es necesario instalarlo por separado.

## Ejecutar el juego

En Linux o macOS:

```bash
cd GameLluvia
./gradlew lwjgl3:run
```

En Windows:

```powershell
cd GameLluvia
.\gradlew.bat lwjgl3:run
```

## Ejecutar las pruebas

```bash
./gradlew test
```