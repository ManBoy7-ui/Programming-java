package com.organizationmanager;

public class Coordinates {
    private final Float x; // Nullable, max value: 84
    private final int y; // Not null, max value: 239

    public Coordinates(Float x, int y) {
        if (x != null && x > 84) throw new IllegalArgumentException("x must be ≤ 84");
        if (y > 239) throw new IllegalArgumentException("y must be ≤ 239");
        this.x = x;
        this.y = y;
    }

    public Float getX() { return x; }
    public int getY() { return y; }

    @Override
    public String toString() {
        return String.format("Coordinates[x=%s, y=%d]", x, y);
    }
}
