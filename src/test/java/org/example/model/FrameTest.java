package org.example.model;

import org.example.exception.InvalidInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FrameTest {

    @ParameterizedTest
    @MethodSource("scoreSamples")
    void shouldScoreFramesCorrectly(String frameString, int expectedScore) throws InvalidInput {
        final var candidate = Frame.fromString(frameString);
        assertEquals(expectedScore, candidate.getFrameScore(), "Scores do not match");
    }

    private static Stream<Arguments> scoreSamples() {
        return Stream.of(
                Arguments.of("X", 10),
                Arguments.of("9-", 9),
                Arguments.of("5/", 10),
                Arguments.of("5/5", 15),
                Arguments.of("5/X", 20),
                Arguments.of("51", 6)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidInputs")
    void shouldHandleInvalidInput(String frameString, String expectedMessage) {
        Exception exception = assertThrows(InvalidInput.class, () -> Frame.fromString(frameString));

        var actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "%s does not contain %s".formatted(actualMessage, expectedMessage));
    }

    private static Stream<Arguments> invalidInputs() {
        return Stream.of(
                Arguments.of("XX", "Cannot have multiple strikes in a single frame"),
                Arguments.of("65", "Parsed score is 11"),
                Arguments.of("111", "Frame can only contain 3 values when it is the last frame with a spare"),
                Arguments.of("1/11", "Invalid character(s) in input. Frame should be at most 3 values, and only when there is a spare in the final frame"),
                Arguments.of("11/", "Invalid character(s) in input. Frame should be at most 3 values, and only when there is a spare in the final frame")
        );
    }
}