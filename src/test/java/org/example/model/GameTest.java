package org.example.model;

import org.example.exception.InvalidInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @ParameterizedTest
    @MethodSource("scoreSamples")
    void shouldScoreCorrectly(String gameString, int expectedScore) throws InvalidInput {
        final var candidate = Game.fromString(gameString);
        assertEquals(expectedScore, candidate.getGameScore(), "Scores do not match");
    }

    private static Stream<Arguments> scoreSamples() {
        return Stream.of(
                Arguments.of("X X X X X X X X X X X X", 300),
                Arguments.of("9- 9- 9- 9- 9- 9- 9- 9- 9- 9-", 90),
                Arguments.of("5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/5", 150)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidInputs")
    void shouldHandleInvalidInput(String gameString, String expectedMessage) {
        Exception exception = assertThrows(InvalidInput.class, () -> Game.fromString(gameString));

        var actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "%s does not contain %s".formatted(actualMessage, expectedMessage));
    }

    private static Stream<Arguments> invalidInputs() {
        return Stream.of(
                Arguments.of("X X X X X X X X X X X X X", "Unexpected number of frames: 13"),
                Arguments.of("X X X X X X X X", "Unexpected number of frames: 8"),
                Arguments.of("9- 9- 9- 9- 9- 9- 9- 9- 9- 9- 6", "Number of 'frames' should only be >10 in the case of strikes in the last frame."),
                Arguments.of("a 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/ 5/5", "Invalid character(s) in input.")
        );
    }
}