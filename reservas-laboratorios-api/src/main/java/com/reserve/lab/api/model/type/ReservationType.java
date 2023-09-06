package com.reserve.lab.api.model.type;


public enum ReservationType {
    WEEKLY("Reserva semanal"),
    DAY("Reserva de lista de días"),
    CANCELLATION("Cancelación solicitud");

    private final String displayValue;

    ReservationType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static String fromDisplayValue(String displayValue) {
        if (displayValue == null || displayValue.isEmpty()) {
            return null;
        }

        for (ReservationType type : ReservationType.values()) {
            if (type.displayValue.equals(displayValue)) {
                return type.name();
            }
        }
        throw new IllegalArgumentException("No existe el tipo de reserva '" + displayValue + "'. Tendrás que modificar el código para añadirlo.");
    }
}
