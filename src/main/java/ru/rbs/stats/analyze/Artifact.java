package ru.rbs.stats.analyze;

import java.time.LocalDateTime;

public class Artifact {
    LocalDateTime timestamp;
    String description;

    public Artifact(LocalDateTime timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "timestamp=" + timestamp +
                ", description='" + description + '\'' +
                '}';
    }
}
