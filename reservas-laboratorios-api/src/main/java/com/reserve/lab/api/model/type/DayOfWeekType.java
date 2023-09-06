package com.reserve.lab.api.model.type;

public enum DayOfWeekType {
    MONDAY("Lunes"),
    TUESDAY("Martes"),
    WEDNESDAY("Miércoles"),
    THURSDAY("Jueves"),
    FRIDAY("Viernes"),
    SATURDAY("Sábado"),
    SUNDAY("Domingo");

    private final String displayValue;

    DayOfWeekType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static DayOfWeekType fromDisplayValue(String displayValue) {
        if (displayValue == null || displayValue.isEmpty()) {
            return null;
        }
        
        for (DayOfWeekType type : DayOfWeekType.values()) {
            if (type.displayValue.equals(displayValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No existe el día de la semana '" + displayValue + "'");
    }
}
