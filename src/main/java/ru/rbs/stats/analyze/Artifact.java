package ru.rbs.stats.analyze;

public class Artifact {
    long timestamp;
    String description;

    public Artifact(long timestamp, String description) {
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
