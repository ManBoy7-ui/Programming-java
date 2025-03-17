package com.organizationmanager;

public class Coordinates {
    private float x; // Max value: 84
    private Integer y; // Max value: 239, cannot be null

    public Coordinates(float x, Integer y) {
        this.x = x;
        this.y = y;
    }

    // Getters
    public float getX() { return x; }
    public Integer getY() { return y; }
}