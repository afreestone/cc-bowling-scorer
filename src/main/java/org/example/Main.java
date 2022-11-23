package org.example;

import org.example.exception.InvalidInput;
import org.example.model.Game;

public class Main {
    public static void main(String[] args) throws InvalidInput {
        var input = String.join(" ", args);
        System.out.printf("Scoring a bowling game with input: %s%n", input);
        var game = Game.fromString(input);
        System.out.printf("Games final score was %d%n", game.getGameScore());
    }
}