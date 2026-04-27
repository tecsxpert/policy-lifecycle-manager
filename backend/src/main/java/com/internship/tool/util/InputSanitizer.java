package com.internship.tool.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility for sanitizing user-provided input strings.
 * Trims whitespace and strips potentially dangerous HTML/script characters
 * to prevent XSS and injection attacks.
 */
@Component
public class InputSanitizer {

    private static final Pattern XSS_PATTERN = Pattern.compile("[<>&\"'/]");

    /**
     * Trims leading/trailing whitespace and replaces sequences of
     * HTML/script metacharacters with an empty string.
     *
     * @param input raw user input
     * @return sanitized string, or null if input was null
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        return XSS_PATTERN.matcher(trimmed).replaceAll("");
    }

    /**
     * Trims whitespace only. Use for fields where special characters
     * are legitimately required (e.g. passwords).
     *
     * @param input raw user input
     * @return trimmed string, or null if input was null
     */
    public static String trim(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }
}

