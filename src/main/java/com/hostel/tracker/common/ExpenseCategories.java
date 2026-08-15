package com.hostel.tracker.common;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ExpenseCategories {

    public static final List<String> BUILTINS = List.of(
            "Electricity",
            "Water",
            "Gas",
            "Internet",
            "Repairs",
            "Cleaning",
            "Supplies",
            "Other"
    );

    private static final Set<String> BUILTIN_LOWER = Set.copyOf(
            BUILTINS.stream().map(name -> name.toLowerCase(Locale.ROOT)).toList()
    );

    private ExpenseCategories() {
    }

    public static String normalize(String value) {
        String raw = value == null ? "" : value.trim().replaceAll("\\s+", " ");
        if (raw.isEmpty()) {
            return "Other";
        }
        for (String builtin : BUILTINS) {
            if (builtin.equalsIgnoreCase(raw)) {
                return builtin;
            }
        }
        return raw;
    }

    public static boolean isBuiltin(String name) {
        return name != null && BUILTIN_LOWER.contains(name.toLowerCase(Locale.ROOT));
    }
}
