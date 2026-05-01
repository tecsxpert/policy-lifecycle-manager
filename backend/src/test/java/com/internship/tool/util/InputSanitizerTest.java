package com.internship.tool.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputSanitizerTest {

    @Test
    void testSanitize_removesHtmlTags() {
        assertThat(InputSanitizer.sanitize("<script>alert(1)</script>")).isEqualTo("scriptalert(1)script");
    }

    @Test
    void testSanitize_removesQuotesAndAmpersand() {
        assertThat(InputSanitizer.sanitize("Tom & Jerry \"The Movie\"")).isEqualTo("Tom  Jerry The Movie");
    }

    @Test
    void testSanitize_trimsWhitespace() {
        assertThat(InputSanitizer.sanitize("  hello world  ")).isEqualTo("hello world");
    }

    @Test
    void testSanitize_nullReturnsNull() {
        assertThat(InputSanitizer.sanitize(null)).isNull();
    }

    @Test
    void testSanitize_emptyStringReturnsEmpty() {
        assertThat(InputSanitizer.sanitize("")).isEmpty();
    }

    @Test
    void testTrim_trimsOnly() {
        assertThat(InputSanitizer.trim("  password123!@#  ")).isEqualTo("password123!@#");
    }

    @Test
    void testTrim_nullReturnsNull() {
        assertThat(InputSanitizer.trim(null)).isNull();
    }
}
