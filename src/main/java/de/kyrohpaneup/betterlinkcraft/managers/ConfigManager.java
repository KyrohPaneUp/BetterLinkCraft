package de.kyrohpaneup.betterlinkcraft.managers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.keybinds.controls.ControlPreset;
import de.kyrohpaneup.betterlinkcraft.mods.autotext.AutoText;
import de.kyrohpaneup.betterlinkcraft.mods.autotext.CustomCommand;
import de.kyrohpaneup.betterlinkcraft.mods.stratreminders.StratReminder;
import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {

    private File blcFile;
    private final String profileFileName = "Profiles";
    private KeybindManager keybindManager;
    private TimerManager timerManager;
    private AutoTextManager autoTextManager;
    private CustomCommandManager customCommandManager;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Strat Reminders
    private File srFile;
    private final Map<String, StratReminder> remindersById = new ConcurrentHashMap<>();
    private final Map<String, List<StratReminder>> remindersByMap = new ConcurrentHashMap<>();

    public void init() {
        this.keybindManager = BetterLinkCraft.INSTANCE.getKeybindManager();
        this.timerManager = BetterLinkCraft.INSTANCE.getTimerManager();
        this.autoTextManager = BetterLinkCraft.INSTANCE.getAutoTextManager();
        this.customCommandManager = BetterLinkCraft.INSTANCE.getCustomCommandManager();
        File mcDir = Minecraft.getMinecraft().mcDataDir;
        blcFile = new File(mcDir, BetterLinkCraft.NAME);
        if (!blcFile.exists()) {
            blcFile.mkdirs();
        }
        File file = new File(blcFile, profileFileName);
        if (!file.exists()) return;
        srFile = new File(blcFile, "StratReminders.json");

        loadStratReminders();
        loadAllPresets();
        loadSpeedrunData();
        loadAutoTextData();
        loadCustomCommands();
    }

    public void createPreset(String name) {
        ControlPreset preset = new ControlPreset(name);
        for (KeyBinding kb : Minecraft.getMinecraft().gameSettings.keyBindings) {
            preset.addBinding(kb.getKeyDescription(), kb.getKeyCode());
        }
        preset.setSensitivity(Minecraft.getMinecraft().gameSettings.mouseSensitivity);
        savePreset(preset);
        keybindManager.applyPreset(preset);
    }

    public void savePreset(ControlPreset preset) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File folder = new File(blcFile, profileFileName);
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, preset.getName() + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(preset, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Optional<ControlPreset> result = KeybindManager.presets.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(preset.getName()))
                    .findFirst();
            if (!result.isPresent()) {
                KeybindManager.presets.add(preset);
            }
        }
    }

    public void renamePreset(ControlPreset preset, String name) {
        File folder = new File(blcFile, profileFileName);
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, preset.getName() + ".json");
        File newFile = new File(folder, name + ".json");

        if (newFile.exists()) return;
        boolean success = file.renameTo(newFile);
        if (success) {
            preset.setName(name);
            savePreset(preset);
        }

        BLCSettings.updateOption(Option.SELECTED_PRESET, preset.getName());
    }

    public void deletePreset(ControlPreset preset) {
        if (preset == null) return;
        File folder = new File(blcFile, profileFileName);
        if (!folder.exists()) return;

        File file = new File(folder, preset.getName() + ".json");
        if (!file.exists()) return;

        if (file.delete()) {
            List<ControlPreset> presets = KeybindManager.presets;
            presets.removeIf(p -> p.getName().equalsIgnoreCase(preset.getName()));

            if (KeybindManager.selectedPreset != null &&
                    KeybindManager.selectedPreset.getName().equals(preset.getName())) {

                if (!presets.isEmpty()) {
                    KeybindManager.selectedPreset = presets.get(0);
                    keybindManager.applyPreset(KeybindManager.selectedPreset);
                    BLCSettings.updateOption(Option.SELECTED_PRESET, KeybindManager.selectedPreset.getName());
                } else {
                    KeybindManager.selectedPreset = null;
                    BLCSettings.updateOption(Option.SELECTED_PRESET, "");
                }

            }
        }
    }


    public void loadAllPresets() {
        List<ControlPreset> presets = new ArrayList<>();
        File file = new File(blcFile, profileFileName);
        if (!file.exists()) return;

        File[] files = file.listFiles();
        if (files == null) return;

        for (File data : files) {
            ControlPreset preset = loadPreset(data);
            if (preset == null) continue;
            presets.add(preset);
        }

        Option selected = Option.SELECTED_PRESET;
        if (!selected.getStringValue().equalsIgnoreCase("")) {
            Optional<ControlPreset> foundPreset = presets.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(selected.getStringValue()))
                    .findFirst();

            if (foundPreset.isPresent()) {
                KeybindManager.selectedPreset = foundPreset.get();
            } else {
                if (!presets.isEmpty()) {
                    KeybindManager.selectedPreset = presets.get(0);
                } else {
                    KeybindManager.selectedPreset = null;
                }
            }
        }
        presets.sort(Comparator.comparing(ControlPreset::getName));
        KeybindManager.presets = presets;
    }

    public ControlPreset loadPreset(File file) {
        // File file = new File(blcFile, profileFileName + "/" + name + ".json");
        if (!file.exists()) return null;

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, ControlPreset.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updatePreset(ControlPreset preset) {
        if (preset == null) return;
        for (KeyBinding kb : Minecraft.getMinecraft().gameSettings.keyBindings) {
            if (kb.getKeyDescription() != null) {
                preset.addBinding(kb.getKeyDescription(), kb.getKeyCode());
            }
        }
        preset.setSensitivity(Minecraft.getMinecraft().gameSettings.mouseSensitivity);
        KeybindManager.presets.stream()
                .filter(p -> p != null && p.getName() != null && p.getName().equalsIgnoreCase(preset.getName()))
                .findFirst()
                .ifPresent(p -> {
                    KeybindManager.presets.remove(p);
                    KeybindManager.presets.add(p);
                    KeybindManager.presets.sort(Comparator.comparing(ControlPreset::getName));
                });
        savePreset(preset);
    }

    // -----------------------------------------
    // ----------- StratReminders --------------
    // -----------------------------------------
    public void saveReminder(StratReminder reminder) {
        if (reminder.getId() == null) {
            reminder.setId(UUID.randomUUID().toString());
        }

        if (remindersById.containsKey(reminder.getId())) {
            deleteReminder(reminder.getId());
        }

        remindersById.put(reminder.getId(), reminder);
        remindersByMap.computeIfAbsent(reminder.map, k -> new ArrayList<>()).add(reminder);
        saveReminderFile();
    }

    public void updateReminder(StratReminder reminder) {
        if (remindersById.containsKey(reminder.getId())) {
            saveReminder(reminder);
        }
    }

    public void deleteReminder(String id) {
        StratReminder reminder = remindersById.remove(id);
        if (reminder != null) {
            remindersByMap.get(reminder.map).remove(reminder);
            saveReminderFile();
        }
    }

    // --------------- Getter ---------------
    public StratReminder getReminder(String id) {
        return remindersById.get(id);
    }

    public List<StratReminder> getRemindersByMap(String map) {
        return Collections.unmodifiableList(
                remindersByMap.getOrDefault(map, Collections.emptyList())
        );
    }

    public Map<String, List<StratReminder>> getAllRemindersMap() {
        return Collections.unmodifiableMap(remindersByMap);
    }

    private void loadStratReminders() {
        if (!srFile.exists()) {
            saveReminderFile();
            return;
        }

        try (Reader reader = new FileReader(srFile)) {
            JsonObject data = GSON.fromJson(reader, JsonObject.class);
            data.entrySet().forEach(entry -> entry.getValue().getAsJsonArray().forEach(element -> {
                StratReminder reminder = GSON.fromJson(element, StratReminder.class);
                if (reminder.getId() == null) {
                    reminder.setId(UUID.randomUUID().toString());
                }
                remindersById.put(reminder.getId(), reminder);
                remindersByMap.computeIfAbsent(reminder.map, k -> new ArrayList<>())
                        .add(reminder);
            }));
        } catch (Exception e) {
            System.err.println("Failed to load reminders: " + e.getMessage());
        }
    }

    private synchronized void saveReminderFile() {
        try (Writer writer = new FileWriter(srFile)) {
            JsonObject data = new JsonObject();
            remindersByMap.forEach((map, reminders) -> {
                JsonArray array = new JsonArray();
                reminders.forEach(r -> array.add(GSON.toJsonTree(r)));
                data.add(map.toLowerCase(), array);
            });
            GSON.toJson(data, writer);
        } catch (Exception e) {
            System.err.println("Failed to save reminders: " + e.getMessage());
        }
    }

    // Speedrun Stuff
    String speedrunPath = "speedrun.json";

    public void saveSpeedrunSegments() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            FileWriter writer = new FileWriter(new File(blcFile, speedrunPath));
            //gson.toJson(timerManager.getSegments(), writer);
            gson.toJson(timerManager.getSpeedrunMaps(), writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadSpeedrunData() {
        try {
            Gson gson = new Gson();
            File file = new File(blcFile, speedrunPath);

            if (!file.exists()) {
                timerManager.setSpeedrunMaps(new HashMap<>());
                return;
            }

            FileReader reader = new FileReader(file);
            Type listType = new TypeToken<HashMap<String, List<String>>>(){}.getType();
            HashMap<String, List<String>> loadedMaps = gson.fromJson(reader, listType);
            reader.close();

            timerManager.setSpeedrunMaps(loadedMaps != null ? loadedMaps : new HashMap<>());

        } catch (IOException e) {
            System.err.println("Error loading File: " + e.getMessage());
            e.printStackTrace();
            timerManager.setSpeedrunMaps(new HashMap<>());
        }
    }

    // AutoText Stuff
    String autoTextPath = "autotext.json";

    public void saveAutoTexts() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            FileWriter writer = new FileWriter(new File(blcFile, autoTextPath));
            //gson.toJson(timerManager.getSegments(), writer);
            gson.toJson(autoTextManager.getAutoTexts(), writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadAutoTextData() {
        try {
            Gson gson = new Gson();
            File file = new File(blcFile, autoTextPath);

            if (!file.exists()) {
                return;
            }

            FileReader reader = new FileReader(file);
            Type listType = new TypeToken<List<AutoText>>(){}.getType();
            List<AutoText> loadedAutoTexts = gson.fromJson(reader, listType);
            reader.close();

            autoTextManager.setAutoTexts(loadedAutoTexts != null ? loadedAutoTexts : new ArrayList<>());

        } catch (IOException e) {
            System.err.println("Error loading File: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Custom Commands Stuff
    String customCommandsPath = "CustomCommands.json";

    public void saveCustomCommands() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            FileWriter writer = new FileWriter(new File(blcFile, customCommandsPath));
            gson.toJson(customCommandManager.getCustomCommands(), writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadCustomCommands() {
        try {
            Gson gson = new Gson();
            File file = new File(blcFile, customCommandsPath);

            if (!file.exists()) {
                return;
            }

            FileReader reader = new FileReader(file);
            Type listType = new TypeToken<List<CustomCommand>>(){}.getType();
            List<CustomCommand> loadedCommands = gson.fromJson(reader, listType);
            reader.close();

            customCommandManager.setCustomCommands(loadedCommands != null ? loadedCommands : new ArrayList<>());

        } catch (IOException e) {
            System.err.println("Error loading File: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
