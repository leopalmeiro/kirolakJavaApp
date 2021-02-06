package com.palmeiro;

public enum SportType {
    SWIMMING(19),
    POOL(26);

    private final int value;

    SportType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
