package de.kyrohpaneup.betterlinkcraft.settings.optionEnums;

import de.kyrohpaneup.betterlinkcraft.utils.StringUtils;

public enum ShowReminders implements OptionEnum{
    HOLD,
    TOGGLE;

    @Override
    public String getValue() {
        return name().toUpperCase();
    }

    @Override
    public String getDisplay() {
        return StringUtils.capitalizeWords(name());
    }
}
