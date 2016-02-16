package org.dataglow.v1.analyze;

import org.dataglow.v1.store.CubeCoordinates;

import java.time.LocalDateTime;

public class Artifact {
    CubeCoordinates coordinates;
    LocalDateTime timestamp;
    String name;
    String description;

    public Artifact(LocalDateTime timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }

    public CubeCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CubeCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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
