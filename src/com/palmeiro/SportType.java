package com.palmeiro;

public enum SportType {
    SWIMMING(26),
    POOL(19);

    private final int value;

    SportType(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
