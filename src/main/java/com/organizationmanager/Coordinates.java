package com.organizationmanager;

/**
 * @param x
 *            Nullable, max value: 84
 * @param y
 *            Not null, max value: 239
 */
public record Coordinates(Float x, int y) {
    public Coordinates {
        if (x != null && x > 84)
            throw new IllegalArgumentException("x must be ≤ 84");
        if (y > 239)
            throw new IllegalArgumentException("y must be ≤ 239");
    }

    @Override
    public String toString() {
        return String.format("Coordinates[x=%s, y=%d]", x, y);
    }
}