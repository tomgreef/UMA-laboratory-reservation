package com.reserve.lab.api.model.type;

public enum AnythingType {
    NOTHING("Ninguno"),
    ANYTHING("Cualquiera"),
    ANY_LABORATORY("Cualquier laboratorio de LCC");

    private final String displayValue;

    AnythingType(String displayValue) {
        this.displayValue = displayValue;
    }

    public static String verify(String displayValue) {
        if (displayValue == null || displayValue.isEmpty() || displayValue.equals(NOTHING.displayValue) || displayValue.equals(ANYTHING.displayValue) || displayValue.equals(ANY_LABORATORY.displayValue)) {
            return null;
        }
        return displayValue;
    }
}
