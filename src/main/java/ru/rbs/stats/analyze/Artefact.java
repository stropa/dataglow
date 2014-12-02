package ru.rbs.stats.analyze;

public class Artefact {
    long timestamp;
    String description;

    public Artefact(long timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Artefact{" +
                "timestamp=" + timestamp +
                ", description='" + description + '\'' +
                '}';
    }
}
