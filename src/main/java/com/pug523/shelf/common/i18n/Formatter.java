package com.pug523.shelf.common.i18n;

public final class Formatter {
    private Formatter() {
    }

    /// Formats a string by replacing `{}` placeholders with the provided arguments.
    ///
    /// @param template The template string (e.g., "Hello {}")
    /// @param args     The arguments to inject
    /// @return The formatted string
    public static String format(String template, Object... args) {
        if (template == null || template.isEmpty()) return "";
        if (args == null || args.length == 0 || template.indexOf('{') == -1) return template;

        StringBuilder builder = new StringBuilder(template.length() + 32); // Pre-allocate
        int argIndex = 0;
        int length = template.length();

        for (int i = 0; i < length; i++) {
            char c = template.charAt(i);
            if (c == '{' && i + 1 < length && template.charAt(i + 1) == '}') {
                builder.append(argIndex < args.length ? args[argIndex++] : "{}");
                i++; // Skip the closing '}'
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }
}
