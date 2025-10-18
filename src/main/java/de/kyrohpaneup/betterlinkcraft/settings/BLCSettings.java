package de.kyrohpaneup.betterlinkcraft.settings;
import de.kyrohpaneup.betterlinkcraft.mods.impl.FullBright;
import de.kyrohpaneup.betterlinkcraft.settings.optionEnums.OptionEnum;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class BLCSettings {

    private static Configuration config;

    public static void init(File file) {
        File configFile = new File(file, "options.cfg");
        config = new Configuration(configFile);
        loadConfig();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void loadConfig() {
        try {
            config.addCustomCategoryComment("options", "Mod Options Configuration");

            for (Option option : Option.values()) {
                switch (option.getType()) {
                    case INT:
                        int intValue = config.getInt(
                                option.name(),
                                "options",
                                (Integer) option.getDefaultValue(),
                                (Integer) option.getMinValue(),
                                (Integer) option.getMaxValue(),
                                option.getComment()
                        );
                        option.setValue(intValue);
                        break;

                    case FLOAT:
                        float floatValue = config.getFloat(
                                option.name(),
                                "options",
                                (Float) option.getDefaultValue(),
                                (Float) option.getMinValue(),
                                (Float) option.getMaxValue(),
                                option.getComment()
                        );
                        option.setValue(floatValue);
                        break;

                    case DOUBLE:
                        double doubleValue = config.get(
                                "options",
                                option.name(),
                                (Double) option.getDefaultValue(),
                                option.getComment(),
                                (Double) option.getMinValue(),
                                (Double) option.getMaxValue()
                        ).getDouble();
                        option.setValue(doubleValue);
                        break;

                    case BOOLEAN:
                        boolean boolValue = config.getBoolean(
                                option.name(),
                                "options",
                                (Boolean) option.getDefaultValue(),
                                option.getComment()
                        );
                        option.setValue(boolValue);
                        break;

                    case STRING:
                        String stringValue = config.getString(
                                option.name(),
                                "options",
                                (String) option.getDefaultValue(),
                                option.getComment()
                        );
                        option.setValue(stringValue);
                        break;

                    case ENUM:
                        String enumValue = config.getString(
                                option.name(),
                                "options",
                                option.getDefaultValue().toString(),
                                option.getComment()
                        );
                        OptionEnum optionEnum = option.getEnumValue();
                        if (optionEnum == null) break;
                        Class<? extends OptionEnum> enumClass = optionEnum.getClass();

                        if (enumClass.isEnum()) {
                            try {
                                Class<? extends Enum> concreteEnumClass = (Class<? extends Enum>) enumClass;

                                Enum<?> enumFromString = Enum.valueOf(concreteEnumClass, enumValue);
                                OptionEnum result = (OptionEnum) enumFromString;

                                option.setValue(result);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Enum-Value '" + enumValue + "' does not exist in " + enumClass.getSimpleName());
                            }
                        }
                        break;
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading config: " + e.getMessage());
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static void saveConfig() {
        if (config != null) {
            for (Option option : Option.values()) {
                Property prop = config.get("options", option.name(), option.getDefaultValue().toString());
                prop.setValue(option.getValue().toString());
                prop.comment = option.getComment();
            }
            config.save();
        }
    }

    public static void updateOption(Option option, Object newValue) {
        option.setValue(newValue);
        saveConfig();

        if (option == Option.FULLBRIGHT_ENABLED && newValue instanceof Boolean) {
            boolean b = (boolean) newValue;
            if (b) {
                FullBright.enable();
            } else {
                FullBright.disable();
            }
        }
    }

    public static void switchOption(Option option) {
        switch (option.getType()) {
            case BOOLEAN:
                updateOption(option, !option.getBooleanValue());
                break;
            case ENUM:
                if (!(option.getValue() instanceof OptionEnum)) return;
                OptionEnum optionEnum = (OptionEnum) option.getValue();
                if (optionEnum instanceof Enum) {
                    optionEnum = nextOption(optionEnum);
                    updateOption(option, optionEnum);
                }
                break;
        }
    }

    public static OptionEnum nextOption(OptionEnum current) {
        Class<? extends Enum<?>> enumClass = ((Enum<?>) current).getDeclaringClass();
        Enum<?>[] values = enumClass.getEnumConstants();

        int ordinal = ((Enum<?>) current).ordinal();
        int nextIndex = (ordinal + 1) % values.length;

        return (OptionEnum) values[nextIndex];
    }
}

