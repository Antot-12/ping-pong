# Ping Pong Game

![Ping Pong Game Menu](./menu.png)

## Overview

Ping Pong Game is a simple multiplayer game implemented in Java. You can play either against another player or against an AI with different difficulty levels.

## Features

- **Two Player Mode**: Play against another player.
- **Single Player Mode**: Play against AI with easy and hard difficulty levels.
- **Score Limit**: The game ends when a player reaches 10 points.
- **Smooth Graphics**: Smooth paddle and ball movement.
- **User-Friendly Interface**: Easy to navigate and play.

## How to Play

### Two Player Mode

1. **Start the Server**:
    - The server is automatically started when you select the two-player mode.
    - Both clients will connect to the server and start the game.

2. **Control the Paddles**:
    - **Player 1**: Use the `UP` and `DOWN` arrow keys to move the paddle.
    - **Player 2**: Use the `W` and `S` keys to move the paddle.

### Single Player Mode

1. **Choose Difficulty**:
    - **Easy AI**: Select "1 Player vs Easy AI".
    - **Hard AI**: Select "1 Player vs Hard AI".

2. **Control the Paddle**:
    - Use the `UP` and `DOWN` arrow keys to move the paddle.

### Winning the Game

- The first player to reach 10 points wins the game.
- A prompt will appear asking if you want to play again. Select "Yes" to restart the game or "No" to exit.

## Controls

- **Player 1**: Use the `UP` and `DOWN` arrow keys to move the paddle.
- **Player 2**: Use the `W` and `S` keys to move the paddle in two-player mode.

## Setup

1. **Clone the Repository**:
    ```sh
    git clone https://github.com/Antot-12/ping-pong.git
    cd pingponggame
    ```

2. **Compile the Java Files**:
    ```sh
    ./gradlew build
    ```

3. **Run the Game**:
    ```sh
    ./gradlew run
    ```

## Project Structure

- **src/main/java/Antot_12**: Contains the Java source files.
- **images**: Contains images used in the README.
- **build.gradle**: The Gradle build file.

## Screenshots

### Main Menu

![Main Menu](./menu.png)

### Two Player Mode

![Two Player Mode](./2_pl.png)

### Single Player Mode (AI)

![Single Player Mode (AI)](./AI.png)

## Authors

- **Antot_12** - *Initial work* - [My GitHub](https://github.com/Antot-12)

