package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {
    private static final HabrCareerDateTimeParser PARSER = new HabrCareerDateTimeParser();
    @Test
    void testDate1() {
        assertThat(PARSER.parse("2023-10-20T12:27:11+03:00"))
                .isEqualTo(LocalDateTime.of(2023, Month.OCTOBER, 20, 12, 27, 11));
    }

    @Test
    void testDate2() {
        assertThat(PARSER.parse("2023-10-27T14:27:21+03:00"))
                .isEqualTo(LocalDateTime.of(2023, Month.OCTOBER, 27, 14, 27, 21));
    }

    @Test
    void testDate3() {
        assertThat(PARSER.parse("2023-12-31T23:59:59+03:00"))
                .isEqualTo(LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59, 59));
    }

    @Test
    void testDate4() {
        assertThat(PARSER.parse("2023-01-01T00:00:00+03:00"))
                .isEqualTo(LocalDateTime.of(2023, Month.JANUARY, 1, 0, 0, 0));
    }
}