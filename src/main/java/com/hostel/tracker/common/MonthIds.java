package com.hostel.tracker.common;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;

public final class MonthIds {

    private static final DateTimeFormatter ID_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private MonthIds() {
    }

    public static String current() {
        return YearMonth.now().format(ID_FORMAT);
    }

    public static String of(int year, int month) {
        return YearMonth.of(year, month).format(ID_FORMAT);
    }

    public static YearMonth parse(String monthId) {
        try {
            return YearMonth.parse(monthId, ID_FORMAT);
        } catch (DateTimeParseException ex) {
            throw ApiException.badRequest("monthId must be YYYY-MM");
        }
    }

    public static String label(YearMonth month) {
        String name = month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return name + " " + month.getYear();
    }

    public static String fromDate(LocalDate date) {
        return YearMonth.from(date).format(ID_FORMAT);
    }
}
