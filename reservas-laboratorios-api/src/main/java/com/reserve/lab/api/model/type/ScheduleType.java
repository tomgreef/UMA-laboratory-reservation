package com.reserve.lab.api.model.type;


public enum ScheduleType {
    PREFERRED("Preferente"),
    ALTERNATIVE("Alternativo");

    private final String displayValue;

    ScheduleType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static String fromDisplayValue(String displayValue) {
        if (displayValue == null || displayValue.isEmpty()) {
            return null;
        }

        for (ScheduleType type : ScheduleType.values()) {
            if (type.displayValue.equals(displayValue)) {
                return type.name();
            }
        }
        throw new IllegalArgumentException("No existe el tipo de prioridad '" + displayValue + "'. Tendrás que modificar el código para añadirlo.");
    }
}
