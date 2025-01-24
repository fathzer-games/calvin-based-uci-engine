[![License](https://img.shields.io/badge/license-Apache%202.0-brightgreen.svg)](https://github.com/fathzer-games/chesslib-uci-engine/blob/master/LICENSE)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fathzer-games_calvin-based-uci-engine&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fathzer-games_calvin-based-uci-engine)

# calvin-based-uci-engine
A chess [UCI](https://en.wikipedia.org/wiki/Universal_Chess_Interface) engine based on:
- chess-utils and [calvin](https://github.com/kelseyde/calvin-chess-engine)'s move generator.
- The [games-core](https://github.com/fathzer-games/games-core) alpha beta search algorithm implementation.
- The evaluation functions, the remaining move count oracle and some other things from the [chess-utils](https://github.com/fathzer-games/chess-utils) library.

## How to run the engine
It requires a Java 17+ virtual machine.

Download the jar [here](https://fathzer-games.github.io/calvin-based-uci-engine/calvin-based-uci-engine.jar), then launch the engine with the following command: ```java -jar calvin-based-uci-engine.jar```

### Openings library
You can use an opening library located at a URL using the `openingsUrl` system property.  
Currently such a library is available [here](https://fathzer-games.github.io/calvin-based-uci-engine/masters-shrink.json.gz).

To use this library, launch the engine with:  
```java -DopeningsUrl=https://fathzer-games.github.io/calvin-based-uci-engine/masters-shrink.json.gz -jar calvin-based-uci-engine.jar```
