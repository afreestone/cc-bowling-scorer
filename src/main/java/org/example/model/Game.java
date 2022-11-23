package org.example.model;

import org.example.exception.InvalidInput;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Game {
    private static final Pattern VALID_INPUT_PATTERN = Pattern.compile("^[0-9xX\\\\/ -]+$");

    private final List<Frame> frames;

    public Game(List<Frame> frames) {
        this.frames = frames;
    }

    public int getGameScore() {
        // So, we have two special cases here:
        // 1. STRIKE - 10 + n+1 + n+2
        // 2. SPARE - 10 + n+1 (first ball only)
        // However they only get the base score for those future frames, not any bonuses that would apply.
        var score = 0;
        // The example input has 12 "frames" in the case of all strikes,
        // but in reality there are only 10. The last two balls are in the last frame
        // Input should probably be `X X X X X X X X X XXX` if spaces are to be interpreted as frame breaks.
        for (int i = 0; i < 10; i++) {
            var current = frames.get(i);
            switch (current.getSpecialEvent()) {
                case STRIKE:
                    // Check bounds
                    if (i + 2 < frames.size()) {
                        score += frames.get(i + 2).getFrameScore();
                    }
                    if (i + 1 < frames.size()) {
                        score += frames.get(i + 1).getFrameScore();
                    }
                    score += current.getFrameScore();
                    // We can't share the +1 logic with Spare since spare only gets the first ball of the next frame.
                    break;
                case SPARE:
                    // Check bounds
                    // We only add the _first_ try from the next frame.
                    if (i + 1 < frames.size()) {
                        score += frames.get(i + 1).getSpareScore();
                    }
                default:
                    score += current.getFrameScore();
            }
        }
        return score;
    }

    public static Game fromString(String score) throws InvalidInput {
        validateInput(score);
        var rawFrames = score.split(" ");
        // Validate?
        if (rawFrames.length < 10 || rawFrames.length > 12) {
            throw new InvalidInput("Unexpected number of frames: %d. Should be 10 (or up to 12 in the case of a final strikes)".formatted(rawFrames.length));
        } else if (rawFrames.length == 11) {
            // This should only occur in the case of the last frame containing a strike.
            if (!rawFrames[9].trim().equalsIgnoreCase("x") && !rawFrames[10].trim().equalsIgnoreCase("x")) {
                throw new InvalidInput("Number of 'frames' should only be >10 in the case of strikes in the last frame.");
            }
        }
        var frames = new ArrayList<Frame>(rawFrames.length);
        for (String f : rawFrames) {
            frames.add(Frame.fromString(f));
        }
        return new Game(frames);
    }

    private static void validateInput(String rawInput) throws InvalidInput {
        if (!VALID_INPUT_PATTERN.matcher(rawInput).find()) {
            throw new InvalidInput("Invalid character(s) in input. Expected 0-9, X, / - and space.");
        }
    }
}
