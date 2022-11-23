package org.example.model;

import org.example.exception.InvalidInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Frame {
    private static final Pattern VALID_INPUT_PATTERN = Pattern.compile("^[x0-9\\-][0-9\\-/]?[x0-9\\-]?$");

    private final List<Score> tries;

    public Frame(List<Score> tries) {
        this.tries = tries;
    }

    public int getFrameScore() {
        return tries.stream().mapToInt(Score::value).sum();
    }

    public int getSpareScore() {
        return tries.get(0).value();
    }

    public ScoreEvent getSpecialEvent() {
        var specialEvents = Set.of(ScoreEvent.STRIKE, ScoreEvent.SPARE);
        var result = tries.stream().filter(t -> specialEvents.contains(t.event())).findFirst();
        if (result.isPresent()) {
            // Something special happened.
            return result.get().event();
        } else {
            // Nothing special happened.
            return ScoreEvent.SCORE;
        }
    }

    public static Frame fromString(String rawFrame) throws InvalidInput {
        // Assume input is in the form of
        // X - Strike
        // 12 - total of 3, 1 pin then 2 pins on second try.
        // 5/ - 5 with a spare on the second try.
        validateInput(rawFrame);
        final var chars = rawFrame.toLowerCase().toCharArray();
        final var parsedTries = new ArrayList<Score>(chars.length);
        var lastPoint = 0;
        var strikeFrame = false;
        for (char c : chars) {
            switch (c) {
                case 'x' -> {
                    parsedTries.add(new Score(ScoreEvent.STRIKE, 10));
                    strikeFrame = true;
                }
                case '/' -> parsedTries.add(new Score(ScoreEvent.SPARE, 10 - lastPoint));
                case '-' -> parsedTries.add(new Score(ScoreEvent.MISS, 0));
                default -> {
                    if (!strikeFrame) {
                        lastPoint = Character.getNumericValue(c);
                        parsedTries.add(new Score(ScoreEvent.SCORE, lastPoint));
                    }
                }
            }
        }
        var parsedFrame = new Frame(parsedTries);
        if (parsedFrame.getFrameScore() > 10 && parsedFrame.getSpecialEvent() == ScoreEvent.SCORE) {
            // Only valid if a strike or spare occured.
            throw new InvalidInput("Maximum raw score for a frame without a strike or spare is 10. Parsed score is %d".formatted(parsedFrame.getFrameScore()));
        }
        return parsedFrame;
    }

    private static void validateInput(String rawInput) throws InvalidInput {
        // Only one strike is allowed in the frame.
        String comparable = rawInput.trim().toLowerCase();
        if (comparable.chars().filter(c -> c == 'x').count() > 1) {
            throw new InvalidInput("Cannot have multiple strikes in a single frame.");
        }
        if (!VALID_INPUT_PATTERN.matcher(comparable).find()) {
            throw new InvalidInput("Invalid character(s) in input. Frame should be at most 3 values, and only when there is a spare in the final frame.");
        }
        if (comparable.length() >= 3 && comparable.charAt(1) != '/') {
            // Dealing with a valid string from the regex, but invalid for other reasons.
            throw new InvalidInput("Frame can only contain 3 values when it is the last frame with a spare");
        }
    }
}
