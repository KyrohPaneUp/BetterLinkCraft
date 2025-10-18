package de.kyrohpaneup.betterlinkcraft.settings.optionEnums;

import de.kyrohpaneup.betterlinkcraft.utils.StringUtils;

public enum GGColor implements OptionEnum {

    GOLD("&6&lG&e&lG"),
    RED("&4&lG&c&lG"),
    GREEN("&2&lG&a&lG"),
    PURPLE("&5&lG&d&lG"),
    BLUE("&3&lG&b&lG"),
    DARK_BLUE("&1&lG&9&lG");

    String color;

    GGColor(String color) {
        this.color = color;
    }

    @Override
    public String getValue() {
        return color;
    }

    @Override
    public String getDisplay() {
        String str = name().toLowerCase();
        str = str.replaceAll("_", " ");
        str = StringUtils.capitalizeWords(str);
        return str;
    }
}
