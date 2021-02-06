package com.palmeiro;

public enum UserType {
    PAOLA(44673),
    TEO(198825),
    LEO(198824);
    private final int value;

    UserType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
