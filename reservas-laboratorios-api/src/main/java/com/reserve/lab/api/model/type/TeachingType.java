package com.reserve.lab.api.model.type;

public enum TeachingType {
    REGULATED("Docencia reglada");

    private final String displayValue;

    TeachingType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static String fromDisplayValue(String displayValue) {
        if (displayValue == null || displayValue.isEmpty()) {
            return null;
        }

        for (TeachingType type : TeachingType.values()) {
            if (type.displayValue.equals(displayValue)) {
                return type.name();
            }
        }
        throw new IllegalArgumentException("No existe el tipo de docencia '" + displayValue + "'. Tendrás que modificar el código para añadirlo.");
    }
}
