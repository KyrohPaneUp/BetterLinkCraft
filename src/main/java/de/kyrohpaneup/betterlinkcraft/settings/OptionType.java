package de.kyrohpaneup.betterlinkcraft.settings;

public enum OptionType {
    INT {
        @Override
        public boolean isValidValue(Object value) {
            return value instanceof Integer;
        }
    },
    FLOAT {
        @Override
        public boolean isValidValue(Object value) {
            return value instanceof Float;
        }
    },
    DOUBLE {
        @Override
        public boolean isValidValue(Object value) { return value instanceof Double; }
    },
    STRING {
        @Override
        public boolean isValidValue(Object value) {
            return value instanceof String;
        }
    },
    ENUM {
        @Override
        public boolean isValidValue(Object value) {
            return value instanceof Enum;
        }
    },
    BOOLEAN {
        @Override
        public boolean isValidValue(Object value) {
            return value instanceof Boolean;
        }
    };

    public abstract boolean isValidValue(Object value);
}
