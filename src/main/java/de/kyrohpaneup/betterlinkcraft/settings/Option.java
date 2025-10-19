package de.kyrohpaneup.betterlinkcraft.settings;

import de.kyrohpaneup.betterlinkcraft.settings.optionEnums.GGColor;
import de.kyrohpaneup.betterlinkcraft.settings.optionEnums.OptionEnum;
import de.kyrohpaneup.betterlinkcraft.settings.optionEnums.ShowReminders;

public enum Option {

    HIDE_PRAC(OptionType.BOOLEAN, false, "Hide Practice Messages", "Hides both practice and unpractice messages"),
    HIDE_JOIN(OptionType.BOOLEAN, false, "Hide Join Messages", "Hides both join and leave messages"),
    // CHAT_FILTER(OptionType.BOOLEAN, true, "Chat Filter", "Prevents spam"),
    CLEAN_CHAT(OptionType.BOOLEAN, false, "Clean Chat", "Cleans up the LinkCraft chat"),
    GG_COLOR(OptionType.ENUM, GGColor.GOLD, "GG Color", "Set a custom /gg color"),
    INV_JAM(OptionType.BOOLEAN, true, "Inv Jam", "Enable Inv Jamming"),
    SELECTED_PRESET(OptionType.STRING, "", "Selected Preset", "Selected Control Preset"),
    SELECTED_SHEET(OptionType.STRING, "", "Selected StratSheet", "ID of the stratsheet"),
    SELECTED_SR_MAP(OptionType.STRING, "default", "Current Map", "This defines which StratReminders will be shown"),
    SHOW_REMINDERS(OptionType.ENUM, ShowReminders.HOLD, "Show Reminders", "HOLD -> Hold to key to show the reminders; TOGGLE -> press the key once to toggle seeing the reminders"),
    TIMER_ENABLED(OptionType.BOOLEAN, false, "Show Timer", "displays a timer in the top right which can be started/stopped via hotkeys"),
    SELECTED_SPEEDRUN_MAP(OptionType.STRING, "default", "Current Map", "This defines which Speedrun Segments will be used"),
    FULLBRIGHT_ENABLED(OptionType.BOOLEAN, false, "Full Bright", "Turns the gamma up a lot"),
    UGIS_PRAC_DELAY(OptionType.DOUBLE, 2.0, 0.0, 5.0, "Quick Practice Delay", "Delay before teleporting to your LC Location when using quick practice"),
    CUSTOM_GG_TEXT(OptionType.STRING, "&6&lG&e&lG", "Custom GG", "Custom GG");

    private final OptionType type;
    private Object value;
    private final Object defaultValue;
    private Object minValue;
    private Object maxValue;
    private final String name;
    private final String comment;

    Option(OptionType type, Object value, String name, String comment) {
        this.type = type;
        this.value = value;
        this.defaultValue = value;
        this.name = name;
        this.comment = comment;
    }

    Option(OptionType type, Object value, Object minValue, Object maxValue, String name, String comment) {
        this.type = type;
        this.value = value;
        this.defaultValue = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.name = name;
        this.comment = comment;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (type.isValidValue(value)) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Invalid value type for option " + this.name());
        }
    }

    public OptionType getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Object getMinValue() {
        return minValue;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public int getIntValue() {
        return (Integer) value;
    }

    public double getDoubleValue() {
        return (Double) value;
    }

    public float getFloatValue() {
        return (Float) value;
    }

    public boolean getBooleanValue() {
        return (Boolean) value;
    }

    public String getStringValue() {
        return (String) value;
    }

    public OptionEnum getEnumValue() {
        if (value instanceof OptionEnum) return (OptionEnum) value;
        return null;
    }

    public String getDisplayString() {
        // TODO: Add enum and default value
        switch (type) {
            case STRING:
                return name;
            case BOOLEAN:
                return value instanceof Boolean && (Boolean) value ? name + ": ON" : name + ": OFF";
            case FLOAT:
                return name + ": " + Math.round((Float) value * 100) + "%";
            case DOUBLE:
                return name + ": " + value + "s";
            case INT:
                return name + ": " + value;
            case ENUM:
                if (!(value instanceof OptionEnum)) return name;
                OptionEnum optionEnum = (OptionEnum) value;
                return name + ": " + optionEnum.getDisplay();
            default:
                return name + "Default";
        }
    }
}
