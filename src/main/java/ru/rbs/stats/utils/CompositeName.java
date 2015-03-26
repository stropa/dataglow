package ru.rbs.stats.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CompositeName {

    public static final String DEFAULT_DELIMITER = ".";
    List<String> parts = new ArrayList<String>();
    private String delimiter = DEFAULT_DELIMITER;

    public static CompositeName fromParts(String... parts) {
        CompositeName instance = new CompositeName();
        if (parts != null) {
            Collections.addAll(instance.parts, parts);
        }
        return instance;
    }

    public CompositeName withPart(String part) {
        this.parts.add(part);
        return this;
    }

    public List<String> getParts() {
        return parts;
    }

    public String getLastPart() {
        return parts.isEmpty() ? null : parts.get(parts.size() - 1);
    }

    public String format() {
        return format(DEFAULT_DELIMITER);
    }

    public String format(String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator = parts.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositeName that = (CompositeName) o;

        if (!delimiter.equals(that.delimiter)) return false;
        if (!parts.equals(that.parts)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parts.hashCode();
        result = 31 * result + delimiter.hashCode();
        return result;
    }
}
