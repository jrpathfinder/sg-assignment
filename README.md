# Scratch Game

Scratch Game where players can place bets, generate symbol matrices, and win rewards based on predefined rules and combinations from config.json provided file.

## Features
- Generate a random game matrix based on probabilities.
- Support for standard and bonus symbols.
- Apply winning combinations and calculate rewards.
- Support for bonus multipliers and extra bonuses.

## Prerequisites
To build and run the application, you need:
- JDK 11 or higher
- Maven 3.6+ or Gradle
- Git (for version control)

## Build Instructions
### Using Maven
1. Clone the repository:
   ```bash
   git clone [https://github.com/<your-username>/<repository-name>](https://github.com/jrpathfinder/sg-assignment.git
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```
   This will generate the executable JAR file in the `target/` directory.
   

## Run Instructions
### Run the Application
To run the application, use the following command:

```bash
java -jar target/<jar-file-name>.jar --config <path-to-config.json> --betting-amount <amount>
```

- Replace `<jar-file-name>` with the name of the generated JAR file (e.g., `scratch-game-1.0.0.jar`).
- Replace `<path-to-config.json>` with the path to your configuration file.
- Replace `<amount>` with the betting amount (e.g., `100`).

### Example
```bash
java -jar target/scratch-game-1.0.0.jar --config ./config.json --betting-amount 100
```

## Configuration File
The application relies on a `config.json` file to define game parameters, including symbols, probabilities, and winning combinations. Below is an example:

```json
{
  "columns": 3,
  "rows": 3,
  "symbols": {
    "A": {
      "reward_multiplier": 5,
      "type": "standard"
    },
    "10x": {
      "reward_multiplier": 10,
      "type": "bonus",
      "impact": "multiply_reward"
    }
  },
  "probabilities": {
    "standard_symbols": [
      {
        "column": 0,
        "row": 0,
        "symbols": {
          "A": 5,
          "B": 3
        }
      }
    ],
    "bonus_symbols": {
      "symbols": {
        "10x": 1
      }
    }
  },
  "win_combinations": {
    "same_symbol_3_times": {
      "reward_multiplier": 2,
      "when": "same_symbols",
      "count": 3,
      "group": "same_symbols"
    }
  }
}
```

## Tests
The project includes unit tests to validate the functionality.

### Run Tests
```bash
mvn test
```
