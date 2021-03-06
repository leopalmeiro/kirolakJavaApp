package com.palmeiro;

public enum CenterType {
    MIRIBILLA(14),
    ATXURI(11);


    private final int value;

    CenterType(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
